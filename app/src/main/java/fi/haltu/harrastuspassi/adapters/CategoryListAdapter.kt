package fi.haltu.harrastuspassi.adapters

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.models.Category

class CategoryListAdapter(private val categories: ArrayList<Category>, private val clickListener:(Category) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.adapter_category_list_item, parent, false)
        return CategoryListViewHolder(view)
    }

    override fun getItemCount(): Int {

        return categories.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val category: Category = categories[position]
        (holder as CategoryListViewHolder).bind(category, clickListener)
    }

    class CategoryListViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private var name: TextView = itemView.findViewById(R.id.name)

        fun bind(category: Category, clickListener: (Category) -> Unit) {
            name.text = category.name
            itemView.setOnClickListener { clickListener(category) }
        }
    }
}