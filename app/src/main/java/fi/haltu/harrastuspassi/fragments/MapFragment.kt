package fi.haltu.harrastuspassi.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterManager
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.activities.HobbyCategoriesActivity
import fi.haltu.harrastuspassi.activities.HobbyDetailActivity
import fi.haltu.harrastuspassi.activities.SettingsActivity
import fi.haltu.harrastuspassi.adapters.HobbyInfoWindowAdapter
import fi.haltu.harrastuspassi.adapters.MarkerClusterRenderer
import fi.haltu.harrastuspassi.models.Filters
import fi.haltu.harrastuspassi.models.HobbyEvent
import fi.haltu.harrastuspassi.models.Settings
import fi.haltu.harrastuspassi.utils.createQueryUrl
import fi.haltu.harrastuspassi.utils.loadFilters
import fi.haltu.harrastuspassi.utils.loadSettings
import fi.haltu.harrastuspassi.utils.saveSettings
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.net.URL

class MapFragment : Fragment() {
    companion object {
        const val CENTER_LAT = 64.9600 //Center point of Finland
        const val CENTER_LON = 27.5900
    }
    private var locationManager: LocationManager? = null
    private lateinit var gMap: GoogleMap
    private lateinit var filters: Filters
    private lateinit var settings: Settings
    private var hobbyEventArrayList = ArrayList<HobbyEvent>()
    private lateinit var mapView: MapView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val view: View = inflater.inflate(R.layout.fragment_map, container, false)

        settings = loadSettings(this.activity!!)
        filters = loadFilters(this.activity!!)

        mapView = view.findViewById(R.id.map_fragment) as MapView
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        try {
            MapsInitializer.initialize(activity!!.applicationContext)
        } catch(e: Exception) {
            e.printStackTrace()
        }

        GetHobbyEvents().execute()

        mapView.getMapAsync { googleMap ->
            gMap = googleMap

            zoomToLocation(filters, settings)
            setUpClusterManager(gMap)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }


    private fun zoomToLocation(filters: Filters, settings: Settings) {

        if(settings.useCurrentLocation) {
            try {
                // Request location updates
                locationManager = this.activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
                if(!gMap.isMyLocationEnabled) {
                    gMap.isMyLocationEnabled = true

                    val myLocation = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (myLocation != null) {
                        val latLng = LatLng(myLocation.latitude, myLocation.longitude)
                        val cameraPoint = CameraUpdateFactory.newLatLngZoom(latLng, 10f)
                        gMap.moveCamera(cameraPoint)
                    }
                }
            } catch(ex: SecurityException) {
                ActivityCompat.requestPermissions(this.activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    SettingsActivity.LOCATION_PERMISSION
                )
            }
        } else {
            gMap.isMyLocationEnabled = false

            if(filters.longitude != 0.0 && filters.latitude != 0.0) {
                val userLocation = LatLng(filters.latitude, filters.longitude)
                val cameraPoint = CameraUpdateFactory.newLatLngZoom(userLocation, 10f)
                gMap.moveCamera(cameraPoint)
            } else {
                val defaultPoint = LatLng(CENTER_LAT, CENTER_LON)
                val cameraPoint = CameraUpdateFactory.newLatLngZoom(defaultPoint, 5f)
                gMap.moveCamera(cameraPoint)
            }
        }
    }

    private fun setUpClusterManager(googleMap: GoogleMap) {
        googleMap.clear()
        val clusterManager = ClusterManager<HobbyEvent>(this.context, googleMap)
        //adds items to cluster

        val markerClusterRenderer = MarkerClusterRenderer(this.context!!, googleMap, clusterManager)
        clusterManager.renderer =  markerClusterRenderer
        googleMap.setInfoWindowAdapter(clusterManager.markerManager)

        clusterManager.markerCollection.setOnInfoWindowAdapter(HobbyInfoWindowAdapter(this.context!!))

        for(event in hobbyEventArrayList) {
            clusterManager.addItem(event)
        }

        googleMap.setOnCameraIdleListener(clusterManager)
        googleMap.setOnInfoWindowClickListener {
            val hobbyEvent: HobbyEvent? = it.tag as HobbyEvent?
            val intent = Intent(this.context, HobbyDetailActivity::class.java)
            intent.putExtra("EXTRA_HOBBY", hobbyEvent)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)

            true
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden) {
            filters = loadFilters(this.activity!!)
            settings = loadSettings(this.activity!!)
            GetHobbyEvents().execute()
            zoomToLocation(filters, settings)
        }
        //if hidden = false, it's almost same than onResume
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                        permissions: Array<String>, grantResults: IntArray) {
    when (requestCode) {
        SettingsActivity.LOCATION_PERMISSION -> {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                try {
                    // Request location updates
                    locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, locationListener)
                    if(!gMap.isMyLocationEnabled) {
                        gMap.isMyLocationEnabled = true

                        val myLocation = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        if (myLocation != null) {
                            val latLng = LatLng(myLocation.latitude, myLocation.longitude)
                            val cameraPoint = CameraUpdateFactory.newLatLngZoom(latLng, 10f)
                            gMap.moveCamera(cameraPoint)
                        }
                    }
                } catch(ex: SecurityException) {
                    gMap.isMyLocationEnabled = false
                }
            } else {
                gMap.isMyLocationEnabled = false
            }
            settings.useCurrentLocation = gMap.isMyLocationEnabled
            saveSettings(settings, this.activity!!)
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

    internal inner class GetHobbyEvents : AsyncTask<Void, Void, String>() {

    override fun doInBackground(vararg params: Void?): String {
        return try {
            URL(getString(R.string.API_URL) + createQueryUrl(filters)).readText()
        } catch (e: IOException) {
            return HobbyCategoriesActivity.ERROR
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        when (result) {
            HobbyCategoriesActivity.ERROR -> {
            }
            else -> {
                try {
                    val mJsonArray = JSONArray(result)
                    hobbyEventArrayList.clear()
                    for (i in 0 until mJsonArray.length()) {
                        val sObject = mJsonArray.get(i).toString()
                        val hobbyObject = JSONObject(sObject)
                        val hobbyEvent = HobbyEvent(hobbyObject)
                        hobbyEventArrayList.add(hobbyEvent)
                    }
                    val hobbyEventSet: Set<HobbyEvent> = hobbyEventArrayList.toSet()
                    hobbyEventArrayList.clear()

                    for(hobbyEvent in hobbyEventSet) {
                        hobbyEventArrayList.add(hobbyEvent)
                    }
                    setUpClusterManager(gMap)
                } catch(e: JSONException) {

                }
                Log.d("listUpdate", "Updated Map")
            }
        }
    }
}
}