package fi.haltu.harrastuspassi.adapters

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.activities.SubCategoryActivity
import fi.haltu.harrastuspassi.models.Category

class CategoryListAdapter(private val categories: ArrayList<Category>, private val activity: AppCompatActivity, private val clickListener:(Category) -> Unit) :
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
        (holder as CategoryListViewHolder).bind(category, activity, clickListener)
    }

    class CategoryListViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private var name: TextView = itemView.findViewById(R.id.name)
        private var checkButton: ImageButton = itemView.findViewById(R.id.check_button)
        private var showMoreButton: ImageButton = itemView.findViewById(R.id.show_more_button)
        fun bind(category: Category, activity: AppCompatActivity, clickListener: (Category) -> Unit) {
            name.text = category.name
            itemView.setOnClickListener { clickListener(category) }
            checkButton.setOnClickListener {
                Toast.makeText(activity, "Check!!" + category.name, Toast.LENGTH_SHORT).show()
            }

            if(category.childCategories!!.size == 0) {
                showMoreButton.visibility = View.INVISIBLE
            } else {
                showMoreButton.setOnClickListener{
                    Toast.makeText(activity, "Show more!" + category.name, Toast.LENGTH_SHORT).show()
                    val intent = Intent(activity, SubCategoryActivity::class.java)
                    val bundle = Bundle()
                    bundle.putSerializable("CATEGORY_LIST", category.childCategories)
                    intent.putExtra("EXTRA_CATEGORY_BUNDLE", bundle)
                    activity.startActivity(intent)
                }
            }
        }
    }
}