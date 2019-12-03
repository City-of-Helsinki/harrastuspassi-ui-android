package fi.haltu.harrastuspassi.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import androidx.annotation.LayoutRes
import fi.haltu.harrastuspassi.models.Category

class CategorySearchAdapter(context: Context, @LayoutRes private val layoutResource: Int, categoryList: List<Category>):
    ArrayAdapter<Category>(context, layoutResource, categoryList) {
    private var categories: List<Category> = categoryList
    private var categoriesOrigin: List<Category> = categoryList
    override fun getCount(): Int {
        return categories.size
    }

    override fun getItem(p0: Int): Category? {
        return categories[p0]
    }

    override fun getItemId(p0: Int): Long {
        return categories[p0].id!!.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: TextView = convertView as TextView? ?: LayoutInflater.from(context).inflate(layoutResource, parent, false) as TextView
        view.text = "${categories[position].name}"
        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                categories = results.values as List<Category>

                notifyDataSetChanged()
            }

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val queryString = constraint?.toString()?.toLowerCase()
                val filterResults = FilterResults()
                filterResults.values = if (queryString == null || queryString.isEmpty()) {
                    categoriesOrigin
                } else {
                    categories.filter {
                        it.name!!.toLowerCase().contains(queryString)
                    }
                }

                return filterResults
            }
        }
    }
}