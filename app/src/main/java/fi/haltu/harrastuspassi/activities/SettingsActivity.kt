package fi.haltu.harrastuspassi.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location as AndroidLocation
import android.location.LocationListener
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button
import android.widget.Switch
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.adapters.LocationListAdapter
import fi.haltu.harrastuspassi.models.Filters
import fi.haltu.harrastuspassi.models.Location
import fi.haltu.harrastuspassi.models.Settings
import fi.haltu.harrastuspassi.utils.loadFilters
import fi.haltu.harrastuspassi.utils.loadSettings
import fi.haltu.harrastuspassi.utils.saveFilters
import fi.haltu.harrastuspassi.utils.saveSettings
import java.util.*

class SettingsActivity : AppCompatActivity(){
    private var locationManager: LocationManager? = null
    private lateinit var geocoder: Geocoder
    private lateinit var currentLocationSwitch: Switch
    private lateinit var locationMapButton: Button
    private lateinit var locationListView: RecyclerView
    private lateinit var saveButton: Button

    private var filters: Filters = Filters()
    private var settings = Settings()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportActionBar!!.title = "Settings"

        geocoder = Geocoder(this, Locale.getDefault())
        filters = loadFilters(this)
        settings = loadSettings(this)
        // CHOOSE LOCATION BUTTON
        locationMapButton = findViewById(R.id.location_map_button)
        locationMapButton.setOnClickListener {
            val intent = Intent(this, LocationSelectActivity::class.java)
            intent.putExtra("EXTRA_FILTERS", filters)
            startActivityForResult(intent, 1)
        }

        // USE USER LOCATION SWITCH
        currentLocationSwitch = findViewById(R.id.current_location_switch)
        try {
            // Request location updates
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
        } catch(ex: SecurityException) {

        }
        currentLocationSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
                disableChoosableLocation(true)
                try {
                    // Request location updates
                    locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
                } catch(ex: SecurityException) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION)
                }
            } else {
                disableChoosableLocation(false)
            }
        }

        // LOCATION LIST
        var locationListAdapter = LocationListAdapter(settings)
        locationListView = findViewById(R.id.location_list)
        locationListView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = locationListAdapter
        }
        //PUTS USE USER LOCATION ON
        if(settings.useCurrentLocation) {
            currentLocationSwitch.performClick()
        }

        //SAVE BUTTON
        saveButton = findViewById(R.id.save_button)
        saveButton.setOnClickListener {
            settings.moveChosenLocationToFirst()
            if(!currentLocationSwitch.isChecked && settings.locationList.isNotEmpty()) {
                val chosenLocation = settings.locationList[settings.selectedIndex]
                filters.latitude = chosenLocation.lat!!
                filters.longitude = chosenLocation.lon!!
            }
            saveFilters(filters, this)
            saveSettings(settings, this)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
            finish()
        }
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: AndroidLocation) {
            filters.latitude = location.latitude
            filters.longitude = location.longitude
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 1 && data != null) {
            filters = data.extras.getSerializable("EXTRA_FILTERS") as Filters
            val addresses = geocoder.getFromLocation(filters.latitude, filters.longitude, 1)
            val location = Location()
            location.address = addresses[0].getAddressLine(0)
            location.city = addresses[0].locality
            location.zipCode = addresses[0].postalCode
            location.lat = filters.latitude
            location.lon = filters.longitude
            settings.add(location)
            locationListView.adapter!!.notifyDataSetChanged()
        }
    }

    companion object {
        const val LOCATION_PERMISSION = 1
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    try {
                        // Request location updates
                        locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
                    } catch(ex: SecurityException) {
                        currentLocationSwitch.isChecked = false

                    }
                } else {
                    currentLocationSwitch.isChecked = false
                }
                return
            }

            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun disableChoosableLocation(isHide: Boolean) {
        if(isHide) {
            locationMapButton.isEnabled = false
            settings.useCurrentLocation = true
        } else {
            locationMapButton.isEnabled = true
            settings.useCurrentLocation = false
        }
        locationListView.adapter!!.notifyDataSetChanged()
    }

}
