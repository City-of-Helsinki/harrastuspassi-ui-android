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
import android.util.Log
import android.widget.Button
import android.widget.Switch
import fi.haltu.harrastuspassi.R

class SettingsActivity : AppCompatActivity(){
    private var locationManager: LocationManager? = null
    private lateinit var currentLocationSwitch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportActionBar!!.title = resources.getString(R.string.settings)
        var locationMapButton = findViewById<Button>(R.id.location_map_button)
        locationMapButton.setOnClickListener {
            val intent = Intent(this, LocationSelectActivity::class.java)
            startActivity(intent)
        }
        currentLocationSwitch = findViewById(R.id.current_location_switch)
        currentLocationSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?

                try {
                    // Request location updates
                    locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
                } catch(ex: SecurityException) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION)
                }
            }
        }
    }
    //define the listener
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.d("LocationTest", "Long: ${location.longitude } \nLat: ${location.latitude}")
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
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
}
