package fi.haltu.harrastuspassi

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView


class HobbiesAdapter(private val list: List<Hobby>, val clickListener: (Hobby) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HobbyListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.hobby_item, parent, false)
        return HobbyListViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val hobby: Hobby = list[position]
        (holder as HobbyListViewHolder).bind(hobby, clickListener)
    }

    override fun getItemCount(): Int = list.size

    class HobbyListViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        private var title: TextView
        private var place: TextView
        private var image: ImageView
        private var duration: TextView
        init {
            title = itemView.findViewById(R.id.title)
            place  = itemView.findViewById(R.id.place)
            image = itemView.findViewById(R.id.image)
            duration = itemView.findViewById(R.id.duration)
        }

        fun bind(hobby: Hobby, clickListener: (Hobby) -> Unit) {
            title.text = hobby.title
            place.text = hobby.place
            image.setImageResource(hobby.image)
            duration.text = hobby.duration
            itemView.setOnClickListener{clickListener(hobby)}
        }


    }

}