package fi.haltu.harrastuspassi.fragments.home

import android.app.ActivityOptions
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.activities.HobbyDetailActivity
import fi.haltu.harrastuspassi.adapters.HobbyHorizontalListAdapter
import fi.haltu.harrastuspassi.models.Filters
import fi.haltu.harrastuspassi.models.HobbyEvent
import fi.haltu.harrastuspassi.utils.createHobbyEventQueryUrl
import fi.haltu.harrastuspassi.utils.idToWeekDay
import fi.haltu.harrastuspassi.utils.loadFilters
import fi.haltu.harrastuspassi.utils.verifyAvailableNetwork
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URL

class HomeHobbiesFragment : Fragment() {
    lateinit var rootView: View
    lateinit var popularHobbyList: RecyclerView
    lateinit var promotedTitle: TextView
    lateinit var userHobbyList: RecyclerView
    lateinit var userHobbyEventsText: TextView
    private var filters = Filters()
    var hobbyEventArrayList = ArrayList<HobbyEvent>()

    companion object {
        const val ERROR = "error"
        const val NO_INTERNET = "no_internet"
        const val MAX_ITEM_AMOUNT = 5 //max amount of hobbies to show in recyclerViews
        const val MIN_ITEM_AMOUNT = 1 //min amount of hobbies to show in recyclerViews
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_home_hobbies, container, false)
        setHasOptionsMenu(true)
        //Loads filters to fetch hobbies filtered by locations
        filters = loadFilters(this.activity!!)

        promotedTitle = rootView.findViewById<TextView>(R.id.home_promoted_title)
        //PROMOTIONS LISTS
        popularHobbyList = rootView.findViewById(R.id.home_popular_hobby_list)

        userHobbyList = rootView.findViewById(R.id.home_user_hobby_list)
        userHobbyEventsText = rootView.findViewById(R.id.user_hobby_text_label)
        GetHobbyEvents().execute()
        return rootView
    }

    private fun setHobbyEvents(parentView: View, hobbyEventList: ArrayList<HobbyEvent>) {
        if (hobbyEventList.isNotEmpty()) {
            var popularHobbies = ArrayList<HobbyEvent>()
            popularHobbies.addAll(hobbyEventList.shuffled())

            //PROMOTED HOBBY
            var promotedHobby = popularHobbies[0]
            popularHobbies.removeAt(0)
            //IMAGE
            var imageView = parentView.findViewById<ImageView>(R.id.home_promoted_image)
            Picasso.with(this.context)
                .load(promotedHobby.hobby.imageUrl)
                .placeholder(R.drawable.harrastuspassi_lil_kel)
                .error(R.drawable.harrastuspassi_lil_kel)
                .into(imageView)
            //TITLE
            promotedTitle.text = promotedHobby.hobby.name
            //DESCRIPTION
            parentView.findViewById<TextView>(R.id.home_promoted_description).text =
                promotedHobby.hobby.description
            //DURATION
            parentView.findViewById<TextView>(R.id.home_promoted_duration).text =
                "${idToWeekDay(promotedHobby.startWeekday, this.activity!!)}"

            parentView.findViewById<CardView>(R.id.home_promoted_hobby).setOnClickListener {
                hobbyItemClicked(promotedHobby, imageView)
            }
            //POPULAR PROMOTION LIST
            when {
                popularHobbies.size > MAX_ITEM_AMOUNT -> {
                    popularHobbyList.apply {
                        this.layoutManager =
                            LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
                        this.adapter =
                            HobbyHorizontalListAdapter(popularHobbies.subList(0, MAX_ITEM_AMOUNT))
                            { hobbyEvent: HobbyEvent, image: ImageView ->
                                hobbyItemClicked(
                                    hobbyEvent,
                                    image
                                )
                            }
                    }
                }
                popularHobbies.size > MIN_ITEM_AMOUNT -> popularHobbyList.apply {
                    this.layoutManager =
                        LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
                    this.adapter = HobbyHorizontalListAdapter(popularHobbies)
                    { hobbyEvent: HobbyEvent, image: ImageView ->
                        hobbyItemClicked(
                            hobbyEvent,
                            image
                        )
                    }
                }
                else -> {
                    popularHobbyList.visibility = View.INVISIBLE
                }
            }
            //USER HOBBIES LIST
            when {
                hobbyEventList.size > MAX_ITEM_AMOUNT -> userHobbyList.apply {
                    this.layoutManager =
                        LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
                    this.adapter =
                        HobbyHorizontalListAdapter(hobbyEventList.subList(0, MAX_ITEM_AMOUNT))
                        { hobbyEvent: HobbyEvent, image: ImageView ->
                            hobbyItemClicked(
                                hobbyEvent,
                                image
                            )
                        }
                }
                hobbyEventList.size >= MIN_ITEM_AMOUNT -> userHobbyList.apply {
                    this.layoutManager =
                        LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
                    this.adapter =
                        HobbyHorizontalListAdapter(hobbyEventList) { hobbyEvent: HobbyEvent, image: ImageView ->
                            hobbyItemClicked(
                                hobbyEvent,
                                image
                            )
                        }
                }
                else -> {
                    userHobbyList.visibility = View.INVISIBLE
                }
            }
        }
    }

    fun updateContent() {
        filters = loadFilters(activity!!)
        GetHobbyEvents().execute()
    }

    private fun hobbyItemClicked(hobbyEvent: HobbyEvent, imageView: ImageView) {
        val intent = Intent(context, HobbyDetailActivity::class.java)

        intent.putExtra("EXTRA_HOBBY", hobbyEvent)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)

        val sharedView: View = imageView
        val transition = getString(R.string.item_detail)
        val transitionActivity =
            ActivityOptions.makeSceneTransitionAnimation(this.activity, sharedView, transition)
        startActivity(intent, transitionActivity.toBundle())
    }


    internal inner class GetHobbyEvents : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void?): String {
            //creates new Filters object to use createHobbyEventQueryUrl -function without having other filters
            var locationFilter = Filters()
            locationFilter.longitude = filters.longitude
            locationFilter.latitude = filters.latitude
            return try {
                URL(getString(R.string.API_URL) + createHobbyEventQueryUrl(locationFilter)).readText()

            } catch (e: IOException) {
                return when (!verifyAvailableNetwork(activity!!)) {
                    true -> NO_INTERNET
                    else -> ERROR
                }
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            when (result) {
                ERROR -> {
                    promotedTitle.text = getString(R.string.error_try_again_later)
                    userHobbyEventsText.visibility = View.INVISIBLE

                }
                NO_INTERNET -> {
                    promotedTitle.text = activity!!.getString(R.string.error_no_internet)
                    userHobbyEventsText.visibility = View.INVISIBLE

                    //progressText.text = getString(R.string.error_no_internet)
                }
                else -> {
                    try {
                        val results = JSONObject(result)
                        val mJsonArray = results.getJSONArray("results")

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

                        setHobbyEvents(rootView, hobbyEventArrayList)

                        if (hobbyEventArrayList.size != 0) {
                            popularHobbyList.adapter!!.notifyDataSetChanged()
                        }
                    } catch (e: JSONException) {
                        userHobbyEventsText.visibility = View.INVISIBLE
                        //progressText.text = getString(R.string.error_no_hobby_events)
                    }
                }
            }
        }
    }
}