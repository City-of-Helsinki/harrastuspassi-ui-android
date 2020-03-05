package fi.haltu.harrastuspassi.activities

import android.annotation.SuppressLint
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.squareup.picasso.Picasso
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.models.HobbyEvent
import fi.haltu.harrastuspassi.utils.*
import org.json.JSONObject
import java.io.IOException
import java.net.URL


class HobbyDetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var coverImageView: ImageView
    private lateinit var titleTextView: TextView
    private lateinit var organizerTextView: TextView
    private lateinit var tableLayout: TableLayout
    private lateinit var locationNameTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var locationAddress: TextView
    private lateinit var locationZipCode: TextView
    private var hobbyEventID: Int = 0
    private var locationReceived: Boolean = true

    private lateinit var map: GoogleMap
    private lateinit var latLan: LatLng
    private lateinit var favorites: HashSet<Int>
    private lateinit var hobbyEvent: HobbyEvent
    private lateinit var favoriteView: TextView
    private var eventList = ArrayList<HobbyEvent>()
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hobby_detail)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = ""
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        if (intent.extras!!.getSerializable("EXTRA_HOBBY") != null) {
            hobbyEvent = intent.extras!!.getSerializable("EXTRA_HOBBY") as HobbyEvent
            hobbyEventID = hobbyEvent.id
        }
        if (intent.extras!!.getSerializable("EXTRA_HOBBY_EVENT_ID") != null) {
            hobbyEventID = intent.extras!!.getSerializable("EXTRA_HOBBY_EVENT_ID") as Int
        }
        GetHobbyEvent().execute()

        coverImageView = findViewById(R.id.hobby_image)
        titleTextView = findViewById(R.id.hobby_title)
        favoriteView = findViewById(R.id.favorite_icon_button)
        organizerTextView = findViewById(R.id.hobby_organizer)
        tableLayout = findViewById(R.id.tableLayout)

        locationNameTextView = findViewById(R.id.promotion_location)
        descriptionTextView = findViewById(R.id.description_text)
        locationAddress = findViewById(R.id.promotion_location_address)
        locationZipCode = findViewById(R.id.promotion_location_zipcode)

        //Loads favorite id:s
        favorites = loadFavorites(this)
        if (favorites.contains(hobbyEventID)) {
            favoriteView.background.setTint(ContextCompat.getColor(this, R.color.hobbyPurple))
        }
        favoriteView.setOnClickListener {
            if (favorites.contains(hobbyEventID)) {
                favoriteView.background.setTint(
                    ContextCompat.getColor(
                        this,
                        R.color.common_google_signin_btn_text_light_disabled
                    )
                )
                favorites.remove(hobbyEventID)
            } else {
                // FIREBASE ANALYTICS
                val bundle = Bundle()
                bundle.putInt("hobbyId", hobbyEvent.hobby.id)
                bundle.putString("hobbyName", hobbyEvent.hobby.name)
                if (hobbyEvent.hobby.organizer != null) {
                    bundle.putString("organizerName", hobbyEvent.hobby.organizer!!.name)
                } else {
                    bundle.putString("organizerName", "no organization")
                }
                if (hobbyEvent.hobby.municipality != null) {
                    bundle.putString("municipality", hobbyEvent.hobby.municipality)
                } else {
                    bundle.putString("municipality", "Haltu")
                }
                favoriteView.background.setTint(ContextCompat.getColor(this, R.color.hobbyPurple))
                favorites.add(hobbyEventID)

                firebaseAnalytics.logEvent("addFavourite", bundle)
            }

            saveFavorite(favorites, this)
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_hobby_detail, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item!!.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
            R.id.action_share -> {
                FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLink(Uri.parse("https://hpassi.page.link/share/?hobbyEvent=$hobbyEventID"))
                    .setDomainUriPrefix("https://hpassi.page.link")
                    .setSocialMetaTagParameters(
                        DynamicLink.SocialMetaTagParameters.Builder()
                            .setTitle(hobbyEvent.hobby.name)
                            .setImageUrl(Uri.parse(hobbyEvent.hobby.imageUrl))
                            .build()
                    )
                    .setNavigationInfoParameters(
                        DynamicLink.NavigationInfoParameters.Builder()
                            .setForcedRedirectEnabled(true).build()
                    )
                    .setAndroidParameters(
                        DynamicLink.AndroidParameters.Builder("fi.haltu.harrastuspassi")
                            .setMinimumVersion(6)
                            .build()
                    )
                    .setIosParameters(
                        DynamicLink.IosParameters.Builder("fi.haltu.harrastuspassi")
                            .setAppStoreId("1473780933")
                            .setMinimumVersion("0.6.0")
                            .build()
                    )
                    .buildShortDynamicLink()
                    .addOnSuccessListener { result ->
                        ShareCompat.IntentBuilder.from(this)
                            .setType("text/plain")
                            .setChooserTitle("Share URL")
                            .setText(result.shortLink.toString())
                            .startChooser()
                    }
                    .addOnCompleteListener { task ->
                        Log.d("DynamicLink", "Result: ${task.exception}")

                    }

                // FIREBASE ANALYTICS
                val bundle = Bundle()
                bundle.putInt("hobbyId", hobbyEvent.hobby.id)
                bundle.putString("hobbyName", hobbyEvent.hobby.name)
                if (hobbyEvent.hobby.organizer != null) {
                    bundle.putString("provider", hobbyEvent.hobby.organizer!!.name)
                } else {
                    bundle.putString("provider", "no provider")
                }
                if (hobbyEvent.hobby.municipality != null) {
                    bundle.putString("municipality", hobbyEvent.hobby.municipality)
                } else {
                    bundle.putString("municipality", "Haltu")
                }

                firebaseAnalytics.logEvent("shareHobby", bundle)
            }
        }

        return true
    }

    private fun setHobbyDetailView(hobbyEvents: ArrayList<HobbyEvent>) {
        val filters = loadFilters(this)

        // FIREBASE ANALYTICS
        val bundle = Bundle()
        bundle.putInt("hobbyId", hobbyEvent.hobby.id)
        bundle.putString("hobbyName", hobbyEvent.hobby.name)
        if (hobbyEvent.hobby.organizer != null) {
            bundle.putString("organizerName", hobbyEvent.hobby.organizer!!.name)
        } else {
            bundle.putString("organizerName", "no organization")
        }
        for (index in 0 until filters.categories.size) {
            bundle.putInt("filterCategory$index", filters.categories.toIntArray()[index])
        }
        bundle.putString(
            "coordinates",
            "${hobbyEvent.hobby.location.lat}, ${hobbyEvent.hobby.location.lon}"
        )
        bundle.putBoolean("free", hobbyEvent.hobby.isFree)
        bundle.putString("postalCode", hobbyEvent.hobby.location.zipCode)
        if (hobbyEvent.hobby.municipality != null) {
            bundle.putString("municipality", hobbyEvent.hobby.municipality)
        } else {
            bundle.putString("municipality", "Haltu")
        }

        firebaseAnalytics.logEvent("viewHobby", bundle)

        //COVER IMAGE
        Picasso.with(this)
            .load(hobbyEvents[0].hobby.imageUrl)
            .placeholder(R.drawable.harrastuspassi_lil_kel)
            .error(R.drawable.harrastuspassi_lil_kel)
            .into(coverImageView)

        //TITLE
        titleTextView.text = hobbyEvents[0].hobby.name
        //ORGANIZER
        if (hobbyEvents[0].hobby.organizer != null) {
            organizerTextView.text = hobbyEvents[0].hobby.organizer!!.name
        } else {
            organizerTextView.text = getString(R.string.not_specified)
        }

        //LOCATION
        locationNameTextView.text = hobbyEvents[0].hobby.location.name
        locationAddress.text = hobbyEvents[0].hobby.location.address
        locationZipCode.text = hobbyEvents[0].hobby.location.zipCode

        //TABLE
        for (hobbyEvent in hobbyEvents) {
            var row: TableRow =
                LayoutInflater.from(this).inflate(R.layout.table_row, null) as TableRow
            row.findViewById<TextView>(R.id.week_day).text =
                idToWeekDay(hobbyEvent.startWeekday, this)
            Log.d("Table", formatDate(hobbyEvent.startDate))
            row.findViewById<TextView>(R.id.start_date).text = formatDate(hobbyEvent.startDate)
            row.findViewById<TextView>(R.id.time).text =
                convertToTimeRange(hobbyEvent.startTime, hobbyEvent.endTime)
            tableLayout.addView(row)
        }

        //DESCRIPTION
        descriptionTextView.text = hobbyEvents[0].hobby.description

        //MAP
        if (hobbyEvents[0].hobby.location.lat != null) {
            latLan =
                LatLng(hobbyEvents[0].hobby.location.lat!!, hobbyEvents[0].hobby.location.lon!!)
        } else {
            locationReceived = false
        }
        if (locationReceived) {
            val markerPos = latLan
            map.addMarker(
                MarkerOptions()
                    .position(markerPos)
                    .icon(bitmapDescriptorFromVector(this, R.drawable.ic_location_24dp))
            )
            map.moveCamera(CameraUpdateFactory.newLatLng(markerPos))
        }
    }

    companion object {
        const val ERROR = "error"
    }

    internal inner class GetHobbyEvent : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String {
            return try {
                URL(getString(R.string.API_URL) + "hobbyevents/$hobbyEventID/?&include=hobby_detail&include=location_detail&include=organizer_detail").readText()
            } catch (e: IOException) {
                return ERROR
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            when (result) {
                ERROR -> {
                    showErrorDialog()
                }

                else -> {
                    val jsonArray = JSONObject(result)
                    val sObject = jsonArray.toString()
                    val eventObject = JSONObject(sObject)
                    val hobbyEvent = HobbyEvent(eventObject)
                    GetHobbyEvents(hobbyEvent.hobby.id).execute()
                }
            }
        }
    }

    internal inner class GetHobbyEvents(hobbyID:Int) : AsyncTask<Void, Void, String>() {
        private val hobbyID = hobbyID
        override fun doInBackground(vararg params: Void?): String {
            return try {
                URL(getString(R.string.API_URL) + "hobbyevents/?hobby=$hobbyID&include=hobby_detail&include=location_detail&include=organizer_detail").readText()
            } catch (e: IOException) {
                return ERROR
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            when (result) {
                ERROR -> {
                    showErrorDialog()
                }

                else -> {

                    val results = JSONObject(result)
                    val jsonArray = results.getJSONArray("results")
                    for (i in 0 until jsonArray.length()) {
                        val sObject = jsonArray.get(i).toString()
                        val eventObject = JSONObject(sObject)
                        val hobbyEvent = HobbyEvent(eventObject)
                        eventList.add(hobbyEvent)
                    }

                    if (eventList.isEmpty()) {

                        showErrorDialog()
                    } else {
                        hobbyEvent = eventList[0]
                    }
                    setHobbyDetailView(eventList)

                }
            }
        }
    }

    private fun showErrorDialog() {
        val builder = AlertDialog.Builder(this@HobbyDetailActivity)
        builder.setMessage(getString(R.string.error_try_again_later))
        builder.show()
    }
}


