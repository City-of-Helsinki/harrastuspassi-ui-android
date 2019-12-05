package fi.haltu.harrastuspassi.adapters

import android.content.Context
import fi.haltu.harrastuspassi.models.Promotion
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.utils.convertToDateRange
import java.text.SimpleDateFormat
import java.util.*


class PromotionListAdapter(private val context: Context, private val list: List<Promotion>, private val clickListener: (Promotion, ImageView) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HobbyListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.adapter_promotion_list_item, parent, false)
        return HobbyListViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val promotion: Promotion = list[position]
        (holder as HobbyListViewHolder).bind(context, promotion, clickListener)
    }

    override fun getItemCount(): Int = list.size

    class HobbyListViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private var title: TextView = itemView.findViewById(R.id.promotion_title)
        private var image: ImageView = itemView.findViewById(R.id.promotion_image)
        private var description: TextView = itemView.findViewById(R.id.promotion_description)
        private var duration: TextView = itemView.findViewById(R.id.promotion_duration)
        private val applicableText = itemView.findViewById<TextView>(R.id.promotion_applicable)

        fun bind(context: Context, promotion: Promotion, clickListener: (Promotion, ImageView) -> Unit) {
            title.text = promotion.title
            description.text = promotion.description
            applicableText.text = context.getString(R.string.available) + ":"
            Picasso.with(itemView.context)
                .load(promotion.imageUrl)
                .placeholder(R.drawable.harrastuspassi_lil_kel)
                .error(R.drawable.harrastuspassi_lil_kel)
                .into(image)


            duration.text = "${convertToDateRange(promotion.startDate, promotion.endDate)}"
            itemView.setOnClickListener { clickListener(promotion, image) }

            if (promotion.isUsed) {
                applicableText.visibility = View.INVISIBLE
                duration.text = context.getString(R.string.promotions_used)
                itemView.findViewById<ConstraintLayout>(R.id.constraintLayout).background = ContextCompat.getDrawable(context, R.color.blackOpacity40)
                //itemView.background = ContextCompat.getDrawable(context, R.drawable.promotion_card_op85)

            }
        }
    }
}