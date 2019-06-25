package fi.haltu.harrastuspassi

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class PromotedHobbyFragment : Fragment() {
    private lateinit var viewpager : ViewPager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.promoted_hobby_fragment, container, false)
        //sliderview
        viewpager = view.findViewById(R.id.viewpager)
        val adapter = ViewPagerAdapter(view.context)
        viewpager.adapter = adapter

        return view
    }

}