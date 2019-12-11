package fi.haltu.harrastuspassi.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.models.Promotion
import fi.haltu.harrastuspassi.utils.convertToDateRange

class PromotionHorizontalListAdapter(private val context: Context, private val list: List<Promotion>, private val clickListener: (Promotion) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HobbyListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.adapter_horizontal_promotion_list_item, parent, false)
        return HobbyListViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val promotion: Promotion = list[position]
        (holder as HobbyListViewHolder).bind(context, promotion, clickListener)
    }

    override fun getItemCount(): Int = list.size

    class HobbyListViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private var image: ImageView = itemView.findViewById(R.id.horizontal_promotion_list_image)
        private var title: TextView = itemView.findViewById(R.id.horizontal_promotion_list_title_text)
        private var availableLabel: TextView = itemView.findViewById(R.id.horizontal_promotion_list_available_label)
        private var duration: TextView = itemView.findViewById(R.id.horizontal_promotion_list_available_text)
        fun bind(context: Context, promotion: Promotion, clickListener: (Promotion) -> Unit) {
            title.text = promotion.title

            Picasso.with(itemView.context)
                .load(promotion.imageUrl)
                .placeholder(R.drawable.harrastuspassi_lil_kel)
                .error(R.drawable.harrastuspassi_lil_kel)
                .into(image)


            duration.text = "${convertToDateRange(promotion.startDate, promotion.endDate)}"
            itemView.setOnClickListener { clickListener(promotion) }

            if (promotion.isUsed) {
                availableLabel.visibility = View.INVISIBLE
                duration.text = itemView.context.getString(R.string.promotions_used)
                itemView.findViewById<ConstraintLayout>(R.id.horizontal_constraint).background = ContextCompat.getDrawable(context, R.color.blackOpacity40)
            }
        }
    }
}