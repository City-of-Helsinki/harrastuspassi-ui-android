package fi.haltu.harrastuspassi.activities

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.CheckBox
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
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.adapters.DayOfWeekListAdapter
import fi.haltu.harrastuspassi.adapters.FilterTagsListAdapter
import fi.haltu.harrastuspassi.models.Category
import fi.haltu.harrastuspassi.models.Filters
import fi.haltu.harrastuspassi.utils.jsonArrayToCategoryList
import fi.haltu.harrastuspassi.utils.loadFilters
import fi.haltu.harrastuspassi.utils.minutesToTime
import fi.haltu.harrastuspassi.utils.saveFilters
import org.json.JSONArray
import java.io.IOException
import java.net.URL


class FilterViewActivity : AppCompatActivity(), View.OnClickListener {

    private var hobbyTestResult: ArrayList<Category> = ArrayList()
    private var categoryList: ArrayList<Category> = ArrayList()
    private var categoryMap: MutableMap<String, Int> = mutableMapOf()

    private var filtersOriginal: Filters = Filters() //
    private var filters: Filters = Filters()
    private lateinit var weekRecyclerView: RecyclerView
    private lateinit var tagsRecyclerView: RecyclerView
    private lateinit var rangeBar: RangeBar
    private lateinit var isFreeCheckBox: CheckBox
    //private lateinit var showHobbiesWithPromotionCheckBox: CheckBox
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_view)

        /*
         * There's probably a better way to change the font on the support action bar, but I was
         * unable to find a way to change the font using styles.xml. This solution is copy-pasted
         * from
         * https://stackoverflow.com/questions/8607707/how-to-set-a-custom-font-in-the-actionbar-title
         */

        // Action Bar Custom View - Start

        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        val inflater = LayoutInflater.from(this)
        val v: View = inflater.inflate(R.layout.custom_action_bar, null)

        (v.findViewById<View>(R.id.title) as TextView).text = resources.getString(R.string.filter_button)

        supportActionBar!!.customView = v

        // Action Bar Custom View - End

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_clear_black_24dp)

        findViewById<Button>(R.id.filterButton).setOnClickListener(this)
        findViewById<ImageButton>(R.id.open_hobby_categories_btn).setOnClickListener(this)

        filters = loadFilters(this)

        filtersOriginal = filters.clone()
        GetCategories().execute()

        hobbyTestResult = idToCategoryList(filters.categories, categoryList)
        tagsRecyclerView = findViewById(R.id.tags_recyclerView)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        ///// TAG FILTER /////
        val straggeredGrid = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        straggeredGrid.gapStrategy = GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        straggeredGrid.canScrollHorizontally()
        tagsRecyclerView.layoutManager = straggeredGrid
        tagsRecyclerView.adapter = FilterTagsListAdapter(hobbyTestResult) { category: Category ->
            categoryClicked(category)
        }

        ///// WEEKDAY FILTER /////
        val dayOfWeekListAdapter =
            DayOfWeekListAdapter(filters.dayOfWeeks, this) { dayOfWeekId: Int ->
                weekClicked(dayOfWeekId)
            }
        weekRecyclerView = findViewById(R.id.day_of_week_list)
        weekRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = dayOfWeekListAdapter
        }
        weekRecyclerView.setHasFixedSize(true)

        ///// TIME SPAN FILTER //////

        rangeBar = findViewById(R.id.time_span_slider)
        val rangeTextLeft: TextView = findViewById(R.id.range_text_left)
        val rangeTextRight: TextView = findViewById(R.id.range_text_right)


        rangeBar.setOnRangeBarChangeListener(object : RangeBar.OnRangeBarChangeListener {
            override fun onRangeChangeListener(
                rangeBar: RangeBar,
                leftPinIndex: Int,
                rightPinIndex: Int,
                leftPinValue: String,
                rightPinValue: String
            ) {

                val rangeStartValue: Int = leftPinValue.toInt()
                val rangeEndValue: Int = rightPinValue.toInt()

                filters.startTimeFrom = rangeStartValue

                if (rangeEndValue == rangeStartValue) {
                    filters.startTimeTo = (rangeEndValue + 60)
                } else {
                    filters.startTimeTo = rangeEndValue
                }

                val startTime = minutesToTime(rangeStartValue)

                val endTime = if (rangeEndValue == rangeStartValue) {
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

        //IS FREE CHECK BOX
        isFreeCheckBox = findViewById(R.id.is_free_check_box)
        isFreeCheckBox.isChecked = filters.showFree
            isFreeCheckBox.setOnClickListener {
            filters.showFree = isFreeCheckBox.isChecked
        }
        /*SHOW_HOBBIES_WITH_PROMOTION_CHECK_BOX
        showHobbiesWithPromotionCheckBox = findViewById(R.id.has_promotion_check_box)
        showHobbiesWithPromotionCheckBox.setOnClickListener {
            //filters.showHobbiesWithPromotion = showHobbiesWithPromotionCheckBox.isChecked
        }*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            try {
                filters = data!!.extras!!.getSerializable("EXTRA_FILTERS") as Filters
                hobbyTestResult = idToCategoryList(filters.categories, categoryList)
                categoryMap = createMap(filters.categories, categoryList)
                tagsRecyclerView.layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
                tagsRecyclerView.adapter =
                    FilterTagsListAdapter(hobbyTestResult) { category: Category ->
                        categoryClicked(category)
                    }
                val dayWeekListAdapter = DayOfWeekListAdapter(
                    filters.dayOfWeeks,
                    this
                ) { dayOfWeekId: Int -> weekClicked(dayOfWeekId) }
                weekRecyclerView.apply {
                    layoutManager = GridLayoutManager(context, 3)
                    adapter = dayWeekListAdapter
                }
            } catch (e: KotlinNullPointerException) {

            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {

            R.id.filterButton -> {
                filters.isModified = !filters.isSameValues(filtersOriginal)
                saveFilters(filters, this)
                setResult(2, intent)
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

    private fun categoryClicked(category: Category) {
        if (filters.categories.contains(categoryMap[category.name])) {
            filters.categories.remove(categoryMap[category.name])
            hobbyTestResult = idToCategoryList(filters.categories, categoryList)
            tagsRecyclerView.layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
            tagsRecyclerView.adapter =
                FilterTagsListAdapter(hobbyTestResult) { category: Category ->
                    categoryClicked(category)
                }
        }

        tagsRecyclerView.adapter!!.notifyDataSetChanged()
    }

    private fun weekClicked(dayOfWeekId: Int) {

        if (filters.dayOfWeeks.contains(dayOfWeekId)) {
            filters.dayOfWeeks.remove(dayOfWeekId)
        } else {
            filters.dayOfWeeks.add(dayOfWeekId)
        }

        weekRecyclerView.adapter!!.notifyDataSetChanged()
    }

    fun idToCategoryList(
        ids: HashSet<Int>,
        categoriesList: ArrayList<Category>
    ): ArrayList<Category> {
        val categories = ArrayList<Category>()
        for (category in categoriesList) {
            if (ids.contains(category.id)) {
                categories.add(category)
            }
        }
        return categories
    }

    private fun createMap(
        ids: HashSet<Int>,
        categoriesList: ArrayList<Category>
    ): MutableMap<String, Int> {
        val categoryMap = mutableMapOf<String, Int>()
        for (category in categoriesList) {
            if (ids.contains(category.id)) {
                categoryMap[category.name] = category.id!!
            }
        }
        return categoryMap
    }

    internal inner class GetCategories : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void?): String {
            return try {
                URL(getString(R.string.API_URL) + "hobbycategories/").readText()
            } catch (e: IOException) {
                return HobbyCategoriesActivity.ERROR
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            when (result) {
                HobbyCategoriesActivity.ERROR -> {
                    Log.d("HobbyCategory", "Error")
                }
                else -> {
                    val jsonArray = JSONArray(result)
                    categoryList.clear()
                    categoryList.addAll(jsonArrayToCategoryList(jsonArray))
                    categoryMap = createMap(filters.categories, categoryList)
                    hobbyTestResult = idToCategoryList(filters.categories, categoryList)
                    tagsRecyclerView.layoutManager = StaggeredGridLayoutManager(2, VERTICAL)
                    tagsRecyclerView.adapter =
                        FilterTagsListAdapter(hobbyTestResult) { category: Category ->
                            categoryClicked(category)
                        }
                    rangeBar.setRangePinsByValue(
                        filters.startTimeFrom.toFloat(),
                        filters.startTimeTo.toFloat()
                    )
                }
            }
        }
    }
}
