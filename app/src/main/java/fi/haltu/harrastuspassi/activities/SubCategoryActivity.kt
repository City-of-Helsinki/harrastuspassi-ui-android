package fi.haltu.harrastuspassi.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toolbar
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.adapters.CategoryListAdapter
import fi.haltu.harrastuspassi.models.Category
import android.R.id.toggle
import android.content.Intent


class SubCategoryActivity : AppCompatActivity() {
    private var categoryList = ArrayList<Category>()
    private lateinit var listView: RecyclerView
    private lateinit var selectedCategories: HashSet<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_category)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = intent.extras!!.getSerializable("EXTRA_CATEGORY_NAME") as String

        val bundle = intent.getBundleExtra("EXTRA_CATEGORY_BUNDLE")
        categoryList = bundle.getSerializable("CATEGORY_LIST") as ArrayList<Category>
        selectedCategories = intent.extras!!.getSerializable("EXTRA_SELECTED_ITEMS") as HashSet<Int>

        val categoryAdapter = CategoryListAdapter(categoryList, this, selectedCategories) { category: Category -> categoryItemClicked(category)}
        listView = findViewById(R.id.category_list_view)
        listView.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = categoryAdapter
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            selectedCategories = data!!.extras!!.getSerializable("EXTRA_SELECTED_ITEMS") as HashSet<Int>
            val categoryAdapter = CategoryListAdapter(categoryList, this, selectedCategories) { category: Category -> categoryItemClicked(category)}
            listView.apply {
                layoutManager = LinearLayoutManager(this.context)
                adapter = categoryAdapter
            }
        }
    }

    override fun finish() {
        val intent = Intent()
        intent.putExtra("EXTRA_SELECTED_ITEMS", selectedCategories)
        setResult(1, intent)
        super.finish()
    }
    private fun categoryItemClicked(category: Category) {
        val text = category.name
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(applicationContext, selectedCategories.toString(), duration)
        toast.show()
    }
}
