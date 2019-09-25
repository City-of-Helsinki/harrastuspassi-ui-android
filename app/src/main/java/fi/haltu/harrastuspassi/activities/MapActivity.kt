package fi.haltu.harrastuspassi.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.activities.SettingsActivity.Companion.LOCATION_PERMISSION
import fi.haltu.harrastuspassi.models.Filters
import fi.haltu.harrastuspassi.models.HobbyEvent
import fi.haltu.harrastuspassi.models.Settings
import fi.haltu.harrastuspassi.utils.loadFilters
import fi.haltu.harrastuspassi.utils.loadSettings
import com.google.android.gms.maps.CameraUpdate
import fi.haltu.harrastuspassi.adapters.HobbyInfoWindowAdapter


class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    companion object {
        const val CENTER_LAT = 64.9600 //Center point of Finland
        const val CENTER_LON = 27.5900
    }
    private var locationManager: LocationManager? = null
    private lateinit var gMap: GoogleMap
    private lateinit var filters: Filters
    private lateinit var settings: Settings
    private var hobbyEventArrayList = ArrayList<HobbyEvent>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        setContentView(R.layout.activity_map)

        if (intent.hasExtra("EXTRA_HOBBY_BUNDLE")) {
            val bundle = intent.getBundleExtra("EXTRA_HOBBY_BUNDLE")
            hobbyEventArrayList = bundle.getSerializable("EXTRA_HOBBY_EVENT_LIST") as ArrayList<HobbyEvent>
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.hobby_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show()

    }

    override fun onResume() {
        super.onResume()
        settings = loadSettings(this)
        filters = loadFilters(this)


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_map, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        finish()
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_filter -> {
                val intent = Intent(this, FilterViewActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
                this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
                return true
            }

            R.id.list -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
                this.overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down)

                return true
            }

            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
                this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        this.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    try {
                        // Request location updates
                        locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
                        gMap.isMyLocationEnabled = true
                    } catch(ex: SecurityException) {
                        gMap.isMyLocationEnabled = false
                    }
                } else {
                    gMap.isMyLocationEnabled = false
                }
                return
            }

            else -> {
                gMap.isMyLocationEnabled = false
            }
        }
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val currentLocation = LatLng(location.latitude, location.longitude)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLocation, 10f)
            gMap.animateCamera(cameraUpdate)
            locationManager!!.removeUpdates(this)

        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap

        if(filters.longitude != 0.0 && filters.latitude != 0.0) {
            val userLocation = LatLng(filters.latitude, filters.longitude)
            val cameraPoint = CameraUpdateFactory.newLatLngZoom(userLocation, 10f)
            gMap.moveCamera(cameraPoint)
        } else {
            val defaultPoint = LatLng(CENTER_LAT, CENTER_LON)
            val cameraPoint = CameraUpdateFactory.newLatLngZoom(defaultPoint, 5f)
            gMap.moveCamera(cameraPoint)
        }

        if(settings.useCurrentLocation) {
            try {
                // Request location updates
                locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
                gMap.isMyLocationEnabled = true
            } catch(ex: SecurityException) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION)
            }

        }

        addMarkers(gMap, hobbyEventArrayList)
    }



    private fun addMarkers(googleMap: GoogleMap, eventList: ArrayList<HobbyEvent>) {
        googleMap.clear()
        for(event in eventList) {

            val markerOptions = MarkerOptions()
            markerOptions.position(LatLng(event.hobby.location.lat!!, event.hobby.location.lon!!))
                .title(event.hobby.name)


            val customViewInfo = HobbyInfoWindowAdapter(this)
            googleMap.setInfoWindowAdapter(customViewInfo)
            val marker = googleMap.addMarker(markerOptions)
            marker.tag = event
        }
    }



}
