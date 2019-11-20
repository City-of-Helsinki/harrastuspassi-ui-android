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
import fi.haltu.harrastuspassi.adapters.PromotionHorizontalListAdapter
import fi.haltu.harrastuspassi.models.Promotion

class HomePromotionsFragment : Fragment() {
    lateinit var popularPromotionsList: RecyclerView
    lateinit var userPromotionsList: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_home_promotions, container, false)
        setHasOptionsMenu(true)

        //PROMOTIONS LISTS
        popularPromotionsList = view.findViewById(R.id.home_popular_promotion_list)
        userPromotionsList = view.findViewById(R.id.home_user_promotion_list)
        setPromotions(view)

        return view
    }

    private fun setPromotions(parentView: View) {
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
}