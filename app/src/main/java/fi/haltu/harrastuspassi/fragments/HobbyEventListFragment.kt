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
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.adapters.HobbyEventListAdapter
import fi.haltu.harrastuspassi.models.HobbyEvent
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import fi.haltu.harrastuspassi.activities.HobbyDetailActivity
import fi.haltu.harrastuspassi.models.Location
import fi.haltu.harrastuspassi.utils.getLatLon
import fi.haltu.harrastuspassi.utils.getLocation
import fi.haltu.harrastuspassi.utils.verifyAvailableNetwork


class HobbyEventListFragment : Fragment() {
    private lateinit var listView: RecyclerView
    private var hobbyEventArrayList = ArrayList<HobbyEvent>()
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_hobby_event_list, container, false)
        val hobbyEventListAdapter = HobbyEventListAdapter(hobbyEventArrayList) { hobby: HobbyEvent -> hobbyItemClicked(hobby)}
        progressBar = view.findViewById(R.id.progressbar)
        progressText = view.findViewById(R.id.progress_text)
        getHobbyEvents().execute()
        listView = view.findViewById(R.id.list_view)
        listView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = hobbyEventListAdapter
        }

        return view
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
                URL("http://10.0.1.229:8000/mobile-api/hobbies/").readText()
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
                    val mJsonArray = JSONArray(result)

                    for (i in 0 until mJsonArray.length()) {
                        val sObject = mJsonArray.get(i).toString()
                        val hobbyObject = JSONObject(sObject)

                        val id = hobbyObject.getInt("id")
                        val name = hobbyObject.getString("name")
                        val startDayOfWeek = hobbyObject.getString("start_day_of_week")
                        val coverImage = hobbyObject.getString("cover_image")
                        val hobbyEvent = HobbyEvent()

                        val locationObject = getLocation(hobbyObject, "location")
                        val hobbyLocation = Location()
                        if (locationObject != null) {
                            val locationName = locationObject.getString("name")
                            val locationAddress = locationObject.getString("address")
                            val locationZipCode = locationObject.getString("zip_code")
                            val locationCity = locationObject.getString("city")
                            val locationLat = getLatLon(locationObject, "lat")
                            val locationLon = getLatLon(locationObject, "lon")

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
                            this.dateTime = startDayOfWeek
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
                }
            }
        }
    }
}