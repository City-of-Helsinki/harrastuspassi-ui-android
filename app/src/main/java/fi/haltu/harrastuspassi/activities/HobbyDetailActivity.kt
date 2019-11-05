package fi.haltu.harrastuspassi.activities

import android.annotation.SuppressLint
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
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
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.squareup.picasso.Picasso
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.models.HobbyEvent
import fi.haltu.harrastuspassi.utils.bitmapDescriptorFromVector
import fi.haltu.harrastuspassi.utils.idToWeekDay
import fi.haltu.harrastuspassi.utils.loadFavorites
import fi.haltu.harrastuspassi.utils.saveFavorite
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashSet


class HobbyDetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var coverImageView: ImageView
    private lateinit var titleTextView: TextView
    private lateinit var organizerTextView: TextView
    private lateinit var dayOfWeekTextView: TextView
    private lateinit var startTimeTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var locationNameTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var locationAddress: TextView
    private lateinit var locationZipCode: TextView
    private var id: Int = 0
    private  var locationReceived: Boolean = true

    private lateinit var map: GoogleMap
    private lateinit var latLan: LatLng
    private lateinit var favorites: HashSet<Int>
    private lateinit var hobbyEvent: HobbyEvent
    private lateinit var favoriteView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hobby_detail)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = ""

        if(intent.extras!!.getSerializable("EXTRA_HOBBY") != null) {
            hobbyEvent = intent.extras!!.getSerializable("EXTRA_HOBBY") as HobbyEvent
            id = hobbyEvent.id
        } else if(intent.extras!!.getSerializable("EXTRA_HOBBY_ID") != null) {
            id = intent.extras!!.getSerializable("EXTRA_HOBBY_ID") as Int
        }
        GetHobbyEvent().execute()

        coverImageView = findViewById(R.id.hobby_image)
        titleTextView = findViewById(R.id.hobby_title)
        favoriteView = findViewById(R.id.favorite_icon_button)
        organizerTextView = findViewById(R.id.hobby_organizer)
        dateTextView = findViewById(R.id.date)
        dayOfWeekTextView = findViewById(R.id.date_time)
        startTimeTextView = findViewById(R.id.start_time)

        locationNameTextView = findViewById(R.id.location)
        descriptionTextView = findViewById(R.id.description_text)
        locationAddress = findViewById(R.id.location_address)
        locationZipCode = findViewById(R.id.location_zipcode)

        //Loads favorite id:s
        favorites = loadFavorites(this)
        if(favorites.contains(id)) {
            favoriteView.background.setTint(ContextCompat.getColor(this, R.color.hobbyYellow))
        }
        favoriteView.setOnClickListener {
            if(favorites.contains(id)) {
                favoriteView.background.setTint(ContextCompat.getColor(this, R.color.common_google_signin_btn_text_light_disabled))
                favorites.remove(id)
            } else {
                favoriteView.background.setTint(ContextCompat.getColor(this, R.color.hobbyYellow))
                favorites.add(id)
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
                .setLink(Uri.parse("https://hpassi.page.link/share/?hobbyEvent=$id"))
                .setDomainUriPrefix("https://hpassi.page.link")
                .setSocialMetaTagParameters(DynamicLink.SocialMetaTagParameters.Builder()
                    .setTitle(hobbyEvent.hobby.name)
                    .setImageUrl(Uri.parse(hobbyEvent.hobby.imageUrl))
                    .build()
                )
                .setNavigationInfoParameters(DynamicLink.NavigationInfoParameters.Builder()
                    .setForcedRedirectEnabled(true).build()
                )
                .setAndroidParameters(
                    DynamicLink.AndroidParameters.Builder("fi.haltu.harrastuspassi")
                        .setMinimumVersion(6)
                        .build())
                .setIosParameters(
                    DynamicLink.IosParameters.Builder("fi.haltu.harrastuspassi")
                        .setAppStoreId("1473780933")
                        .setMinimumVersion("0.6.0")
                        .build())
                .buildShortDynamicLink()
                .addOnSuccessListener { result ->
                    ShareCompat.IntentBuilder.from(this)
                        .setType("text/plain")
                        .setChooserTitle("Share URL")
                        .setText(result.shortLink.toString())
                        .startChooser()
                }
            }
        }

        return true
    }

    private fun setHobbyDetailView(hobbyEvent: HobbyEvent) {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.US)
        var date = getString(R.string.not_specified)
        try {
            date = formatter.format(parser.parse(hobbyEvent.startDate))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val timeParser = SimpleDateFormat("HH:mm:ss", Locale.US)
        val timeFormatter = SimpleDateFormat("HH.mm", Locale.US)
        var startTime = getString(R.string.not_specified)
        var endTime = getString(R.string.not_specified)

        try {
            startTime = timeFormatter.format(timeParser.parse(hobbyEvent.startTime))
            endTime =  timeFormatter.format(timeParser.parse(hobbyEvent.endTime))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //TITLE
        titleTextView.text = hobbyEvent.hobby.name
        //ORGANIZER
        if (hobbyEvent.hobby.organizer != null) {
            organizerTextView.text = hobbyEvent.hobby.organizer!!.name
        } else {
            organizerTextView.text = getString(R.string.not_specified)
        }
        //WEEKDAY
        if (hobbyEvent.startWeekday != 0) {
            dayOfWeekTextView.text = idToWeekDay(hobbyEvent.startWeekday, this)
        } else {
            dayOfWeekTextView.text = "?"
        }
        //START TIME
        startTimeTextView.text = "$startTime - $endTime"
        //DATE
        dateTextView.text = date
        //DESCRIPTION
        descriptionTextView.text = hobbyEvent.hobby.description
        //COVER IMAGE
        Picasso.with(this)
            .load(hobbyEvent.hobby.imageUrl)
            .placeholder(R.drawable.harrastuspassi_kel_lil_2)
            .error(R.drawable.harrastuspassi_kel_lil_2)
            .into(coverImageView)
        //LOCATION
        locationNameTextView.text = hobbyEvent.hobby.location.name
        locationAddress.text = hobbyEvent.hobby.location.address
        locationZipCode.text = hobbyEvent.hobby.location.zipCode
        //MAP
        if (hobbyEvent.hobby.location.lat != null) {
            latLan = LatLng(hobbyEvent.hobby.location.lat!!, hobbyEvent.hobby.location.lon!!)
        } else {
            locationReceived = false
        }
        if (locationReceived) {
            val markerPos = latLan
            map.addMarker(MarkerOptions()
                .position(markerPos)
                .icon(bitmapDescriptorFromVector(this, R.drawable.ic_location_24dp)))
            map.moveCamera(CameraUpdateFactory.newLatLng(markerPos))
        }
    }

    companion object {
        const val ERROR = "error"
    }

    internal inner class GetHobbyEvent : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void?): String {
            return try {
                URL(getString(R.string.API_URL) + "hobbyevents/" + id + "/?include=hobby_detail&include=organizer_detail&include=location_detail").readText()
            } catch (e: IOException) {
                return ERROR
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            when (result) {
                ERROR -> {
                    val builder = AlertDialog.Builder(this@HobbyDetailActivity)
                    builder.setMessage(getString(R.string.error_try_again_later))
                    builder.show()
                }

                else -> {
                    val hobbyObject = JSONObject(result)
                    hobbyEvent = HobbyEvent(hobbyObject)
                    setHobbyDetailView(hobbyEvent)
                }
            }
        }
    }
}


