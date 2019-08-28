package fi.haltu.harrastuspassi.adapters

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.activities.HobbyCategoriesActivity
import fi.haltu.harrastuspassi.models.Category

class CategoryListAdapter(private val categories: ArrayList<Category>,
                          private val activity: AppCompatActivity,
                          private val selectedItems: HashSet<Int>,
                          private val clickListener:(Category) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.adapter_category_list_item, parent, false)
        return CategoryListViewHolder(view)
    }

    override fun getItemCount(): Int =  categories.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val category: Category = categories[position]
        (holder as CategoryListViewHolder).bind(category, activity, selectedItems, clickListener)
    }

    class CategoryListViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private var name: TextView = itemView.findViewById(R.id.name)
        private var checkButton: CheckBox = itemView.findViewById(R.id.check_button)
        private var showMoreButton: ImageButton = itemView.findViewById(R.id.show_more_button)
        fun bind(category: Category, activity: AppCompatActivity, selectedItems: HashSet<Int>, clickListener: (Category) -> Unit) {
            name.text = category.name
            itemView.setOnClickListener { clickListener(category) }

            checkButton.isChecked = selectedItems.contains(category.id!!)

            checkButton.setOnClickListener {
                if(selectedItems.contains(category.id!!)) {
                    selectedItems.remove(category.id!!)
                    checkButton.isChecked = false

                } else {
                    selectedItems.add(category.id!!)
                    checkButton.isChecked = true
                }
                setShowMoreButton(category, selectedItems, activity)
            }
            setShowMoreButton(category, selectedItems, activity)
        }

        private fun setShowMoreButton(category: Category, selectedItems: HashSet<Int>, activity: AppCompatActivity) {
            if(category.childCategories!!.size == 0 ) {
                showMoreButton.visibility = View.INVISIBLE
            } else {
                showMoreButton.visibility = View.VISIBLE
                showMoreButton.setOnClickListener{
                    Toast.makeText(activity, "Show more!" + category.name, Toast.LENGTH_SHORT).show()
                    val intent = Intent(activity, HobbyCategoriesActivity::class.java)
                    val bundle = Bundle()
                    bundle.putSerializable("CATEGORY_LIST", category.childCategories)
                    intent.putExtra("EXTRA_CATEGORY_BUNDLE", bundle)
                    intent.putExtra("EXTRA_SELECTED_ITEMS", selectedItems)
                    intent.putExtra("EXTRA_CATEGORY_NAME", category.name)

                    activity.startActivityForResult(intent, 1)
                }
            }
        }
    }
}