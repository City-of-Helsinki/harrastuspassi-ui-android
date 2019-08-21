package fi.haltu.harrastuspassi.activities

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.adapters.CategoryListAdapter
import fi.haltu.harrastuspassi.models.Category
import fi.haltu.harrastuspassi.utils.jsonArrayToCategoryList
import org.json.JSONArray import java.io.IOException
import java.net.URL

class HobbyCategoriesActivity : AppCompatActivity() {
    private var categoryList = ArrayList<Category>()
    private lateinit var listView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hobby_categories)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator (R.drawable.ic_clear_black_24dp)
        supportActionBar!!.title = "Valitse harrastus"

        val categoryAdapter = CategoryListAdapter(categoryList) { category: Category -> categoryItemClicked(category)}
        getCategories().execute()
        listView = this.findViewById(R.id.category_list_view)

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

    companion object {
        const val ERROR = "error"
        const val NO_INTERNET = "no_internet"
    }

    internal inner class getCategories: AsyncTask<Void, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()

        }

        override fun doInBackground(vararg params: Void?): String {
            return try {
                URL(getString(R.string.API_URL) + "/hobbycategories/?include=child_categories&parent=null").readText()
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
