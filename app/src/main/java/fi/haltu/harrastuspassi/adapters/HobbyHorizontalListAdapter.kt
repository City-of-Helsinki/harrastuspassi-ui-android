package fi.haltu.harrastuspassi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.models.HobbyEvent
import fi.haltu.harrastuspassi.utils.idToWeekDay

class HobbyHorizontalListAdapter(
    private val list: List<HobbyEvent>,
    private val clickListener: (HobbyEvent, ImageView) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HobbyListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.adapter_horizontal_hobby_list_item, parent, false)
        return HobbyListViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val hobbyEvent: HobbyEvent = list[position]
        (holder as HobbyListViewHolder).bind(hobbyEvent, clickListener)
    }

    override fun getItemCount(): Int = list.size

    class HobbyListViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private var image: ImageView = itemView.findViewById(R.id.horizontal_hobby_list_image)
        private var title: TextView = itemView.findViewById(R.id.horizontal_hobby_list_title_text)
        private var description: TextView =
            itemView.findViewById(R.id.horizontal_hobby_list_description)
        private var weekDay: TextView = itemView.findViewById(R.id.horizontal_hobby_list_week_day)
        private var weekDayIcon: TextView = itemView.findViewById(R.id.horizontal_hobby_list_week_day_icon)
        fun bind(hobbyEvent: HobbyEvent, clickListener: (HobbyEvent, ImageView) -> Unit) {
            title.text = hobbyEvent.hobby.name
            description.text = hobbyEvent.hobby.description
            if(hobbyEvent.isLipasEvent()) {
                weekDayIcon.visibility = View.INVISIBLE
                weekDay.text = ""
            } else {
                weekDayIcon.visibility = View.VISIBLE
                weekDay.text = idToWeekDay(hobbyEvent.startWeekday, itemView.context)
            }
            Picasso.with(itemView.context)
                .load(hobbyEvent.hobby.imageUrl)
                .placeholder(R.drawable.harrastuspassi_lil_kel)
                .error(R.drawable.harrastuspassi_lil_kel)
                .into(image)


            itemView.setOnClickListener { clickListener(hobbyEvent, image) }

        }
    }
}