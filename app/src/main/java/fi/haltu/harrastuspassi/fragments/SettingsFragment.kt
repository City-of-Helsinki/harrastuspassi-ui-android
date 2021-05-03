package fi.haltu.harrastuspassi.fragments

import android.Manifest
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.activities.LocationSelectActivity
import fi.haltu.harrastuspassi.adapters.LocationListAdapter
import fi.haltu.harrastuspassi.models.Filters
import fi.haltu.harrastuspassi.models.Location
import fi.haltu.harrastuspassi.models.Settings
import fi.haltu.harrastuspassi.utils.loadFilters
import fi.haltu.harrastuspassi.utils.loadSettings
import fi.haltu.harrastuspassi.utils.saveFilters
import fi.haltu.harrastuspassi.utils.saveSettings
import java.util.*
import android.location.Location as AndroidLocation

class SettingsFragment : Fragment(), LocationListener {
    private var locationManager: LocationManager? = null
    private lateinit var geocoder: Geocoder
    private lateinit var currentLocationSwitch: Switch
    private lateinit var locationMapButton: Button
    private lateinit var locationListView: RecyclerView
    private lateinit var latestLocationTitle: TextView
    private lateinit var acceptFromSettingsText: TextView
    private var filters: Filters = Filters()
    private var settings: Settings = Settings()
    private lateinit var filtersOriginal: Filters
    private lateinit var settingsOriginal: Settings
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_settings, container, false)
        super.onCreate(savedInstanceState)
        geocoder = Geocoder(context, Locale.getDefault())
        filters = loadFilters(this.activity!!)
        settings = loadSettings(this.activity!!)
        filtersOriginal = filters.clone()
        settingsOriginal = settings.clone()

        // CHOOSE LOCATION BUTTON
        locationMapButton = view.findViewById(R.id.location_map_button)
        locationMapButton.setOnClickListener {

            val intent = Intent(this.activity, LocationSelectActivity::class.java)
            intent.putExtra("EXTRA_FILTERS", filters)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivityForResult(intent, 1)
        }

        // USE USER LOCATION SWITCH
        currentLocationSwitch = view.findViewById(R.id.user_location_switch)
        try {
            // Request location updates
            locationManager = context?.getSystemService(LOCATION_SERVICE) as LocationManager?
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
            Log.d("LocationPermissin", "$ex")
        }
        currentLocationSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                locationManager = context?.getSystemService(LOCATION_SERVICE) as LocationManager?
                disableChooseLocation(true)
                try {

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
                    this.requestPermissions(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        LOCATION_PERMISSION
                    )
                }
            } else {
                disableChooseLocation(false)
            }
        }
        //ACCEPT FROM SETTINGS TEXT
        acceptFromSettingsText = view.findViewById(R.id.accept_from_settings)
        acceptFromSettingsText.setOnClickListener {
            val intent = Intent()
            intent.action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", activity!!.packageName, null)
            intent.data = uri
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        acceptFromSettingsText.visibility = View.INVISIBLE

        // LOCATION LIST
        var locationListAdapter = LocationListAdapter(settings)
        latestLocationTitle = view.findViewById(R.id.recent_location_text)
        locationListView = view.findViewById(R.id.location_list)
        locationListView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = locationListAdapter
        }
        //PUTS USE USER LOCATION ON
        if (settings.useCurrentLocation) {
            currentLocationSwitch.performClick()
        }

        return view
    }

    override fun onLocationChanged(location: AndroidLocation) {
        filters.latitude = location.latitude
        filters.longitude = location.longitude
        filters.isModified = true
        locationManager!!.removeUpdates(this)
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            settings.moveChosenLocationToFirst()
            if (!currentLocationSwitch.isChecked && settings.locationList.isNotEmpty()) {
                val chosenLocation = settings.locationList[settings.selectedIndex]
                filters.latitude = chosenLocation.lat!!
                filters.longitude = chosenLocation.lon!!
                filters.isModified = true
            }
            saveFilters(filters, this.activity!!)
            saveSettings(settings, this.activity!!)
        } else {
            filters = loadFilters(this.activity!!)
            var savedSettings = loadSettings(this.activity!!)
            settings.useCurrentLocation = savedSettings.useCurrentLocation
            currentLocationSwitch.isChecked = settings.useCurrentLocation
            currentLocationSwitch.isChecked = settings.useCurrentLocation
            filtersOriginal = filters.clone()
            this.locationListView.adapter!!.notifyDataSetChanged()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && data != null) {
            filters = data.extras?.getSerializable("EXTRA_FILTERS") as Filters
            val addresses = geocoder.getFromLocation(filters.latitude, filters.longitude, 1)
            val location = Location()
            try {
                location.address = addresses[0].getAddressLine(0)
                location.city = addresses[0].locality
                location.zipCode = addresses[0].postalCode
            } catch (e: IndexOutOfBoundsException) {
                location.address = ""
                location.city = ""
                location.zipCode = ""
            }

            location.lat = filters.latitude
            location.lon = filters.longitude
            settings.add(location)
            locationListView.adapter!!.notifyDataSetChanged()
        }
    }

    companion object {
        const val LOCATION_PERMISSION = 1
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //user accepted permission
                    try {
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
                        currentLocationSwitch.isChecked = false
                    }
                } else if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    //user rejected the permission
                    currentLocationSwitch.isChecked = false
                    //user also checked "never ask again"
                    if (permissions.isNotEmpty() && !shouldShowRequestPermissionRationale(
                            permissions[0]
                        )
                    ) {
                        acceptFromSettingsText.visibility = View.VISIBLE
                    } else {
                        acceptFromSettingsText.visibility = View.INVISIBLE
                    }
                }

                return
            }

            else -> {
                currentLocationSwitch.isChecked = false
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun disableChooseLocation(isHide: Boolean) {
        if (isHide) {
            locationMapButton.isEnabled = false
            settings.useCurrentLocation = true
            locationMapButton.alpha = (0.5).toFloat()
            locationListView.alpha = (0.5).toFloat()
            latestLocationTitle.alpha = (0.5).toFloat()

        } else {
            locationMapButton.isEnabled = true
            settings.useCurrentLocation = false
            locationMapButton.alpha = (1.0).toFloat()
            locationListView.alpha = (1.0).toFloat()
            latestLocationTitle.alpha = (1.0).toFloat()
        }

        locationListView.adapter!!.notifyDataSetChanged()
    }

}
