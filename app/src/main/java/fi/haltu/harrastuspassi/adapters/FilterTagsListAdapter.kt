package fi.haltu.harrastuspassi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.models.Category
import java.util.*
import kotlin.collections.ArrayList

class FilterTagsListAdapter(
    private val categoryTagsList: ArrayList<Category>,
    private val clickListener: (category: Category) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.filter_tags_item, parent, false)
        return TagViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val filterTag: Category = categoryTagsList[position]
        (holder as TagViewHolder).bind(filterTag, clickListener)
    }

    override fun getItemCount(): Int {
        return categoryTagsList.size
    }





    class TagViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private var tag: TextView = itemView.findViewById(R.id.textView)
        private var deleteButton: ImageButton = itemView.findViewById(R.id.delete_tag)

        fun bind(category: Category, clickListener: (Category) -> Unit) {
            tag.text = translateCategory(category)
            deleteButton.setOnClickListener { clickListener(category) }
        }

        private fun translateCategory(category: Category) : String {
            val currentLanguage = Locale.getDefault().language

            return if(currentLanguage == "fi") {
                category.nameFi
            } else if(currentLanguage == "sv") {
                category.nameSv
            } else {
                if(category.nameEn.isNullOrEmpty()) {
                    category.name
                } else {
                    category.nameEn
                }
            }
        }
    }
}
