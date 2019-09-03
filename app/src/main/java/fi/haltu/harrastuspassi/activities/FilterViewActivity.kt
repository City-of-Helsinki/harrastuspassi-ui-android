package fi.haltu.harrastuspassi.activities

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
import android.support.v7.widget.StaggeredGridLayoutManager.VERTICAL
import android.util.Log
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
import com.appyvet.materialrangebar.RangeBar
import fi.haltu.harrastuspassi.adapters.DayOfWeekListAdapter
import fi.haltu.harrastuspassi.models.Filters
import fi.haltu.harrastuspassi.utils.loadFilters
import fi.haltu.harrastuspassi.utils.minutesToTime
import fi.haltu.harrastuspassi.utils.saveFilters


class FilterViewActivity : AppCompatActivity(), View.OnClickListener {

    private var hobbyTestResult:ArrayList<String> = ArrayList()
    private var categoryList: ArrayList<Category> = ArrayList()
    private var categoryMap: MutableMap<String, Int> =  mutableMapOf()
    private var filters: Filters = Filters()
    private lateinit var weekRecyclerView: RecyclerView
    private lateinit var tagsRecyclerView: RecyclerView
    private lateinit var rangeBar : RangeBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_view)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator (R.drawable.ic_clear_black_24dp)
        supportActionBar!!.title = "Suodata"
        findViewById<Button>(R.id.filterButton).setOnClickListener(this)
        findViewById<ImageButton>(R.id.open_hobby_categories_btn).setOnClickListener(this)

        filters = try {
            intent.extras!!.getSerializable("EXTRA_FILTERS") as Filters
        } catch (e: KotlinNullPointerException) {
            loadFilters(this)
        }
        GetCategories().execute()

        hobbyTestResult = idToCategoryName(filters.categories, categoryList)
        tagsRecyclerView = findViewById(R.id.tags_recyclerView)

        ///// TAG FILTER /////
        val straggeredGrid = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        straggeredGrid.gapStrategy = GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        straggeredGrid.canScrollHorizontally()
        tagsRecyclerView.layoutManager = straggeredGrid
        tagsRecyclerView.adapter = FilterTagsListAdapter(hobbyTestResult) { categoryTag: String -> categoryClicked(categoryTag)}

        ///// WEEKDAY FILTER /////
        val dayOfWeekListAdapter = DayOfWeekListAdapter(filters.dayOfWeeks) { dayOfWeekId: Int -> weekClicked(dayOfWeekId)}
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
                filters.startTimeTo = rangeEndValue

                val startTime = minutesToTime(rangeStartValue)
                val endTime = minutesToTime(rangeEndValue)

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
            filters = data!!.extras!!.getSerializable("EXTRA_FILTERS") as Filters
            hobbyTestResult = idToCategoryName(filters.categories, categoryList)
            categoryMap = createMap(filters.categories, categoryList)
            tagsRecyclerView.layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
            tagsRecyclerView.adapter = FilterTagsListAdapter(hobbyTestResult) { categoryTag: String -> categoryClicked(categoryTag)}
            weekRecyclerView.apply {
                layoutManager = GridLayoutManager(context, 3)
                adapter = DayOfWeekListAdapter(filters.dayOfWeeks) { dayOfWeekId: Int -> weekClicked(dayOfWeekId)}
            }
        }
    }

    override fun onClick(v: View) {

        when(v.id) {
            R.id.filterButton -> {
                val intent = Intent(this, MainActivity::class.java)
                saveFilters(filters, this)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
            }
            R.id.open_hobby_categories_btn -> {
                val intent = Intent(this, HobbyCategoriesActivity::class.java).apply {
                }
                intent.putExtra("EXTRA_FILTERS", filters)
                startActivityForResult(intent, 1)
            }
        }
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

        override fun onPreExecute() {
            super.onPreExecute()
        }

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
