package fi.haltu.harrastuspassi

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class HobbyListFragment : Fragment() {
    private lateinit var listView : RecyclerView
    private val hobbies = HashMap<String, ArrayList<Hobby>>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        loadHobbiesData()
        val view: View = inflater.inflate(R.layout.hobby_list_fragment, container, false)
        listView = view.findViewById(R.id.list_view)


        val seattleHobbies = getHobbiesByPlace("Seattle")
        val hobbiesAdapter = HobbiesAdapter(seattleHobbies, {hobby: Hobby, cardView: CardView -> hobbyItemClicked(hobby, cardView)})


        listView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = hobbiesAdapter
        }

        return view
    }

    private fun getHobbiesByPlace(place: String): ArrayList<Hobby> {
        val hobbies = hobbies[place]
        if (hobbies != null) return hobbies else return ArrayList<Hobby>()
    }

    private fun loadHobbiesData() {
        addSeattleAttractions(hobbies)
    }

    private fun hobbyItemClicked(hobby: Hobby, cardView: CardView) {
        val intent = Intent(context, HobbyDetailActivity::class.java)

        val sharedView: View = cardView
        val transition = getString(R.string.item_detail)

        intent.putExtra("EXTRA_HOBBY", hobby)
        val transitionActivity = ActivityOptions.makeSceneTransitionAnimation(activity, sharedView, transition)
        startActivity(intent, transitionActivity.toBundle())
    }

    private fun addSeattleAttractions(hobbiesByCity: MutableMap<String, ArrayList<Hobby>>) {
        val hobbesList = ArrayList<Hobby>()

        var hobby = Hobby()
        hobby.title = "Kitara kurssi"
        hobby.description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit." +
                " Nam sed vestibulum turpis, in condimentum urna. " +
                "Morbi mattis bibendum massa, quis cursus erat rhoncus vel."
        hobby.image = R.drawable.image_1
        hobby.place = "Itä-Hakkilan koulu"
        hobby.distance = 2.0
        hobby.duration = "ma - pe klo 17 - 20"
        hobby.organizer = "Valar Morghulis Taide Ry"

        hobbesList.add(hobby)

        hobby = Hobby()
        hobby.title = "Taidetta muovipulloista"
        hobby.description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit." +
                " Nam sed vestibulum turpis, in condimentum urna. " +
                "Morbi mattis bibendum massa, quis cursus erat rhoncus vel."
        hobby.image = R.drawable.image_2
        hobby.place = "Runar Schildts Park"
        hobby.distance = 2.0
        hobby.duration = "pe"
        hobby.organizer = "Valar Morghulis Taide Ry"

        hobbesList.add(hobby)

        hobby = Hobby()
        hobby.title = "Jalkapallo, tutustumiskurssi"
        hobby.description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit." +
                " Nam sed vestibulum turpis, in condimentum urna. " +
                "Morbi mattis bibendum massa, quis cursus erat rhoncus vel."
        hobby.image = R.drawable.image_3
        hobby.place = "Hatwall Areena"
        hobby.distance = 2.0
        hobby.duration = "ma, ke, pe"
        hobby.organizer = "Valar Morghulis Taide Ry"

        hobbesList.add(hobby)

        hobby = Hobby()
        hobby.title = "Sulkapallo kokeneille"
        hobby.description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit." +
                " Nam sed vestibulum turpis, in condimentum urna. " +
                "Morbi mattis bibendum massa, quis cursus erat rhoncus vel."
        hobby.image = R.drawable.image_4
        hobby.place = "Runar Schilds Park"
        hobby.distance = 2.0
        hobby.duration = "lauantai klo 14"
        hobby.organizer = "Valar Morghulis Taide Ry"

        hobbesList.add(hobby)

        hobby = Hobby()
        hobby.title = "Käsityö"
        hobby.description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit." +
                " Nam sed vestibulum turpis, in condimentum urna. " +
                "Morbi mattis bibendum massa, quis cursus erat rhoncus vel."
        hobby.image = R.drawable.image_5
        hobby.place = "Itä-Hakkilan koulu"
        hobby.distance = 2.0
        hobby.duration = "ma - ke"
        hobby.organizer = "Valar Morghulis Taide Ry"

        hobbesList.add(hobby)
        hobby = Hobby()
        hobby.title = "Kabaddi valituille"
        hobby.description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit." +
                " Nam sed vestibulum turpis, in condimentum urna. " +
                "Morbi mattis bibendum massa, quis cursus erat rhoncus vel."
        hobby.image = R.drawable.image_6
        hobby.place = "Hartwall Areena"
        hobby.distance = 2.0
        hobby.duration = "Jokapäivä klo 8 - 18"
        hobby.organizer = "Helsingin kabaddi seura"

        hobbesList.add(hobby)

        hobbiesByCity.put("Seattle", hobbesList)

    }

}