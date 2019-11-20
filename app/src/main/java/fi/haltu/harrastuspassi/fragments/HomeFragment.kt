package fi.haltu.harrastuspassi.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import fi.haltu.harrastuspassi.R


class HomeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)


        setHobbies(view)
        return view
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.map).isVisible = false
        menu.findItem(R.id.action_filter).isVisible = false

        super.onPrepareOptionsMenu(menu)
    }


    private fun setHobbies(parentView: View) {
        var promotedPromotionView: View = parentView.findViewById(R.id.home_promoted_hobby)
        //IMAGE
        Picasso.with(this.context)
            .load("URL")
            .placeholder(R.drawable.harrastuspassi_lil_kel)
            .error(R.drawable.harrastuspassi_lil_kel)
            .into(promotedPromotionView.findViewById<ImageView>(R.id.home_promoted_image))
        //TITLE
        promotedPromotionView.findViewById<TextView>(R.id.home_promoted_title).text = "TitleText"
        //DESCRIPTION
        promotedPromotionView.findViewById<TextView>(R.id.home_promoted_description).text = "Nullam volutpat tempor metus vel rhoncus. Fusce sodales diam risus, nec hendrerit augue fermentum eu. Donec vitae erat ut libero molestie congue in vitae ligula."
        //DURATION
        promotedPromotionView.findViewById<TextView>(R.id.home_promoted_duration).text = "Voimassa: 10.11 - 15.11.2019"
    }
}