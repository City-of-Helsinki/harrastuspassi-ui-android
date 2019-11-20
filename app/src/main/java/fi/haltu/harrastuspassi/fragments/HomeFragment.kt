package fi.haltu.harrastuspassi.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.adapters.PromotionHorizontalListAdapter
import fi.haltu.harrastuspassi.models.Promotion


class HomeFragment : Fragment() {
    lateinit var popularPromotionsList: RecyclerView
    lateinit var userPromotionsList: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)

        //PROMOTIONS LISTS
        popularPromotionsList = view.findViewById(R.id.home_popular_promotion_list)
        userPromotionsList = view.findViewById(R.id.home_user_promotion_list)
        setPromotions(view)

        setHobbies(view)
        return view
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.map).isVisible = false
        menu.findItem(R.id.action_filter).isVisible = false

        super.onPrepareOptionsMenu(menu)
    }

    private fun setPromotions(parentView: View) {
        var promotedPromotionView: View = parentView.findViewById(R.id.home_promoted_promotion)
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

        //POPULAR PROMOTION LIST

        var promotionList = ArrayList<Promotion>()
        promotionList.add(Promotion())
        promotionList.add(Promotion())
        promotionList.add(Promotion())
        promotionList.add(Promotion())
        popularPromotionsList.apply {
            this.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = PromotionHorizontalListAdapter(promotionList){ promotion: Promotion, image: ImageView -> promotionsItemClicked(promotion, image)}
        }

        //USER PROMOTION LIST
        userPromotionsList.apply {
            this.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            this.adapter = PromotionHorizontalListAdapter(promotionList){ promotion: Promotion, image: ImageView -> promotionsItemClicked(promotion, image)}
        }
    }

    private fun promotionsItemClicked(promotion: Promotion, imageView: ImageView) {

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