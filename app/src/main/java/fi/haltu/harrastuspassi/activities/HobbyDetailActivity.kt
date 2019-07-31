package fi.haltu.harrastuspassi.activities

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hobby_detail)

        val hobby = intent.extras!!.getSerializable("EXTRA_HOBBY") as HobbyEvent
        //title = (hobby.title)
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
        locationNameTextView.text = hobby.place.name
        locationAddress.text = hobby.place.address
        locationZipCode.text = hobby.place.zipCode

        latLan = LatLng(hobby.place.lat!!, hobby.place.lon!!)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        getHobbyEvent().execute()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val tampere = latLan
        map.addMarker(MarkerOptions().position(tampere).title("Marker in Tampere"))
        map.moveCamera(CameraUpdateFactory.newLatLng(tampere))
    }

    companion object {
        const val ERROR = "error"
        const val NO_INTERNET = "no_internet"
    }

    internal inner class getHobbyEvent : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void?): String {
            return try {
                URL("http://10.0.1.229:8000/mobile-api/hobbies/" + id).readText()
            } catch (e: IOException) {
                return "API ERROR"
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            when (result) {
                ERROR -> {
                    Log.d("ERROR", result)
                }
                NO_INTERNET -> {
                    Log.d("ERROR", result)
                }
                else -> {
                        val hobbyObject = JSONObject(result)

                        //val coverImage = hobbyObject.getString("cover_image")
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

                        //this@HobbyDetailActivity.coverImageView.setText(coverImage)
                        this@HobbyDetailActivity.titleTextView.setText(title)
                        this@HobbyDetailActivity.organizerTextView.setText(organizer)
                        this@HobbyDetailActivity.dayOfWeekTextView.setText(dayOfWeek)
                        this@HobbyDetailActivity.startTimeTextView.setText(startTime)
                        this@HobbyDetailActivity.dateTextView.setText(startDate)
                        this@HobbyDetailActivity.descriptionTextView.setText(description)


                        Log.d("Location", "toimiiko? " + hobbyObject.toString())

                }
            }
        }
    }



}
