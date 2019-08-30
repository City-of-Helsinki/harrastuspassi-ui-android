package fi.haltu.harrastuspassi.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.adapters.HobbyEventListAdapter
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import fi.haltu.harrastuspassi.activities.HobbyDetailActivity
import fi.haltu.harrastuspassi.models.Filters
import fi.haltu.harrastuspassi.models.HobbyEvent
import fi.haltu.harrastuspassi.utils.loadFilters
import fi.haltu.harrastuspassi.utils.minutesToTime
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
        val hobbyEventListAdapter = HobbyEventListAdapter(hobbyEventArrayList) { hobbyEvent: HobbyEvent -> hobbyItemClicked(hobbyEvent)}
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

    private fun hobbyItemClicked(hobbyEvent: HobbyEvent) {
        val intent = Intent(context, HobbyDetailActivity::class.java)

        intent.putExtra("EXTRA_HOBBY", hobbyEvent)
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
                URL(getString(R.string.API_URL) + createQueryUrl(filters)).readText()
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
                            var hobbyEvent = HobbyEvent(hobbyObject)
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

    fun createQueryUrl(filters: Filters): String {
        var query = "hobbyevents/?include=hobby_detail"
        var categoryArrayList = filters.categories.toArray()
        var weekDayArrayList = filters.dayOfWeeks.toArray()
        if(categoryArrayList.isNotEmpty()) {
            query += "&"
            for (i in 0 until categoryArrayList.size) {
                val categoryId = categoryArrayList[i]
                query += if (i == categoryArrayList.indexOfLast{ true }) {
                    "category=$categoryId"
                } else {
                    "category=$categoryId&"
                }
            }
        }
        if(weekDayArrayList.isNotEmpty()) {
            query += "&"
            for(i in 0 until weekDayArrayList.size) {
                val weekId = weekDayArrayList[i]
                query += if(i == weekDayArrayList.indexOfLast { true }) {
                    "start_weekday=$weekId"
                } else {
                    "start_weekday=$weekId&"
                }
            }
        }
        query += "&start_time_from=${minutesToTime(filters.startTimeFrom)}"
        query += "&start_time_to=${minutesToTime(filters.startTimeTo)}"

        return query
    }
}