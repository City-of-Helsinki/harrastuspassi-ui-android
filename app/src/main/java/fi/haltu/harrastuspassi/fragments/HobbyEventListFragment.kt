package fi.haltu.harrastuspassi.fragments

import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.adapters.HobbyEventListAdapter
import fi.haltu.harrastuspassi.models.HobbyEvent
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.URL


class HobbyEventListFragment : Fragment() {
    private lateinit var listView: RecyclerView
    private var hobbyEventArrayList = ArrayList<HobbyEvent>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //hobbyEventList = addHobbyEvents()
        val view: View = inflater.inflate(R.layout.fragment_hobby_event_list, container, false)
        val hobbyEventList = HobbyEventListAdapter(hobbyEventArrayList)

        getHobbyEvents().execute()

        listView = view.findViewById(R.id.list_view)
        listView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = hobbyEventList
        }

        return view
    }

    internal inner class getHobbyEvents : AsyncTask<Void, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            Log.d("Testing","Start")

        }

        override fun doInBackground(vararg params: Void?): String {
            Log.d("Testing","In progress")
            var result: String
            try {
                result = URL("http://10.0.1.172:8000/hobbies/").readText()
            } catch (e: IOException) {
                result = "error"
            }

            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(result != "error") {
                val mJsonArray = JSONArray(result)
                for(i in 0 until mJsonArray.length()) {
                    val sObject = mJsonArray.get(i).toString()
                    val mItemObject = JSONObject(sObject)
                    val title =  mItemObject.getString("name")
                    val place =  mItemObject.getString("location")
                    val dateTime =  mItemObject.getString("day_of_week")
                    val image =  mItemObject.getString("image")
                    var hobbyEvent = HobbyEvent(title, place, dateTime, image)

                    hobbyEventArrayList.add(hobbyEvent)
                }
            }

            listView.adapter!!.notifyDataSetChanged()
        }
    }
}