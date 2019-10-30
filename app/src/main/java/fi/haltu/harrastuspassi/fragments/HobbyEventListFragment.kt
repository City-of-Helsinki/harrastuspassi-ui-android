package fi.haltu.harrastuspassi.fragments

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.adapters.HobbyEventListAdapter
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import fi.haltu.harrastuspassi.activities.HobbyDetailActivity
import fi.haltu.harrastuspassi.models.Filters
import fi.haltu.harrastuspassi.models.HobbyEvent
import fi.haltu.harrastuspassi.utils.*
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
        setHasOptionsMenu(true)
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
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)

        val sharedView: View = hobbyImage
        val transition = getString(R.string.item_detail)
        val transitionActivity = ActivityOptions.makeSceneTransitionAnimation(this.activity, sharedView, transition)
        startActivity(intent, transitionActivity.toBundle())
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden) {
            updateList()
        }
        //if hidden = false, it's almost same than onResume
    }

    override fun onResume() {
        super.onResume()
        updateList()
    }

    private fun updateList() {
        filters = loadFilters(this.activity!!)
        if(!filters.isListUpdated) {
            GetHobbyEvents().execute()
            filters.isListUpdated = true
            saveFilters(filters, this.activity!!)
        }
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

            hobbyEventArrayList.clear()

            when (result) {
                ERROR -> {
                    progressText.visibility = View.VISIBLE
                    this@HobbyEventListFragment.progressText.text = getString(R.string.error_try_again_later)
                }
                NO_INTERNET -> {
                    progressText.text = getString(R.string.error_no_internet)
                }
                else -> {
                    try {
                        val mJsonArray = JSONArray(result)

                        for (i in 0 until mJsonArray.length()) {
                            val sObject = mJsonArray.get(i).toString()
                            val hobbyObject = JSONObject(sObject)
                            val hobbyEvent = HobbyEvent(hobbyObject)

                            hobbyEventArrayList.add(hobbyEvent)
                        }

                        val hobbyEventSet: Set<HobbyEvent> = hobbyEventArrayList.toSet()
                        hobbyEventArrayList.clear()
                        for (hobbyEvent in hobbyEventSet) {
                            hobbyEventArrayList.add(hobbyEvent)
                        }


                        if(hobbyEventArrayList.size == 0) {
                            progressText.visibility = View.VISIBLE
                            progressText.text = getString(R.string.error_no_hobby_events)
                        } else {
                            progressText.visibility = View.INVISIBLE
                            listView.adapter!!.notifyDataSetChanged()
                        }
                    } catch(e: JSONException) {
                            progressText.text = getString(R.string.error_no_hobby_events)
                    }
                }
            }
            progressBar.visibility = View.INVISIBLE
            refreshLayout.isRefreshing = false
            updateListView(listView, hobbyEventArrayList)

        }

        private fun updateListView(listView: RecyclerView, hobbyEventArrayList: ArrayList<HobbyEvent>) {
            val hobbyEventListAdapter = HobbyEventListAdapter(hobbyEventArrayList) { hobbyEvent: HobbyEvent, hobbyImage: ImageView -> hobbyItemClicked(hobbyEvent, hobbyImage)}
            listView.adapter = hobbyEventListAdapter
        }
    }
}