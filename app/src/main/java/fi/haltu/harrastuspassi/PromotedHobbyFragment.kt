package fi.haltu.harrastuspassi

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class PromotedHobbyFragment : Fragment() {
    private lateinit var viewpager : ViewPager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.promoted_hobby_fragment, container, false)
        viewpager = view.findViewById(R.id.viewpager)
        val adapter = PromotedHobbiesAdapter(view.context)
        viewpager.adapter = adapter

        val indicator = view.findViewById<TabLayout>(R.id.indicator_image)
        indicator.setupWithViewPager(viewpager, true)
        return view
    }

}