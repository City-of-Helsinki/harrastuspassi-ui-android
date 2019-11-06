package fi.haltu.harrastuspassi.adapters

import fi.haltu.harrastuspassi.models.Promotion
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fi.haltu.harrastuspassi.R
import java.text.SimpleDateFormat
import java.util.*


class PromotionListAdapter(private val list: List<Promotion>, private val clickListener: (Promotion, ImageView) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HobbyListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.adapter_promotion_list_item, parent, false)
        return HobbyListViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val promotion: Promotion = list[position]
        (holder as HobbyListViewHolder).bind(promotion, clickListener)
    }

    override fun getItemCount(): Int = list.size

    class HobbyListViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private var title: TextView = itemView.findViewById(R.id.promotion_title)
        private var image: ImageView = itemView.findViewById(R.id.promotion_image)
        private var description: TextView = itemView.findViewById(R.id.promotion_description)
        private var duration: TextView = itemView.findViewById(R.id.promotion_duration)

        fun bind(promotion: Promotion, clickListener: (Promotion, ImageView) -> Unit) {
            title.text = promotion.title
            description.text = promotion.description
            Picasso.with(itemView.context)
                .load(promotion.imageUrl)
                .placeholder(R.drawable.harrastuspassi_lil_kel)
                .error(R.drawable.harrastuspassi_lil_kel)
                .into(image)
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val formatterDDMM = SimpleDateFormat("dd.mm.yyyy", Locale.US)
            val formatterDDMMYYYY = SimpleDateFormat("dd.MM.yyyy", Locale.US)
            var startDate = ""
            var endDate = ""
            try {
                startDate = formatterDDMM.format(parser.parse(promotion.startDate))
                endDate = formatterDDMMYYYY.format(parser.parse(promotion.endDate))
                //TODO Add end date too :)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            duration.text = "Voimassa $startDate - $endDate"
            itemView.setOnClickListener { clickListener(promotion, image) }
        }
    }
}