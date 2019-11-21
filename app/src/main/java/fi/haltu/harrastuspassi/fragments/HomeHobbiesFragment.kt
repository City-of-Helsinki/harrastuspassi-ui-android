package fi.haltu.harrastuspassi.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.adapters.HobbyHorizontalListAdapter
import fi.haltu.harrastuspassi.models.Hobby
import fi.haltu.harrastuspassi.models.HobbyEvent

class HomeHobbiesFragment : Fragment() {
    lateinit var popularHobbyList: RecyclerView
    lateinit var userHobbyList: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_home_hobbies, container, false)
        setHasOptionsMenu(true)

        //PROMOTIONS LISTS
        popularHobbyList = view.findViewById(R.id.home_popular_hobby_list)
        userHobbyList = view.findViewById(R.id.home_user_hobby_list)
        setHobby(view)

        return view
    }

    private fun setHobby(parentView: View) {
        //IMAGE
        Picasso.with(this.context)
            .load("URL")
            .placeholder(R.drawable.harrastuspassi_lil_kel)
            .error(R.drawable.harrastuspassi_lil_kel)
            .into(parentView.findViewById<ImageView>(R.id.home_promoted_image))
        //TITLE
        parentView.findViewById<TextView>(R.id.home_promoted_title).text = "TitleText"
        //DESCRIPTION
        parentView.findViewById<TextView>(R.id.home_promoted_description).text = "Nullam volutpat tempor metus vel rhoncus. Fusce sodales diam risus, nec hendrerit augue fermentum eu. Donec vitae erat ut libero molestie congue in vitae ligula."
        //DURATION
        parentView.findViewById<TextView>(R.id.home_promoted_duration).text = "Voimassa: 10.11 - 15.11.2019"

        //POPULAR PROMOTION LIST

        var hobbyList = ArrayList<HobbyEvent>()
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
        hobbyList.add(hobbyEvent)


        popularHobbyList.apply {
            this.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = HobbyHorizontalListAdapter(hobbyList){ hobbyEvent: HobbyEvent, image: ImageView -> hobbyItemClicked(hobbyEvent, image)}
        }

        //USER PROMOTION LIST
        userHobbyList.apply {
            this.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = HobbyHorizontalListAdapter(hobbyList){ hobbyEvent: HobbyEvent, image: ImageView -> hobbyItemClicked(hobbyEvent, image)}
        }
    }

    private fun hobbyItemClicked(hobbyEvent: HobbyEvent, imageView: ImageView) {

    }
}