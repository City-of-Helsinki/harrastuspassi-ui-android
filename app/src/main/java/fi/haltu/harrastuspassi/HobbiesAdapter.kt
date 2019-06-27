package fi.haltu.harrastuspassi

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import java.util.*

class HobbiesAdapter(private val list: List<Hobby>) :
    RecyclerView.Adapter<HobbyListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HobbyListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return HobbyListViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: HobbyListViewHolder, position: Int) {
        val hobby: Hobby = list[position]
        holder.bind(hobby)
    }

    override fun getItemCount(): Int = list.size

}