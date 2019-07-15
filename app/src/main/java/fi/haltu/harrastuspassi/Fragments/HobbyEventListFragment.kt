package fi.haltu.harrastuspassi.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fi.haltu.harrastuspassi.Adapters.HobbyEventListAdapter
import fi.haltu.harrastuspassi.Model.HobbyEvent
import fi.haltu.harrastuspassi.R


class HobbyEventListFragment : Fragment() {
    private lateinit var listView: RecyclerView
    private var hobbyEventList = ArrayList<HobbyEvent>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hobbyEventList = addHobbyEvents()
        val view: View = inflater.inflate(R.layout.fragment_hobby_event_list, container, false)
        val hobbyEventList = HobbyEventListAdapter(hobbyEventList)

        listView = view.findViewById(R.id.list_view)
        listView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = hobbyEventList
        }

        return view
    }

    private fun addHobbyEvents(): ArrayList<HobbyEvent> {
        val hobbesList = ArrayList<HobbyEvent>()

        var hobbyEvent = HobbyEvent()
        hobbyEvent.title = "Kitara kurssi"
        hobbyEvent.image = R.drawable.image_1
        hobbyEvent.place = "Itä-Hakkilan koulu"
        hobbyEvent.dateTime = "Ma 23.9 klo 18-20"

        hobbesList.add(hobbyEvent)

        hobbyEvent = HobbyEvent()
        hobbyEvent.title = "Taidetta muovipulloista"
        hobbyEvent.image = R.drawable.image_2
        hobbyEvent.place = "Runar Schildts Park"
        hobbyEvent.dateTime = "Ma 23.9 klo 18-20"

        hobbesList.add(hobbyEvent)

        hobbyEvent = HobbyEvent()
        hobbyEvent.title = "Jalkapallo, tutustumiskurssi"
        hobbyEvent.image = R.drawable.image_3
        hobbyEvent.place = "Hatwall Areena"
        hobbyEvent.dateTime = "Ma 23.9 klo 18-20"

        hobbesList.add(hobbyEvent)

        hobbyEvent = HobbyEvent()
        hobbyEvent.title = "Sulkapallo kokeneille"
        hobbyEvent.image = R.drawable.image_4
        hobbyEvent.place = "Runar Schilds Park"
        hobbyEvent.dateTime = "Ma 23.9 klo 18-20"

        hobbesList.add(hobbyEvent)

        hobbyEvent = HobbyEvent()
        hobbyEvent.title = "Käsityö"
        hobbyEvent.image = R.drawable.image_5
        hobbyEvent.place = "Itä-Hakkilan koulu"
        hobbyEvent.dateTime = "Ma 23.9 klo 18-20"

        hobbesList.add(hobbyEvent)
        hobbyEvent = HobbyEvent()
        hobbyEvent.title = "Kabaddi valituille"
        hobbyEvent.image = R.drawable.image_6
        hobbyEvent.place = "Hartwall Areena"
        hobbyEvent.dateTime = "Ma 23.9 klo 18-20"

        hobbyEvent = HobbyEvent()
        hobbyEvent.title = "Taidetta muovipulloista"
        hobbyEvent.image = R.drawable.image_2
        hobbyEvent.place = "Runar Schildts Park"
        hobbyEvent.dateTime = "Ma 23.9 klo 18-20"

        hobbesList.add(hobbyEvent)

        hobbyEvent = HobbyEvent()
        hobbyEvent.title = "Jalkapallo, tutustumiskurssi"
        hobbyEvent.image = R.drawable.image_3
        hobbyEvent.place = "Hatwall Areena"
        hobbyEvent.dateTime = "Ma 23.9 klo 18-20"

        hobbesList.add(hobbyEvent)

        hobbyEvent = HobbyEvent()
        hobbyEvent.title = "Sulkapallo kokeneille"
        hobbyEvent.image = R.drawable.image_4
        hobbyEvent.place = "Runar Schilds Park"
        hobbyEvent.dateTime = "Ma 23.9 klo 18-20"

        hobbesList.add(hobbyEvent)

        hobbyEvent = HobbyEvent()
        hobbyEvent.title = "Käsityö"
        hobbyEvent.image = R.drawable.image_5
        hobbyEvent.place = "Itä-Hakkilan koulu"
        hobbyEvent.dateTime = "Ma 23.9 klo 18-20"

        hobbesList.add(hobbyEvent)
        hobbyEvent = HobbyEvent()
        hobbyEvent.title = "Kabaddi valituille"
        hobbyEvent.image = R.drawable.image_6
        hobbyEvent.place = "Hartwall Areena"
        hobbyEvent.dateTime = "Ma 23.9 klo 18-20"

        hobbesList.add(hobbyEvent)

        return hobbesList
    }
}