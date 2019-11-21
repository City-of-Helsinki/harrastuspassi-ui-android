package fi.haltu.harrastuspassi.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location as AndroidLocation
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.activities.FilterViewActivity
import fi.haltu.harrastuspassi.activities.HobbyCategoriesActivity
import fi.haltu.harrastuspassi.activities.HobbyDetailActivity
import fi.haltu.harrastuspassi.activities.MainActivity
import fi.haltu.harrastuspassi.adapters.HobbyEventListAdapter
import fi.haltu.harrastuspassi.adapters.MarkerClusterRenderer
import fi.haltu.harrastuspassi.models.*
import fi.haltu.harrastuspassi.utils.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.net.URL

class MapFragment : Fragment(), OnMapReadyCallback {


    companion object {
        const val CENTER_LAT = 64.9600 //Center point of Finland
        const val CENTER_LON = 27.5900
        const val LOCATION_PERMISSION = 1
    }

    private var locationManager: LocationManager? = null
    private lateinit var gMap: GoogleMap
    private lateinit var filters: Filters
    private lateinit var settings: Settings
    private var hobbyEventArrayList = ArrayList<HobbyEvent>()
    private var hobbyArrayList = ArrayList<Hobby>()
    private lateinit var mapView: MapView
    private var isInit = true
    private lateinit var userMarker: Marker


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val view: View = inflater.inflate(R.layout.fragment_map, container, false)

        settings = loadSettings(this.activity!!)
        filters = loadFilters(this.activity!!)

        //GOOGLE MAP
        mapView = view.findViewById(R.id.map_fragment) as MapView
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        try {
            MapsInitializer.initialize(activity!!.applicationContext)
        } catch(e: Exception) {
            e.printStackTrace()
        }

        mapView.getMapAsync(this)
        GetHobbyEvents().execute()

        return view
    }
    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        zoomToLocation(filters, settings)
        setUpClusterManager(gMap)
    }
    
    override fun onResume() {
        super.onResume()
        mapView.onResume()
        if(!isInit) {
            updateMap()
        } else {
            isInit = false
        }
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

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden) {
            updateMap()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.map -> {
                val mainActivity = this.context as MainActivity
                mainActivity.switchBetweenMapAndListFragment()
                return true
            }

            R.id.action_filter -> {
                val intent = Intent(this.context, FilterViewActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
                this.activity?.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.map).icon =  ContextCompat.getDrawable(this.context!!, R.drawable.list_icon)

        super.onPrepareOptionsMenu(menu)
    }


    private fun updateMap() {
        filters = loadFilters(this.activity!!)
        settings = loadSettings(this.activity!!)
        if(!filters.isMapUpdated) {
            GetHobbyEvents().execute()
            filters.isMapUpdated = true
            saveFilters(filters, this.activity!!)
        }
        zoomToLocation(filters, settings)
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

                    if(::userMarker.isInitialized) {
                        userMarker.remove()
                    }
                }
            } catch(ex: SecurityException) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION
                )
            }
        } else {
            gMap.isMyLocationEnabled = false

            if(filters.longitude != 0.0 && filters.latitude != 0.0) {
                val userLocation = LatLng(filters.latitude, filters.longitude)
                val cameraPoint = CameraUpdateFactory.newLatLngZoom(userLocation, 10f)
                addUserLocationMarker(gMap, userLocation)
                gMap.moveCamera(cameraPoint)
            } else {
                val defaultPoint = LatLng(CENTER_LAT, CENTER_LON)
                addUserLocationMarker(gMap, defaultPoint)
                val cameraPoint = CameraUpdateFactory.newLatLngZoom(defaultPoint, 5f)
                gMap.moveCamera(cameraPoint)
            }
        }
    }

    private fun addUserLocationMarker(gMap: GoogleMap, latLng: LatLng) {

        userMarker = gMap.addMarker(
            MarkerOptions().position(latLng)
            .title(activity!!.getString(R.string.your_location))
            .icon(bitmapDescriptorFromVector(this.context!!, R.drawable.ic_accessibility_purple_24dp))
        )
    }

    private fun setUpClusterManager(googleMap: GoogleMap) {
        googleMap.clear()
        val clusterManager = ClusterManager<Hobby>(this.context, googleMap)
        //adds items to cluster

        val markerClusterRenderer = MarkerClusterRenderer(this.context!!, googleMap, clusterManager)
        clusterManager.renderer =  markerClusterRenderer
        //googleMap.setInfoWindowAdapter(clusterManager.markerManager)

        //clusterManager.markerCollection.setOnInfoWindowAdapter(HobbyInfoWindowAdapter(this.context!!))

        for(event in hobbyArrayList) {
            clusterManager.addItem(event)
        }

        googleMap.setOnCameraIdleListener(clusterManager)
        googleMap.setOnMarkerClickListener { marker ->
            if(marker.tag != null) {
                val hobby: Hobby? = marker.tag as Hobby?
                val dialog = Dialog(this.context!!)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(true)
                dialog.setContentView(R.layout.dialog_hobby_list)
                //TITLE
                val titleText = dialog.findViewById<TextView>(R.id.title)
                titleText.text = hobby?.location?.name
                //HOBBY_LIST
                val recyclerView = dialog.findViewById<RecyclerView>(R.id.hobby_list)
                var hobbyList = ArrayList<HobbyEvent>()
                for(event in hobbyEventArrayList) {
                    if(event.hobby.location.id == hobby!!.location.id) {
                        hobbyList.add(event)
                    }
                }
                val hobbyEventListAdapter = HobbyEventListAdapter(hobbyList) { hobbyEvent: HobbyEvent, hobbyImage: ImageView -> hobbyItemClicked(hobbyEvent, hobbyImage)}
                recyclerView.apply {
                    layoutManager = LinearLayoutManager(this.context)
                    adapter = hobbyEventListAdapter
                }

                //CLOSE_ICON
                val closeIcon = dialog.findViewById<ImageView>(R.id.close_icon)
                closeIcon.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
                dialog.show()
            } else {
                val markerPosition = LatLng(marker.position.latitude, marker.position.longitude)
                var zoomLevel = googleMap.cameraPosition.zoom
                marker.showInfoWindow()
                val cameraPoint = CameraUpdateFactory.newLatLngZoom(markerPosition, zoomLevel + 2f)
                gMap.animateCamera(cameraPoint)
            }

            true
        }
    }

    private fun hobbyItemClicked(hobbyEvent: HobbyEvent, hobbyImage: ImageView) {
        val intent = Intent(context, HobbyDetailActivity::class.java)

        intent.putExtra("EXTRA_HOBBY", hobbyEvent)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)

        val sharedView: View = hobbyImage
        val transition = getString(R.string.item_detail)
        val transitionActivity = ActivityOptions.makeSceneTransitionAnimation(this.activity, sharedView, transition)
        startActivity(intent, transitionActivity.toBundle())
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                        permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERMISSION -> {
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
    override fun onLocationChanged(location: AndroidLocation) {
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
                        hobbyArrayList = uniqueByLocation(hobbyEventArrayList)
                        setUpClusterManager(gMap)
                        zoomToLocation(filters, settings)
                    } catch(e: JSONException) {

                    }
                }
            }
        }
    }

    fun uniqueByLocation(hobbyArrayList: ArrayList<HobbyEvent>): ArrayList<Hobby> {
        val array = ArrayList<Hobby>()
        for (hobbyEvent in hobbyArrayList) {
            array.add(hobbyEvent.hobby)
        }
        val hobbySet: Set<Hobby> = array.toSet()

        array.clear()
        for(hobby in hobbySet) {
            array.add(hobby)
        }

        return array
    }
}