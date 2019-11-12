package fi.haltu.harrastuspassi.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.adapters.PromotionListAdapter
import fi.haltu.harrastuspassi.models.Promotion
import fi.haltu.harrastuspassi.utils.convertToDateRange
import kotlin.collections.ArrayList

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
        val dialog = Dialog(this.context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_promotion_detail)
        //IMAGE
        val imageView = dialog.findViewById<ImageView>(R.id.promotion_dialog_image)
        Picasso.with(context)
            .load(promotion.imageUrl)
            .placeholder(R.drawable.harrastuspassi_lil_kel)
            .error(R.drawable.harrastuspassi_lil_kel)
            .into(imageView)
        //TITLE
        val titleText = dialog.findViewById<TextView>(R.id.promotion_dialog_title)
        titleText.text = promotion.title
        //DESCRIPTION
        val descriptionText = dialog.findViewById<TextView>(R.id.promotion_dialog_description)
        descriptionText.text = promotion.description
        //DATE
        val durationText = dialog.findViewById<TextView>(R.id.promotion_dialog_duration)

        durationText.text = "${activity!!.getString(R.string.available)} ${convertToDateRange(promotion.startDate, promotion.endDate)}"
        //CLOSE_ICON
        val closeIcon = dialog.findViewById<ImageView>(R.id.dialog_close_button)
        closeIcon.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        dialog.show()

    }
    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.map).isVisible = false
        menu.findItem(R.id.action_filter).isVisible = false

        super.onPrepareOptionsMenu(menu)
    }
}