package fi.haltu.harrastuspassi.activities

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.models.Hobby
import fi.haltu.harrastuspassi.models.HobbyEvent
import fi.haltu.harrastuspassi.models.Location
import fi.haltu.harrastuspassi.utils.getOptionalDouble
import fi.haltu.harrastuspassi.utils.getOptionalJSONObject
import fi.haltu.harrastuspassi.utils.idToWeekDay
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class HobbyDetailActivity : AppCompatActivity(), OnMapReadyCallback{

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

    private lateinit var hobbyEvent: HobbyEvent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hobby_detail)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        hobbyEvent = intent.extras!!.getSerializable("EXTRA_HOBBY") as HobbyEvent
        id = (hobbyEvent.id)

        coverImageView = findViewById(R.id.hobby_image)
        titleTextView = findViewById(R.id.hobby_title)
        organizerTextView = findViewById(R.id.hobby_organizer)
        dateTextView = findViewById(R.id.date)
        dayOfWeekTextView = findViewById(R.id.date_time)
        startTimeTextView = findViewById(R.id.start_time)

        locationNameTextView = findViewById(R.id.location)
        descriptionTextView = findViewById(R.id.description_text)
        locationAddress = findViewById(R.id.location_address)
        locationZipCode = findViewById(R.id.location_zipcode)


        Picasso.with(this)
            .load(hobbyEvent.hobby.imageUrl)
            .placeholder(R.drawable.image_placeholder_icon)
            .error(R.drawable.image_placeholder_icon)
            .into(coverImageView)

        titleTextView.text = hobbyEvent.hobby.name


        if (hobbyEvent.hobby.location.lat != null) {
            Log.d("Latitide:", hobbyEvent.hobby.location.lat.toString())
            latLan = LatLng(hobbyEvent.hobby.location.lat!!, hobbyEvent.hobby.location.lon!!)
        } else {
            locationReceived = false
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        getHobbyEvent().execute()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        locationNameTextView.text = hobbyEvent.hobby.location.name
        locationAddress.text = hobbyEvent.hobby.location.address
        locationZipCode.text = hobbyEvent.hobby.location.zipCode
        map = googleMap

        if (locationReceived) {
            val markerPos = latLan
            map.addMarker(MarkerOptions().position(markerPos).title("Marker in Tampere"))
            map.moveCamera(CameraUpdateFactory.newLatLng(markerPos))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        return when (item!!.itemId) {
            R.id.home -> {
                finish()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    companion object {
        const val ERROR = "error"
    }

    internal inner class getHobbyEvent : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void?): String {
            return try {
                URL(getString(R.string.API_URL) + "hobbyevents/" + id + "/?include=hobby_detail").readText()
            } catch (e: IOException) {
                return ERROR
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            when (result) {
                ERROR -> {
                    Log.d("ERROR", result)
                    val builder = AlertDialog.Builder(this@HobbyDetailActivity)
                    builder.setMessage("Jokin meni vikaan, yritä myöhemmin uudelleen.")
                    builder.show()
                }

                else -> {
                    val hobbyObject = JSONObject(result)
                    hobbyEvent = HobbyEvent(hobbyObject)

                    val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.US)
                    var date = "Ei ilmoitettu"
                    try {
                        date = formatter.format(parser.parse(hobbyEvent.startDate))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    val timeParser = SimpleDateFormat("HH:mm:ss", Locale.US)
                    val timeFormatter = SimpleDateFormat("HH.mm", Locale.US)
                    var startTime = "Ei ilmoiteuttu"
                    var endTime = "Ei ilmoiteuttu"

                    try {
                        startTime = timeFormatter.format(timeParser.parse(hobbyEvent.startTime))
                        endTime =  timeFormatter.format(timeParser.parse(hobbyEvent.endTime))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    this@HobbyDetailActivity.titleTextView.text = hobbyEvent.hobby.name
                    if (hobbyEvent.hobby.organizer != null) {
                        this@HobbyDetailActivity.organizerTextView.text = hobbyEvent.hobby.organizer!!.name
                    } else {
                        this@HobbyDetailActivity.organizerTextView.text = "Ei ilmoitettu"
                    }

                    if (hobbyEvent.startWeekday != 0) {
                        this@HobbyDetailActivity.dayOfWeekTextView.text = idToWeekDay(hobbyEvent.startWeekday)
                    } else {
                        this@HobbyDetailActivity.dayOfWeekTextView.text = "?"
                    }

                    this@HobbyDetailActivity.startTimeTextView.text = "$startTime - $endTime"
                    this@HobbyDetailActivity.dateTextView.text = date
                    this@HobbyDetailActivity.descriptionTextView.text = hobbyEvent.hobby.description
                }
            }
        }
    }



}


