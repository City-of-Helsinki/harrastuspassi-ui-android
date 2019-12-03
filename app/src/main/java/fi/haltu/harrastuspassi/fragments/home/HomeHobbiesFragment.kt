package fi.haltu.harrastuspassi.fragments.home

import android.app.ActivityOptions
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.activities.HobbyDetailActivity
import fi.haltu.harrastuspassi.adapters.HobbyHorizontalListAdapter
import fi.haltu.harrastuspassi.models.HobbyEvent
import fi.haltu.harrastuspassi.utils.idToWeekDay
import fi.haltu.harrastuspassi.utils.verifyAvailableNetwork
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URL

class HomeHobbiesFragment : Fragment() {
    lateinit var rootView: View
    lateinit var popularHobbyList: RecyclerView
    lateinit var title: TextView
    //lateinit var userHobbyList: RecyclerView
    var hobbyEventArrayList = ArrayList<HobbyEvent>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_home_hobbies, container, false)
        setHasOptionsMenu(true)
        title = rootView.findViewById<TextView>(R.id.home_promoted_title)
        //PROMOTIONS LISTS
        popularHobbyList = rootView.findViewById(R.id.home_popular_hobby_list)
        //userHobbyList = view.findViewById(R.id.home_user_hobby_list)
        GetHobbyEvents().execute()
        return rootView
    }

    private fun setHobbyEvents(parentView: View, hobbyEventList: ArrayList<HobbyEvent>) {
        if(hobbyEventList.isNotEmpty()) {
            var promotedHobby = hobbyEventList[0]

            //IMAGE
            var imageView = parentView.findViewById<ImageView>(R.id.home_promoted_image)
            Picasso.with(this.context)
                .load(promotedHobby.hobby.imageUrl)
                .placeholder(R.drawable.harrastuspassi_lil_kel)
                .error(R.drawable.harrastuspassi_lil_kel)
                .into(imageView)
            //TITLE
            title.text = promotedHobby.hobby.name
            //DESCRIPTION
            parentView.findViewById<TextView>(R.id.home_promoted_description).text = promotedHobby.hobby.description
            //DURATION
            parentView.findViewById<TextView>(R.id.home_promoted_duration).text = "${idToWeekDay(promotedHobby.startWeekday, this.activity!!)}"

            parentView.findViewById<ConstraintLayout>(R.id.home_promoted_hobby).setOnClickListener {
                hobbyItemClicked(promotedHobby, imageView)
            }
            if (hobbyEventList.size > 7) {
                hobbyEventList.removeAt(0)
                popularHobbyList.apply {
                    this.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
                    this.adapter = HobbyHorizontalListAdapter(hobbyEventList.subList(1,6)){ hobbyEvent: HobbyEvent, image: ImageView -> hobbyItemClicked(hobbyEvent, image)}
                }
            } else {
                popularHobbyList.visibility = View.INVISIBLE
            }
        }

        //POPULAR PROMOTION LIST

        /*var hobbyList = ArrayList<HobbyEvent>()
        var hobbyEvent = HobbyEvent()
        hobbyEvent.apply{
            this.startWeekday = 1
            this.hobby = Hobby().apply {
                this.name = "Harrastuksen nimi tähän, voi olla kahdella rivillä"
                this.description = "Harrastuksen lyhyehkö kuvaus tarvittaessa"
            }
        }

        hobbyList.add(hobbyEvent)
        hobbyList.add(hobbyEvent)
        hobbyList.add(hobbyEvent)
        hobbyList.add(hobbyEvent)
        hobbyList.add(hobbyEvent)*/

        /*USER PROMOTION LIST
        userHobbyList.apply {
            this.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = HobbyHorizontalListAdapter(hobbyList){ hobbyEvent: HobbyEvent, image: ImageView -> hobbyItemClicked(hobbyEvent, image)}
        }*/
    }

    private fun hobbyItemClicked(hobbyEvent: HobbyEvent, imageView: ImageView) {
        val intent = Intent(context, HobbyDetailActivity::class.java)

        intent.putExtra("EXTRA_HOBBY", hobbyEvent)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)

        val sharedView: View = imageView
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
        }

        override fun doInBackground(vararg params: Void?): String {
            return try {
                URL(getString(R.string.API_URL) + "hobbyevents/?include=hobby_detail&include=location_detail&include=organizer_detail").readText()

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
                    //progressText.visibility = View.VISIBLE
                    //this@HobbyEventListFragment.progressText.text = getString(R.string.error_try_again_later)
                }
                NO_INTERNET -> {
                    title.text = activity!!.getString(R.string.error_no_internet)
                    //progressText.text = getString(R.string.error_no_internet)
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

                        setHobbyEvents(rootView, hobbyEventArrayList)

                        if (hobbyEventArrayList.size == 0) {
                            //progressText.visibility = View.VISIBLE
                            //progressText.text = getString(R.string.error_no_hobby_events)
                        } else {
                            //progressText.visibility = View.INVISIBLE
                            popularHobbyList.adapter!!.notifyDataSetChanged()
                        }
                    } catch (e: JSONException) {
                        //progressText.text = getString(R.string.error_no_hobby_events)
                    }
                }
            }
            //progressBar.visibility = View.INVISIBLE
            //refreshLayout.isRefreshing = false
            //updateListView(listView, hobbyEventArrayList)

        }
    }
}