package fi.haltu.harrastuspassi.activities

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.models.Category
import fi.haltu.harrastuspassi.utils.jsonArrayToCategoryList
import org.json.JSONArray
import java.io.IOException
import java.net.URL
import android.widget.Button
import fi.haltu.harrastuspassi.adapters.FilterTagsListAdapter
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.appyvet.materialrangebar.RangeBar
import com.google.firebase.analytics.FirebaseAnalytics
import fi.haltu.harrastuspassi.adapters.DayOfWeekListAdapter
import fi.haltu.harrastuspassi.models.Filters
import fi.haltu.harrastuspassi.utils.loadFilters
import fi.haltu.harrastuspassi.utils.minutesToTime
import fi.haltu.harrastuspassi.utils.saveFilters


class FilterViewActivity : AppCompatActivity(), View.OnClickListener {

    private var hobbyTestResult:ArrayList<String> = ArrayList()
    private var categoryList: ArrayList<Category> = ArrayList()
    private var categoryMap: MutableMap<String, Int> =  mutableMapOf()

    private var filtersOriginal: Filters = Filters() //
    private var filters: Filters = Filters()
    private lateinit var weekRecyclerView: RecyclerView
    private lateinit var tagsRecyclerView: RecyclerView
    private lateinit var rangeBar : RangeBar

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_view)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator (R.drawable.ic_clear_black_24dp)
        supportActionBar!!.title = resources.getString(R.string.filter_button)
        findViewById<Button>(R.id.filterButton).setOnClickListener(this)
        findViewById<ImageButton>(R.id.open_hobby_categories_btn).setOnClickListener(this)

        filters = loadFilters(this)

        filtersOriginal = filters.clone()
        GetCategories().execute()

        hobbyTestResult = idToCategoryName(filters.categories, categoryList)
        tagsRecyclerView = findViewById(R.id.tags_recyclerView)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        ///// TAG FILTER /////
        val straggeredGrid = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        straggeredGrid.gapStrategy = GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        straggeredGrid.canScrollHorizontally()
        tagsRecyclerView.layoutManager = straggeredGrid
        tagsRecyclerView.adapter = FilterTagsListAdapter(hobbyTestResult) { categoryTag: String -> categoryClicked(categoryTag)}

        ///// WEEKDAY FILTER /////
        val dayOfWeekListAdapter = DayOfWeekListAdapter(filters.dayOfWeeks, this) { dayOfWeekId: Int -> weekClicked(dayOfWeekId)}
        weekRecyclerView = findViewById(R.id.day_of_week_list)
        weekRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = dayOfWeekListAdapter

        }
        weekRecyclerView.setHasFixedSize(true)

        ///// TIME SPAN FILTER //////

        rangeBar = findViewById(R.id.time_span_slider)
        val rangeTextLeft : TextView = findViewById(R.id.range_text_left)
        val rangeTextRight: TextView = findViewById(R.id.range_text_right)


        rangeBar.setOnRangeBarChangeListener(object : RangeBar.OnRangeBarChangeListener {
            override fun onRangeChangeListener(
                rangeBar: RangeBar,
                leftPinIndex: Int,
                rightPinIndex: Int,
                leftPinValue: String,
                rightPinValue: String
            ) {

                val rangeStartValue : Int = leftPinValue.toInt()
                val rangeEndValue : Int = rightPinValue.toInt()

                filters.startTimeFrom = rangeStartValue

                if (rangeEndValue == rangeStartValue) {
                    filters.startTimeTo = (rangeEndValue + 60)
                } else {
                    filters.startTimeTo = rangeEndValue
                }

                val startTime = minutesToTime(rangeStartValue)

                val endTime = if (rangeEndValue == rangeStartValue){
                    minutesToTime((rangeEndValue + 60))
                } else {
                    minutesToTime(rangeEndValue)
                }

                rangeTextLeft.text = startTime
                rangeTextRight.text = endTime
            }

            override fun onTouchEnded(rangeBar: RangeBar) {
            }

            override fun onTouchStarted(rangeBar: RangeBar) {
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            try {
                filters = data!!.extras!!.getSerializable("EXTRA_FILTERS") as Filters
                hobbyTestResult = idToCategoryName(filters.categories, categoryList)
                categoryMap = createMap(filters.categories, categoryList)
                tagsRecyclerView.layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
                tagsRecyclerView.adapter = FilterTagsListAdapter(hobbyTestResult) { categoryTag: String -> categoryClicked(categoryTag)}
                val dayWeekListAdapter = DayOfWeekListAdapter(filters.dayOfWeeks, this) { dayOfWeekId: Int -> weekClicked(dayOfWeekId)}
                weekRecyclerView.apply {
                    layoutManager = GridLayoutManager(context, 3)
                    adapter = dayWeekListAdapter
                }
            } catch (e: KotlinNullPointerException) {

            }
        }
    }

    override fun onClick(v: View) {
        val bundle = Bundle()
        bundle.putInt("startTime", filters.startTimeFrom) // Should this be an IntArray?
        bundle.putIntArray("categories", filters.categories.toIntArray())
        bundle.putIntArray("weekday", filters.dayOfWeeks.toIntArray())
        //bundle.putBoolean("isFree", )
        //bundle.putString("municipality",)

        when(v.id) {

            R.id.filterButton -> {
                firebaseAnalytics.logEvent("hobbyFilter", bundle)
                filters.isModified = !filters.isSameValues(filtersOriginal)
                saveFilters(filters, this)
                finish()
            }
            R.id.open_hobby_categories_btn -> {
                val intent = Intent(this, HobbyCategoriesActivity::class.java).apply {
                }
                intent.putExtra("EXTRA_FILTERS", filters)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivityForResult(intent, 1)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return true
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    override fun finish() {
        super.finish()
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
    }

    private fun categoryClicked(categoryTag:String) {
        if(filters.categories.contains(categoryMap[categoryTag])) {
            filters.categories.remove(categoryMap[categoryTag])
            hobbyTestResult = idToCategoryName(filters.categories, categoryList)
            tagsRecyclerView.layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
            tagsRecyclerView.adapter = FilterTagsListAdapter(hobbyTestResult) { categoryTag: String -> categoryClicked(categoryTag)}
        }

        tagsRecyclerView.adapter!!.notifyDataSetChanged()
    }

    private fun weekClicked(dayOfWeekId: Int) {

        if(filters.dayOfWeeks.contains(dayOfWeekId)) {
            filters.dayOfWeeks.remove(dayOfWeekId)
        } else {
            filters.dayOfWeeks.add(dayOfWeekId)
        }

        weekRecyclerView.adapter!!.notifyDataSetChanged()
    }

    fun idToCategoryName(ids: HashSet<Int>, categoriesList: ArrayList<Category>): ArrayList<String> {
        val categories = ArrayList<String>()
        for(category in categoriesList) {
            if(ids.contains(category.id)) {
                categories.add(category.name!!)
            }
        }
        return categories
    }

    private fun createMap(ids: HashSet<Int>, categoriesList: ArrayList<Category>): MutableMap<String, Int>{
        val categoryMap = mutableMapOf<String, Int>()
        for(category in categoriesList) {
            if(ids.contains(category.id)) {
                categoryMap[category.name!!] = category.id!!
            }
        }
        return categoryMap
    }

    internal inner class GetCategories: AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void?): String {
            return try {
                URL(getString(R.string.API_URL) + "hobbycategories/").readText()
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
                    categoryMap = createMap(filters.categories, categoryList)
                    hobbyTestResult = idToCategoryName(filters.categories, categoryList)
                    tagsRecyclerView.layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
                    tagsRecyclerView.adapter = FilterTagsListAdapter(hobbyTestResult) { categoryTag: String -> categoryClicked(categoryTag)}
                    rangeBar.setRangePinsByValue(filters.startTimeFrom.toFloat(), filters.startTimeTo.toFloat())
                }
            }
        }
    }
}
