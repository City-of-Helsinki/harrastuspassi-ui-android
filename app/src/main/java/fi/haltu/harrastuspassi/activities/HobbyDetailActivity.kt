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
import fi.haltu.harrastuspassi.models.HobbyEvent
import fi.haltu.harrastuspassi.models.Location
import fi.haltu.harrastuspassi.utils.getLatLon
import fi.haltu.harrastuspassi.utils.getLocation
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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

    private lateinit var map: GoogleMap
    private lateinit var latLan: LatLng

    private var hobby: HobbyEvent = HobbyEvent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hobby_detail)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        hobby = intent.extras!!.getSerializable("EXTRA_HOBBY") as HobbyEvent
        id = (hobby.id)

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
            .load(hobby.imageUrl)
            .placeholder(R.drawable.image_placeholder_icon)
            .error(R.drawable.image_placeholder_icon)
            .into(coverImageView)

        titleTextView.text = hobby.title


        latLan = LatLng(hobby.place.lat!!, hobby.place.lon!!)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        getHobbyEvent().execute()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        locationNameTextView.text = hobby.place.name
        locationAddress.text = hobby.place.address
        locationZipCode.text = hobby.place.zipCode
        map = googleMap

        val tampere = latLan
        map.addMarker(MarkerOptions().position(tampere).title("Marker in Tampere"))
        map.moveCamera(CameraUpdateFactory.newLatLng(tampere))
    }

    companion object {
        const val ERROR = "error"
    }

    internal inner class getHobbyEvent : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void?): String {
            return try {
                URL("http://10.0.1.229:8000/mobile-api/hobbies/" + id).readText()
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
                    val title = hobbyObject.getString("name")
                    val organizer = hobbyObject.getString("organizer")
                    val dayOfWeek = hobbyObject.getString("start_day_of_week")
                    val startTime = hobbyObject.getString("start_time")
                    val startDate = hobbyObject.getString("start_date")

                    val description = hobbyObject.getString("description")

                    val hobbyEvent = HobbyEvent()

                    val locationObject = getLocation(hobbyObject, "locationNameTextView")
                    val hobbyLocation = Location()
                    if (locationObject != null) {
                        val locationName = locationObject.getString("name")
                        val locationAddress = locationObject.getString("address")
                        val locationZipCode = locationObject.getString("zip_code")
                        val locationCity = locationObject.getString("city")
                        val locationLat = getLatLon(locationObject, "lat")
                        val locationLon = getLatLon(locationObject, "lon")

                        hobbyLocation.apply {
                            this.name = locationName
                            this.address = locationAddress
                            this.zipCode = locationZipCode
                            this.city = locationCity
                            this.lat = locationLat
                            this.lon = locationLon
                        }
                    }


                    val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.US)
                    val date = formatter.format(parser.parse(startDate))

                    val timeParser = SimpleDateFormat("HH:mm:ss", Locale.US)
                    val timeFormatter = SimpleDateFormat("HH.mm", Locale.US)
                    val time = timeFormatter.format(timeParser.parse(startTime))


                    this@HobbyDetailActivity.titleTextView.setText(title)
                        this@HobbyDetailActivity.organizerTextView.setText(organizer)
                        this@HobbyDetailActivity.dayOfWeekTextView.setText(dayOfWeek)
                        this@HobbyDetailActivity.startTimeTextView.setText(time)
                        this@HobbyDetailActivity.dateTextView.setText(date)
                        this@HobbyDetailActivity.descriptionTextView.setText(description)


                        //Log.d("Location", "toimiiko? " + hobbyObject.toString())
                        //Log.d("Date", "toimiiko formatting? " + formattedDate.toString())

                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item!!.itemId) {
            R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }

    }

}

