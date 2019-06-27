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
    private var description: TextView? = null
    private var image: ImageView? = null

    init {
        title = itemView.findViewById(R.id.title)
        description  = itemView.findViewById(R.id.description)
        image = itemView.findViewById(R.id.image)
    }

    fun bind(hobby: Hobby) {
        title!!.text = hobby!!.title
        description!!.text = hobby.description
        image!!.setImageResource(hobby.image)
    }
}
