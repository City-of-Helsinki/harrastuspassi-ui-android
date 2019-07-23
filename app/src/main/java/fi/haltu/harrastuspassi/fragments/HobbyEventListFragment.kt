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
import fi.haltu.harrastuspassi.utils.InternetCheck
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import android.widget.Toast
import fi.haltu.harrastuspassi.activities.HobbyDetailActivity


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
        val hobbyEventList = HobbyEventListAdapter(hobbyEventArrayList, {hobby: HobbyEvent -> hobbyItemClicked(hobby)})
        progressBar = view.findViewById(R.id.progressbar)
        progressText = view.findViewById(R.id.progress_text)
        getHobbyEvents().execute()
        listView = view.findViewById(R.id.list_view)
        listView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = hobbyEventList
        }
        return view
    }

    private fun hobbyItemClicked(hobby: HobbyEvent) {
        val intent = Intent(context, HobbyDetailActivity::class.java)

        intent.putExtra("EXTRA_HOBBY", hobby)
        startActivity(intent)

        val toast = Toast.makeText(context, hobby.title, Toast.LENGTH_SHORT)
        toast.show()
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
                URL("https://app.harrastuspassi.fi/mobile-api/hobbies/").readText()
            } catch (e: IOException) {
                return when (!InternetCheck().verifyAvailableNetwork(activity!!)) {
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
                        val mItemObject = JSONObject(sObject)

                        val title = mItemObject.getString("name")
                        val place = mItemObject.getString("location")
                        val dateTime = mItemObject.getString("day_of_week")
                        val image = mItemObject.getString("cover_image")
                        val hobbyEvent = HobbyEvent(title, place, dateTime, image)

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