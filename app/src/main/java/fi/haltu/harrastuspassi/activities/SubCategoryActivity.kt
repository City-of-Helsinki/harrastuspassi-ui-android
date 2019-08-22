package fi.haltu.harrastuspassi.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.adapters.CategoryListAdapter
import fi.haltu.harrastuspassi.models.Category

class SubCategoryActivity : AppCompatActivity() {
    private var categoryList = ArrayList<Category>()
    private lateinit var listView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_category)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator (R.drawable.ic_clear_black_24dp)
        supportActionBar!!.title = "Valitse harrastus"
        val bundle = intent.getBundleExtra("EXTRA_CATEGORY_BUNDLE")
        categoryList = bundle.getSerializable("CATEGORY_LIST") as ArrayList<Category>

        val categoryAdapter = CategoryListAdapter(categoryList, this) { category: Category -> categoryItemClicked(category)}
        listView = findViewById(R.id.category_list_view)
        listView.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = categoryAdapter
        }
    }

    private fun categoryItemClicked(category: Category) {
        val text = category.name
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()
    }
}
