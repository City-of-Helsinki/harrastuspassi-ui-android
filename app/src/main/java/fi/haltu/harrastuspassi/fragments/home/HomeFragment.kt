package fi.haltu.harrastuspassi.fragments.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.activities.MainActivity
import fi.haltu.harrastuspassi.adapters.CategorySearchAdapter
import fi.haltu.harrastuspassi.models.Category
import fi.haltu.harrastuspassi.utils.jsonArrayToSingleCategoryList
import fi.haltu.harrastuspassi.utils.loadFilters
import fi.haltu.harrastuspassi.utils.saveFilters
import org.json.JSONArray
import java.io.IOException
import java.net.URL


class HomeFragment : Fragment() {
    lateinit var searchEditText: AutoCompleteTextView
    lateinit var searchContainer: ConstraintLayout
    lateinit var searchIcon: TextView
    var categoryList = ArrayList<Category>()
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        val parentActivity = this.activity as MainActivity
        firebaseAnalytics = parentActivity.firebaseAnalytics

        //SEARCH
        searchEditText = view.findViewById(R.id.home_search)
        searchContainer = view.findViewById(R.id.search_container)
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus) {
                searchContainer.setBackgroundColor(ContextCompat.getColor(this.context!!, R.color.white))
            } else {
                searchContainer.setBackgroundColor(ContextCompat.getColor(this.context!!, R.color.white60))
            }
        }
        searchEditText.setOnKeyListener { _, keyCode, event ->
            // User presses "enter" on keyboard
            if ((event.action == KeyEvent.ACTION_DOWN) &&
                (keyCode == KeyEvent.KEYCODE_ENTER)) {
                search(searchEditText.text.toString())
                view.clearFocus()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        searchIcon = view.findViewById(R.id.home_search_icon)
        searchIcon.setOnClickListener {
            search(searchEditText.text.toString())
        }

        GetCategories().execute()

        return view
    }


    @SuppressLint("DefaultLocale")
    private fun search(searchStr: String) {
        var isInList = false
        var simplifiedStr = searchStr.trimEnd().toLowerCase()
        if(simplifiedStr != "") {
            for(category in categoryList) {
                if(category.name.toLowerCase().contains(simplifiedStr)) {

                    // FIREBASE ANALYTICS
                    val bundle = Bundle()
                    bundle.putString("categoryName", category.name)
                    firebaseAnalytics.logEvent("frontPageSearch", bundle)

                    var filters = loadFilters(activity!!)

                    filters.categories.clear()
                    filters.categories.add(category.id!!)
                    filters.isListUpdated = false
                    saveFilters(filters, activity!!)
                    var mainActivity = context as MainActivity
                    mainActivity.performListClick()
                    isInList = true
                    break
                }
            }
            if(!isInList) {
                Toast.makeText(this.context, "Ei tuloksia", Toast.LENGTH_SHORT).show()
            }
        }
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
                URL(getString(R.string.API_URL) + "hobbycategories/").readText()
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
                    categoryList = jsonArrayToSingleCategoryList(jsonArray)
                    searchEditText.setAdapter(CategorySearchAdapter(context!!, android.R.layout.simple_dropdown_item_1line, categoryList))
                    searchEditText.threshold = 2
                    searchEditText.setOnItemClickListener { _, _, _, id ->
                        var filters = loadFilters(activity!!)
                        filters.categories.clear()
                        filters.categories.add(id.toInt())
                        saveFilters(filters, activity!!)
                        var mainActivity = context as MainActivity
                        mainActivity.performListClick()
                    }
                }
            }
        }
    }
}
