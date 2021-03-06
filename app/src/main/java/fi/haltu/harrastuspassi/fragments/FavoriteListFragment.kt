package fi.haltu.harrastuspassi.fragments

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.activities.HobbyDetailActivity
import fi.haltu.harrastuspassi.activities.MainActivity
import fi.haltu.harrastuspassi.adapters.HobbyEventListAdapter
import fi.haltu.harrastuspassi.models.Filters
import fi.haltu.harrastuspassi.models.HobbyEvent
import fi.haltu.harrastuspassi.utils.loadFavorites
import fi.haltu.harrastuspassi.utils.loadFilters
import fi.haltu.harrastuspassi.utils.verifyAvailableNetwork
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URL


class FavoriteListFragment : Fragment() {
    private lateinit var listView: RecyclerView
    private var hobbyEventArrayList = ArrayList<HobbyEvent>()
    private var filteredArrayList = ArrayList<HobbyEvent>()
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private lateinit var findFavoritesButton: Button
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var favorites: HashSet<Int>
    private lateinit var filters: Filters

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_favorite_list, container, false)
        val hobbyEventListAdapter =
            HobbyEventListAdapter(hobbyEventArrayList) { hobbyEvent: HobbyEvent, hobbyImage: ImageView ->
                hobbyItemClicked(
                    hobbyEvent,
                    hobbyImage
                )
            }
        refreshLayout = view.findViewById(R.id.swipe_refresh_list)

        refreshLayout.setOnRefreshListener {
            filters = loadFilters(this.activity!!)
            GetHobbyEvents(null).execute()
        }

        filters = loadFilters(this.activity!!)
        favorites = loadFavorites(this.activity!!)

        val navigateToHobbyList = View.OnClickListener {
            var mainActivity = context as MainActivity
            mainActivity.performHobbyEventListClick()
        }

        progressBar = view.findViewById(R.id.progressbar)
        progressText = view.findViewById(R.id.progress_text)
        progressText.setOnClickListener(navigateToHobbyList)

        findFavoritesButton = view.findViewById(R.id.find_favorites_button)
        findFavoritesButton.setOnClickListener(navigateToHobbyList)
        findFavoritesButton.visibility = View.INVISIBLE

        listView = view.findViewById(R.id.list_view)
        listView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = hobbyEventListAdapter
        }

        GetHobbyEvents(null).execute()
        return view
    }


    private fun hobbyItemClicked(hobbyEvent: HobbyEvent, hobbyImage: ImageView) {
        val intent = Intent(context, HobbyDetailActivity::class.java)

        intent.putExtra("EXTRA_HOBBY", hobbyEvent)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)

        val sharedView: View = hobbyImage
        val transition = getString(R.string.item_detail)
        val transitionActivity =
            ActivityOptions.makeSceneTransitionAnimation(this.activity, sharedView, transition)
        startActivity(intent, transitionActivity.toBundle())
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        //hidden == false is almost same than onResume
        if (!hidden) {
            favorites = loadFavorites(this.activity!!)
            updateListView(listView, filterFavorites(hobbyEventArrayList, favorites))
        }
    }

    override fun onResume() {
        super.onResume()
        favorites = loadFavorites(this.activity!!)
        updateListView(listView, filterFavorites(hobbyEventArrayList, favorites))
    }

    companion object {
        const val ERROR = "error"
        const val NO_INTERNET = "no_internet"
    }

    internal inner class GetHobbyEvents(url: String? = null) : AsyncTask<Void, Void, String>() {
        private val fetchUrl = url
        override fun onPreExecute() {
            super.onPreExecute()
            progressBar.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg params: Void?): String {
            return try {
                Log.d("latitude", "${filters.latitude}")
                if(fetchUrl.isNullOrEmpty()) {
                    URL(getString(R.string.API_URL) + "hobbyevents/?include=location_detail&include=hobby_detail&include=organizer_detail&ordering=start_date").readText()
                } else {
                    URL(fetchUrl).readText()
                }


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

            var nextHobbyEventsURL = ""

            when (result) {
                ERROR -> {
                    progressText.visibility = View.VISIBLE
                    progressText.text = getString(R.string.error_try_again_later)
                }
                NO_INTERNET -> {
                    progressText.text = getString(R.string.error_no_internet)
                }
                else -> {
                    try {
                        val results = JSONObject(result)
                        val mJsonArray = results.getJSONArray("results")
                        nextHobbyEventsURL = results.getString("next")
                        for (i in 0 until mJsonArray.length()) {
                            val sObject = mJsonArray.get(i).toString()
                            val hobbyObject = JSONObject(sObject)
                            val hobbyEvent = HobbyEvent(hobbyObject)

                            hobbyEventArrayList.add(hobbyEvent)
                        }

                        if (hobbyEventArrayList.size == 0) {
                            progressText.visibility = View.VISIBLE
                            progressText.text = getString(R.string.error_no_hobby_events)
                        } else {
                            progressText.visibility = View.INVISIBLE
                            listView.adapter!!.notifyDataSetChanged()
                        }
                    } catch (e: JSONException) {
                        progressText.text = getString(R.string.error_no_hobby_events)
                    }
                }
            }
            progressBar.visibility = View.INVISIBLE
            refreshLayout.isRefreshing = false
            if(nextHobbyEventsURL.isNullOrEmpty() || nextHobbyEventsURL == "null") {
                updateListView(listView, filterFavorites(hobbyEventArrayList, favorites))
            } else {
                GetHobbyEvents(nextHobbyEventsURL).execute()
            }
        }
    }

    private fun updateListView(listView: RecyclerView, hobbyEventArrayList: ArrayList<HobbyEvent>) {
        if (hobbyEventArrayList.isEmpty()) {
            progressText.visibility = View.VISIBLE
            progressText.text = getString(R.string.no_favorites)
            findFavoritesButton.visibility = View.VISIBLE
        } else {
            progressText.visibility = View.INVISIBLE
            findFavoritesButton.visibility = View.INVISIBLE
        }
        val hobbyEventListAdapter =
            HobbyEventListAdapter(hobbyEventArrayList) { hobbyEvent: HobbyEvent, hobbyImage: ImageView ->
                hobbyItemClicked(
                    hobbyEvent,
                    hobbyImage
                )
            }
        listView.adapter = hobbyEventListAdapter
    }

    private fun filterFavorites(
        hobbyEvents: ArrayList<HobbyEvent>,
        favorites: HashSet<Int>
    ): ArrayList<HobbyEvent> {
        var filteredList = ArrayList<HobbyEvent>()
        val hobbyEventSet: Set<HobbyEvent> = hobbyEvents.toSet()

        for (event in hobbyEventSet) {
            if (favorites.contains(event.id)) {
                filteredList.add(event)
            }
        }
        return filteredList
    }
}