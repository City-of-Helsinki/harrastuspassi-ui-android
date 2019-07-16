package fi.haltu.harrastuspassi.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.models.HobbyEvent


class HobbyEventListAdapter(private val list: List<HobbyEvent>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HobbyListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.adapter_hobby_event_list_item_hobby, parent, false)
        return HobbyListViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val hobbyEvent: HobbyEvent = list[position]
        (holder as HobbyListViewHolder).bind(hobbyEvent)
    }

    override fun getItemCount(): Int = list.size

    class HobbyListViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private var title: TextView = itemView.findViewById(R.id.title)
        private var place: TextView = itemView.findViewById(R.id.place)
        private var image: ImageView = itemView.findViewById(R.id.image)
        private var duration: TextView = itemView.findViewById(R.id.dateTime)

        fun bind(hobbyEvent: HobbyEvent) {
            title.text = hobbyEvent.title
            place.text = hobbyEvent.place
            image.setImageResource(hobbyEvent.image)
            duration.text = hobbyEvent.dateTime
        }
    }
}