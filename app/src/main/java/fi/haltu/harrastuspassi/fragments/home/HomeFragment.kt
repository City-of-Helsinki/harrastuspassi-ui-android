package fi.haltu.harrastuspassi.fragments.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.analytics.FirebaseAnalytics
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.activities.MainActivity
import fi.haltu.harrastuspassi.adapters.CategorySearchAdapter
import fi.haltu.harrastuspassi.fragments.HobbyEventListFragment
import fi.haltu.harrastuspassi.fragments.SettingsFragment
import fi.haltu.harrastuspassi.models.Category
import fi.haltu.harrastuspassi.models.Filters
import fi.haltu.harrastuspassi.models.Settings
import fi.haltu.harrastuspassi.utils.*
import org.json.JSONArray
import java.io.IOException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment(), LocationListener {
    lateinit var searchEditText: AutoCompleteTextView
    lateinit var searchContainer: ConstraintLayout
    lateinit var searchIcon: TextView
    lateinit var promotionsFragment: HomePromotionsFragment
    lateinit var hobbiesFragment: HomeHobbiesFragment
    var filters = Filters()
    var settings = Settings()
    private var locationManager: LocationManager? = null
    var categoryList = ArrayList<Category>()
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        val parentActivity = this.activity as MainActivity
        firebaseAnalytics = parentActivity.firebaseAnalytics
        hobbiesFragment = childFragmentManager!!.findFragmentById(R.id.home_hobbies_fragment) as HomeHobbiesFragment
        promotionsFragment = childFragmentManager!!.findFragmentById(R.id.home_promotions_fragment) as HomePromotionsFragment

        filters = loadFilters(activity!!)
        settings = loadSettings(activity!!)

        //SEARCH
        searchEditText = view.findViewById(R.id.home_search)
        searchContainer = view.findViewById(R.id.search_container)
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                searchContainer.setBackgroundColor(
                    ContextCompat.getColor(
                        this.context!!,
                        R.color.white
                    )
                )
            } else {
                searchContainer.setBackgroundColor(
                    ContextCompat.getColor(
                        this.context!!,
                        R.color.white80
                    )
                )
            }
        }
        searchEditText.setOnKeyListener { _, keyCode, event ->
            // User presses "enter" on keyboard
            if ((event.action == KeyEvent.ACTION_DOWN) &&
                (keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
                search(searchEditText.text.toString())
                KeyboardUtils.hideKeyboard(activity!!)
                view.clearFocus()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        searchIcon = view.findViewById(R.id.home_search_icon)
        searchIcon.setOnClickListener {
            search(searchEditText.text.toString())
            KeyboardUtils.hideKeyboard(activity!!)
        }

        // Asking permission to use users location
        val settings = loadSettings(activity!!)
            try {
                locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
                locationManager?.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0L,
                    0f,
                    this
                )
                locationManager?.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0L,
                    0f,
                    this
                )
            } catch (ex: SecurityException) {
                if(settings.isFirstTime) {
                    this.requestPermissions(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        SettingsFragment.LOCATION_PERMISSION
                    )
                    settings.isFirstTime = false
                    saveSettings(settings, activity!!)
                }
            }

        GetCategories().execute()

        return view
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if(!hidden) {
            updateChildFragments()
        }
        super.onHiddenChanged(hidden)
    }
    override fun onLocationChanged(location: Location) {
        filters.latitude = location.latitude
        filters.longitude = location.longitude
        filters.isModified = true
        saveFilters(filters, activity!!)
        updateChildFragments()
        locationManager!!.removeUpdates(this)
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}

    private fun updateChildFragments() {
        hobbiesFragment.updateContent()
        promotionsFragment.updateContent()
    }

    @SuppressLint("DefaultLocale")
    private fun search(searchStr: String) {
        var simplifiedStr = searchStr.trimEnd().toLowerCase()
        if (simplifiedStr != "") {
            for (category in categoryList) {

                // FIREBASE ANALYTICS
                val bundle = Bundle()
                bundle.putString("categoryName", category.name)
                firebaseAnalytics.logEvent("frontPageSearch", bundle)

                var filters = loadFilters(activity!!)

                filters.categories.clear()
                //filters.categories.add(category.id!!)
                filters.searchText = searchStr
                filters.isListUpdated = false
                saveFilters(filters, activity!!)
                searchEditText.text.clear()
                var mainActivity = context as MainActivity
                mainActivity.performListClick()
                break
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            SettingsFragment.LOCATION_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //user accepted permission
                    try {
                        settings.useCurrentLocation = true
                        saveSettings(settings, activity!!)

                        // Request location updates
                        locationManager?.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            0L,
                            0f,
                            this
                        )
                        locationManager?.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            0L,
                            0f,
                            this
                        )
                    } catch (ex: SecurityException) {
                        settings.useCurrentLocation = false
                        saveSettings(settings, activity!!)
                    }
                } else if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    settings.useCurrentLocation = false
                    saveSettings(settings, activity!!)
                }
                return
            }

            else -> {
                settings.useCurrentLocation = false
                saveSettings(settings, activity!!)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        const val ERROR = "error"
    }

    internal inner class GetCategories : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void?): String {
            return try {
                URL(getString(R.string.API_URL) + "hobbycategories/").readText()
            } catch (e: IOException) {
                return ERROR
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            when (result) {
                ERROR -> {
                    Log.d("HobbyCategory", "Error")
                }
                else -> {
                    val jsonArray = JSONArray(result)

                    categoryList.clear()
                    categoryList = jsonArrayToSingleCategoryList(jsonArray)
                    searchEditText.setAdapter(
                        CategorySearchAdapter(
                            context!!,
                            android.R.layout.simple_list_item_1,
                            categoryList
                        )
                    )
                    searchEditText.threshold = 2
                    searchEditText.setOnItemClickListener { _, _, _, id ->
                        var filters = loadFilters(activity!!)
                        filters.categories.clear()
                        filters.searchText = getCategoryNameById(categoryList, id.toInt())

                        saveFilters(filters, activity!!)
                        searchEditText.text.clear()

                        var mainActivity = context as MainActivity
                        mainActivity.performListClick()
                    }
                }
            }
        }
        private fun getCategoryNameById(categories: ArrayList<Category>, categoryId: Int): String {
            for(category in categories) {
                if(category.id == categoryId) {
                    val currentLanguage = Locale.getDefault().language
                    return if(currentLanguage == "fi") {
                        category.nameFi
                    } else if(currentLanguage == "sv") {
                        category.nameSv
                    } else {
                        if(category.nameEn.isNullOrEmpty()) {
                            category.name
                        } else {
                            category.nameEn
                        }
                    }
                }
            }
            return ""
        }
    }
}
