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
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.models.Filters
import fi.haltu.harrastuspassi.models.Location
import fi.haltu.harrastuspassi.models.LocationQueueList
import java.util.*

class SettingsActivity : AppCompatActivity(){
    private var locationManager: LocationManager? = null
    //private lateinit var geocoder: Geocoder
    private lateinit var currentLocationSwitch: Switch
    private lateinit var locationMapButton: Button
    private var filters: Filters = Filters()
    private var locationList = LocationQueueList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportActionBar!!.title = "Settings"
        locationMapButton = findViewById(R.id.location_map_button)
        locationMapButton.setOnClickListener {
            val intent = Intent(this, LocationSelectActivity::class.java)
            intent.putExtra("EXTRA_FILTERS", filters)
            startActivityForResult(intent, 1)
        }
        //geocoder = Geocoder(this, Locale.getDefault())

        currentLocationSwitch = findViewById(R.id.current_location_switch)
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
    }
    //define the listener
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: AndroidLocation) {
            Log.d("LocationTest", "Long: ${location.longitude } \nLat: ${location.latitude}")

            filters.latitude = location.latitude
            filters.longitude = location.longitude


        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 1) {
            filters = data!!.extras.getSerializable("EXTRA_FILTERS") as Filters
            //val addresses = geocoder.getFromLocation(filters.latitude, filters.longitude, 1)
            val location = Location()
            /*location.address = addresses[0].getAddressLine(0)
            location.city = addresses[0].locality
            location.zipCode = addresses[0].postalCode*/
            Toast.makeText(this, location.toString(), Toast.LENGTH_LONG).show()
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
            locationMapButton.visibility = View.INVISIBLE
        } else {
            locationMapButton.visibility = View.VISIBLE
        }
    }
}
