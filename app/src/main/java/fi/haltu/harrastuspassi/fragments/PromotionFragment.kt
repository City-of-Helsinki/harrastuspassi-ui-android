package fi.haltu.harrastuspassi.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.adapters.PromotionListAdapter
import fi.haltu.harrastuspassi.models.Promotion

class PromotionFragment : Fragment(){
    private lateinit var promotionListView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_promotion, container, false)
        setHasOptionsMenu(true)

        //Promotion List
        var promotionList = ArrayList<Promotion>()
        promotionList.add(Promotion())
        promotionList.add(Promotion())
        promotionList.add(Promotion())
        promotionList.add(Promotion())
        promotionList.add(Promotion())
        promotionListView = view.findViewById(R.id.promotion_list_view)
        val promotionListAdapter = PromotionListAdapter(promotionList){ promotion: Promotion, promotionImage: ImageView -> hobbyItemClicked(promotion, promotionImage)}
        promotionListView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = promotionListAdapter
        }

        return view
    }
    private fun hobbyItemClicked(promotion: Promotion, hobbyImage: ImageView) {
        Toast.makeText(this.context, "${promotion.title} clicked", Toast.LENGTH_SHORT).show()
    }
    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.map).isVisible = false
        menu.findItem(R.id.action_filter).isVisible = false

        super.onPrepareOptionsMenu(menu)
    }
}