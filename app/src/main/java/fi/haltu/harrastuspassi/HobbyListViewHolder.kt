package fi.haltu.harrastuspassi

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import org.w3c.dom.Text

class HobbyListViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.hobby_item, parent, false)) {

    private var title: TextView? = null
    private var place: TextView? = null
    private var image: ImageView? = null
    private var duration: TextView? = null
    init {
        title = itemView.findViewById(R.id.title)
        place  = itemView.findViewById(R.id.place)
        image = itemView.findViewById(R.id.image)
        duration = itemView.findViewById(R.id.duration)
    }

    fun bind(hobby: Hobby) {
        title!!.text = hobby!!.title
        place!!.text = hobby.place
        image!!.setImageResource(hobby.image)
        duration!!.text = hobby.duration
    }
}
