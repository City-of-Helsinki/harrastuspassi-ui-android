package fi.haltu.harrastuspassi.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.adapters.HobbyEventListAdapter
import fi.haltu.harrastuspassi.models.HobbyEvent
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import fi.haltu.harrastuspassi.activities.HobbyDetailActivity
import fi.haltu.harrastuspassi.models.Filters
import fi.haltu.harrastuspassi.models.Location
import fi.haltu.harrastuspassi.utils.getOptionalDouble
import fi.haltu.harrastuspassi.utils.getOptionalJSONObject
import fi.haltu.harrastuspassi.utils.loadFilters
import fi.haltu.harrastuspassi.utils.verifyAvailableNetwork
import org.json.JSONException


class HobbyEventListFragment : Fragment() {
    private lateinit var listView: RecyclerView
    private var hobbyEventArrayList = ArrayList<HobbyEvent>()
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private var filters: Filters = Filters()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_hobby_event_list, container, false)
        val hobbyEventListAdapter = HobbyEventListAdapter(hobbyEventArrayList) { hobby: HobbyEvent -> hobbyItemClicked(hobby)}
        progressBar = view.findViewById(R.id.progressbar)
        progressText = view.findViewById(R.id.progress_text)

        listView = view.findViewById(R.id.list_view)
        listView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = hobbyEventListAdapter
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        filters = loadFilters(this.activity!!)
        getHobbyEvents().execute()
        Toast.makeText(this.context,filters.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun hobbyItemClicked(hobby: HobbyEvent) {
        val intent = Intent(context, HobbyDetailActivity::class.java)

        intent.putExtra("EXTRA_HOBBY", hobby)
        startActivity(intent)
    }

    companion object {
        const val ERROR = "error"
        const val NO_INTERNET = "no_internet"
    }

    internal inner class getHobbyEvents : AsyncTask<Void, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            progressBar.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: Void?): String {
            return try {
                URL(getString(R.string.API_URL) + createQueryUrl(filters.categories)).readText()
            } catch (e: IOException) {
                return when (!verifyAvailableNetwork(activity!!)) {
                    true -> NO_INTERNET
                    else -> ERROR
                }
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            when (result) {
                ERROR -> {
                    progressBar.visibility = View.INVISIBLE
                    this@HobbyEventListFragment.progressText.text = "Jokin meni vikaan. Kokeile myöhemmin uudelleen."
                }
                NO_INTERNET -> {
                    progressBar.visibility = View.INVISIBLE
                    progressText.text = "Ei verkkoyhteyttä. Tarkista verkkoyhteys ja käynnistä sovellus uudelleen."
                }
                else -> {
                    try {
                        val mJsonArray = JSONArray(result)
                        hobbyEventArrayList.clear()
                        for (i in 0 until mJsonArray.length()) {
                            val sObject = mJsonArray.get(i).toString()
                            val hobbyObject = JSONObject(sObject)

                            val id = hobbyObject.getInt("id")
                            val name = hobbyObject.getString("name")
                            //val startDayOfWeek = hobbyObject.getString("start_day_of_week")
                            val coverImage = hobbyObject.getString("cover_image")
                            val hobbyEvent = HobbyEvent()

                            val locationObject = getOptionalJSONObject(hobbyObject, "location")
                            val hobbyLocation = Location()
                            if (locationObject != null) {
                                val locationName = locationObject.getString("name")
                                val locationAddress = locationObject.getString("address")
                                val locationZipCode = locationObject.getString("zip_code")
                                val locationCity = locationObject.getString("city")
                                val locationLat = getOptionalDouble(locationObject, "lat")
                                val locationLon = getOptionalDouble(locationObject, "lon")

                                hobbyLocation.apply {
                                    this.name = locationName
                                    this.address = locationAddress
                                    this.zipCode = locationZipCode
                                    this.city = locationCity
                                    this.lat = locationLat
                                    this.lon = locationLon
                                }
                            }

                            hobbyEvent.apply {
                                this.id = id
                                this.title = name
                                this.place = hobbyLocation
                                //    this.dateTime = startDayOfWeek
                                this.imageUrl = coverImage
                            }

                            hobbyEventArrayList.add(hobbyEvent)
                        }

                        if(hobbyEventArrayList.size == 0) {
                            progressBar.visibility = View.INVISIBLE
                            progressText.text = "Ei harrastustapahtumia."
                        } else {
                            progressText.visibility = View.INVISIBLE
                            progressBar.visibility = View.INVISIBLE
                            listView.adapter!!.notifyDataSetChanged()
                        }
                    } catch(e: JSONException) {
                            progressBar.visibility = View.INVISIBLE
                            progressText.text = "Ei harrastustapahtumia."
                    }
                }
            }
        }
    }

    fun createQueryUrl(categories: HashSet<Int>): String {
        var query = "/hobbies/"
        var arrayList = categories.toArray()

        if(arrayList.isNotEmpty()) {
            query += "?"
            for (i in 0 until arrayList.size) {
                val categoryId = arrayList[i]
                query += if (i == arrayList.indexOfLast{true}) {
                    "category=$categoryId"
                } else {
                    "category=$categoryId&"
                }
            }
        }

        return query
    }
}