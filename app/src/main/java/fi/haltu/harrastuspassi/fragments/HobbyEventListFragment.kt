package fi.haltu.harrastuspassi.fragments

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
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
    private lateinit var refreshLayout: SwipeRefreshLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.fragment_hobby_event_list, container, false)
        val hobbyEventListAdapter = HobbyEventListAdapter(hobbyEventArrayList) { hobbyEvent: HobbyEvent, hobbyImage: ImageView -> hobbyItemClicked(hobbyEvent, hobbyImage)}

        refreshLayout = view.findViewById(R.id.swipe_refresh_list)

        refreshLayout.setOnRefreshListener {
            GetHobbyEvents().execute()
        }

        progressBar = view.findViewById(R.id.progressbar)
        progressText = view.findViewById(R.id.progress_text)

        listView = view.findViewById(R.id.list_view)
        listView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = hobbyEventListAdapter
        }
        filters = loadFilters(this.activity!!)
        GetHobbyEvents().execute()
        return view
    }

    private fun hobbyItemClicked(hobbyEvent: HobbyEvent, hobbyImage: ImageView) {
        val intent = Intent(context, HobbyDetailActivity::class.java)

        intent.putExtra("EXTRA_HOBBY", hobbyEvent)
        val sharedView: View = hobbyImage
        val transition = getString(R.string.item_detail)
        val transitionActivity = ActivityOptions.makeSceneTransitionAnimation(this.activity, sharedView, transition)
        startActivity(intent, transitionActivity.toBundle())
    }

    companion object {
        const val ERROR = "error"
        const val NO_INTERNET = "no_internet"
    }


    internal inner class GetHobbyEvents : AsyncTask<Void, Void, String>() {

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
                    this@HobbyEventListFragment.progressText.text = getString(R.string.error_try_again_later)
                }
                NO_INTERNET -> {
                    progressBar.visibility = View.INVISIBLE
                    progressText.text = getString(R.string.error_no_internet)
                }
                else -> {
                    try {
                        val mJsonArray = JSONArray(result)
                        hobbyEventArrayList.clear()
                        for (i in 0 until mJsonArray.length()) {
                            val sObject = mJsonArray.get(i).toString()
                            val hobbyObject = JSONObject(sObject)
                            val hobbyEvent = HobbyEvent(hobbyObject)
                            hobbyEventArrayList.add(hobbyEvent)
                        }

                        if(hobbyEventArrayList.size == 0) {
                            progressBar.visibility = View.INVISIBLE
                            progressText.text = getString(R.string.error_no_hobby_events)
                        } else {
                            progressText.visibility = View.INVISIBLE
                            progressBar.visibility = View.INVISIBLE
                            listView.adapter!!.notifyDataSetChanged()
                        }
                    } catch(e: JSONException) {
                            progressBar.visibility = View.INVISIBLE
                            progressText.text = getString(R.string.error_no_hobby_events)
                    }
                }
            }
            refreshLayout.isRefreshing = false
        }
    }

    fun createQueryUrl(filters: Filters): String {
        var query = "hobbyevents/?include=hobby_detail"
        val categoryArrayList = filters.categories.toArray()
        val weekDayArrayList = filters.dayOfWeeks.toArray()
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