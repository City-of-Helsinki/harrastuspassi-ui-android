package fi.haltu.harrastuspassi.activities

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.adapters.CategoryListAdapter
import fi.haltu.harrastuspassi.models.Category
import fi.haltu.harrastuspassi.utils.jsonArrayToCategoryList
import org.json.JSONArray import java.io.IOException
import java.net.URL
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fi.haltu.harrastuspassi.models.Filters


class HobbyCategoriesActivity : AppCompatActivity() {

    private var categoryList = ArrayList<Category>()
    private lateinit var listView: RecyclerView
    private var isSaveClicked: Boolean = false
    private var filters: Filters = Filters()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hobby_categories)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        filters = intent.extras!!.getSerializable("EXTRA_FILTERS") as Filters

        if (intent.hasExtra("EXTRA_CATEGORY_BUNDLE")) {
            val bundle = intent.getBundleExtra("EXTRA_CATEGORY_BUNDLE")
            categoryList = bundle.getSerializable("CATEGORY_LIST") as ArrayList<Category>
            supportActionBar!!.title = intent.getStringExtra("EXTRA_CATEGORY_NAME")
        } else {
            supportActionBar!!.title = resources.getString(R.string.choose_hobby)
            GetCategories().execute()
        }

        val categoryAdapter = CategoryListAdapter(categoryList, this, filters) { category: Category -> categoryItemClicked(category)}
        listView = this.findViewById(R.id.category_list_view)
        listView.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = categoryAdapter
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {

            filters = data!!.extras.getSerializable("EXTRA_FILTERS") as Filters
            val categoryAdapter = CategoryListAdapter(categoryList, this, filters) { category: Category -> categoryItemClicked(category)}
            listView.apply {
                layoutManager = LinearLayoutManager(this.context)
                adapter = categoryAdapter
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_filters, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent()
                intent.putExtra("EXTRA_FILTERS", filters)
                setResult(1, intent)
                finish()
            }
            R.id.save-> {
                if(!isSaveClicked) {
                    isSaveClicked = true
                    val intent = Intent(this, FilterViewActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    intent.putExtra("EXTRA_FILTERS", filters)
                    setResult(1, intent)
                    //startActivity(intent)

                    finish()

                }
            }
        }
        return true
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("EXTRA_FILTERS", filters)
        setResult(1, intent)
        finish()
        super.onBackPressed()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    private fun categoryItemClicked(category: Category) {
        if(filters.categories.contains(category.id!!)) {
            filters.categories.remove(category.id!!)

        } else {
            filters.categories.add(category.id!!)
        }

        listView.adapter!!.notifyDataSetChanged()
        val text = category.name
    }

    companion object {
        const val ERROR = "error"
    }

    internal inner class GetCategories: AsyncTask<Void, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: Void?): String {
            return try {
                URL(getString(R.string.API_URL) + "hobbycategories/?include=child_categories&parent=null").readText()
            } catch (e: IOException) {
                return ERROR
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            when(result) {
                ERROR -> {
                    Log.d("HobbyCategory", "Error")
                }
                else -> {
                    val jsonArray = JSONArray(result)
                    categoryList.clear()
                    categoryList.addAll(jsonArrayToCategoryList(jsonArray))
                    listView.adapter!!.notifyDataSetChanged()
                }
            }
        }
    }
}
