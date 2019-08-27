package fi.haltu.harrastuspassi.adapters

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import fi.haltu.harrastuspassi.R

class FilterTagsRecyclerViewAdapter(private val categoryTagsList: ArrayList<String>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.filter_tags_item, parent, false)
        return TagViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val filterTag : String = categoryTagsList[position]
        (holder as TagViewHolder).bind(filterTag)
    }

    override fun getItemCount(): Int {
        return categoryTagsList.size
    }

    class TagViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private var tag: TextView = itemView.findViewById(R.id.textView)

        fun bind(filterTag: String) {
            tag.text = filterTag
            Log.d("bind", "filter tags")


        }
    }
}
