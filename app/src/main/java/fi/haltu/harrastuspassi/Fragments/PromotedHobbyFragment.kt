package fi.haltu.harrastuspassi.Fragments

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fi.haltu.harrastuspassi.Adapters.PromotedHobbiesAdapter
import fi.haltu.harrastuspassi.Model.Hobby
import fi.haltu.harrastuspassi.R

class PromotedHobbyFragment : Fragment() {
    private lateinit var viewpager: ViewPager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_promoted_hobby, container, false)
        viewpager = view.findViewById(R.id.viewpager)
        val adapter = PromotedHobbiesAdapter(view.context, activity!!, addHobbies())
        viewpager.adapter = adapter

        val indicator = view.findViewById<TabLayout>(R.id.indicator_image)
        indicator.setupWithViewPager(viewpager, true)
        return view
    }

    private fun addHobbies(): ArrayList<Hobby> {
        val hobbiesList = ArrayList<Hobby>()

        var hobby = Hobby()
        hobby.title = "Kitara kurssi"
        hobby.description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit." +
                " Nam sed vestibulum turpis, in condimentum urna. " +
                "Morbi mattis bibendum massa, quis cursus erat rhoncus vel."
        hobby.image = R.drawable.image_1
        hobby.place = "Itä-Hakkilan koulu"
        hobby.distance = 2.0
        hobby.duration = "ke"
        hobby.organizer = "Valar Morghulis Taide Ry"

        hobbiesList.add(hobby)

        hobby = Hobby()
        hobby.title = "Taidetta muovipulloista"
        hobby.description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit." +
                " Nam sed vestibulum turpis, in condimentum urna. " +
                "Morbi mattis bibendum massa, quis cursus erat rhoncus vel."
        hobby.image = R.drawable.image_2
        hobby.place = "Itä-Hakkilan koulu"
        hobby.distance = 2.0
        hobby.duration = "ke"
        hobby.organizer = "Valar Morghulis Taide Ry"

        hobbiesList.add(hobby)

        hobby = Hobby()
        hobby.title = "Jalkapallo, tutustumiskurssi"
        hobby.description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit." +
                " Nam sed vestibulum turpis, in condimentum urna. " +
                "Morbi mattis bibendum massa, quis cursus erat rhoncus vel."
        hobby.image = R.drawable.image_3
        hobby.place = "Itä-Hakkilan koulu"
        hobby.distance = 2.0
        hobby.duration = "ke"
        hobby.organizer = "Valar Morghulis Taide Ry"

        hobbiesList.add(hobby)

        return hobbiesList
    }
}