package fi.haltu.harrastuspassi.activities

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.models.Category
import fi.haltu.harrastuspassi.utils.jsonArrayToCategoryList
import org.json.JSONArray
import java.io.IOException
import java.net.URL
import android.widget.Button
import fi.haltu.harrastuspassi.models.Filters
import fi.haltu.harrastuspassi.utils.loadFilters
import fi.haltu.harrastuspassi.utils.saveFilters


class FilterViewActivity : AppCompatActivity() {
    private var hobbyTestResult:ArrayList<String> = ArrayList()
    private lateinit var filterButton: Button
    private lateinit var categoryList: ArrayList<Category>
    private var filters: Filters = Filters()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_view)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator (R.drawable.ic_clear_black_24dp)
        supportActionBar!!.title = "Suodata"

        categoryList = ArrayList()
        try {
            filters.categories = intent.extras!!.getSerializable("EXTRA_SELECTED_ITEMS") as HashSet<Int>
        } catch (e: KotlinNullPointerException) {
            filters = loadFilters(this)
        }
        getCategories().execute()

        hobbyTestResult = idToCategoryName(filters.categories, categoryList)

        filterButton = findViewById(R.id.filterButton)
        filterButton.setOnClickListener{
            Toast.makeText(applicationContext, filters.categories.toString(), Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            saveFilters(filters, this)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            filters.categories = data!!.extras!!.getSerializable("EXTRA_SELECTED_ITEMS") as HashSet<Int>
            hobbyTestResult = idToCategoryName(filters.categories, categoryList)
            Toast.makeText(applicationContext,hobbyTestResult.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    fun openCategories (view: View) {
        val intent = Intent(this, HobbyCategoriesActivity::class.java).apply {
        }
        intent.putExtra( "EXTRA_SELECTED_ITEMS",filters.categories)
        startActivityForResult(intent, 1)
    }

    fun idToCategoryName(ids: HashSet<Int>, categoriesList: ArrayList<Category>): ArrayList<String> {
        var categories = ArrayList<String>()

        for(category in categoriesList) {
            if(ids.contains(category.id)) {
                categories.add(category.name!!)
            }
        }
        return categories
    }

    internal inner class getCategories: AsyncTask<Void, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: Void?): String {
            return try {
                URL(getString(R.string.API_URL) + "/hobbycategories/").readText()
            } catch (e: IOException) {
                return HobbyCategoriesActivity.ERROR
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            when(result) {
                HobbyCategoriesActivity.ERROR -> {
                    Log.d("HobbyCategory", "Error")
                }
                else -> {
                    val jsonArray = JSONArray(result)
                    categoryList.clear()
                    categoryList.addAll(jsonArrayToCategoryList(jsonArray))
                    hobbyTestResult.clear()
                    hobbyTestResult = idToCategoryName(filters.categories, categoryList)
                    Toast.makeText(applicationContext, hobbyTestResult.toString(), Toast.LENGTH_SHORT).show()
                    //recyclerList.adapter!!.notifyDataSetChanged()
                    //TODO ^ Remember to refresh recycler list by using that func :)^
                }
            }
        }
    }
}
