package fi.haltu.harrastuspassi.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
import fi.haltu.harrastuspassi.models.Filters
import fi.haltu.harrastuspassi.models.Hobby
import fi.haltu.harrastuspassi.models.HobbyEvent
import fi.haltu.harrastuspassi.models.Settings
import fi.haltu.harrastuspassi.utils.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import android.location.Location as AndroidLocation

class MapFragment : Fragment(), OnMapReadyCallback {

    companion object {
        const val CENTER_LAT = 64.9600 //Center point of Finland
        const val CENTER_LON = 27.5900
        const val LOCATION_PERMISSION = 1
        const val PAGE_SIZE = 500
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
    private lateinit var filterIcon: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_map, container, false)
        // APP BAR
        (activity as AppCompatActivity).supportActionBar!!.hide()
        view.findViewById<ImageView>(R.id.list_icon).setOnClickListener {
            val mainActivity = this.context as MainActivity
            mainActivity.switchBetweenMapAndListFragment()
        }
        view.findViewById<TextView>(R.id.list_text).setOnClickListener {
            val mainActivity = this.context as MainActivity
            mainActivity.switchBetweenMapAndListFragment()
        }
        view.findViewById<ImageView>(R.id.map_filter_icon).setOnClickListener {
            startFilterActivity()
        }
        view.findViewById<TextView>(R.id.map_filter_text).setOnClickListener {
            startFilterActivity()
        }

        filterIcon = view.findViewById(R.id.map_filter_icon)

        settings = loadSettings(this.activity!!)
        loadFiltersAndUpdateIcon()

        //GOOGLE MAP
        mapView = view.findViewById(R.id.map_fragment) as MapView
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        try {
            MapsInitializer.initialize(activity!!.applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mapView.getMapAsync(this)

        return view
    }

    private fun updateFilterIcon() {
        filterIcon.setImageResource(if (filters.hasActiveSecondaryFilters())
            R.drawable.ic_round_tune_active_24dp else R.drawable.ic_round_tune_24dp)
    }

    private fun loadFiltersAndUpdateIcon() {
        filters = loadFilters(this.activity!!)
        updateFilterIcon()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        GetHobbyEvents().execute()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        if (!isInit) {
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
        if (!hidden) {
            updateMap()
        }
    }

    private fun startFilterActivity() {
        val intent = Intent(this.context, FilterViewActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
        this.activity?.overridePendingTransition(
            R.anim.slide_in_left,
            R.anim.slide_out_left
    )
}
    private fun updateMap() {
        gMap.clear()
        loadFiltersAndUpdateIcon()
        settings = loadSettings(this.activity!!)
        GetHobbyEvents().execute()
    }

    private fun zoomToLocation(filters: Filters, settings: Settings) {
        if (settings.useCurrentLocation) {
            try {
                // Request location updates
                locationManager =
                    this.activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                locationManager?.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    0L,
                    0f,
                    locationListener
                )
                locationManager?.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0L,
                    0f,
                    locationListener
                )
                if (!gMap.isMyLocationEnabled) {
                    gMap.isMyLocationEnabled = true

                    val myLocation =
                        locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (myLocation != null) {
                        val latLng = LatLng(myLocation.latitude, myLocation.longitude)
                        val cameraPoint = CameraUpdateFactory.newLatLngZoom(latLng, 10f)
                        gMap.moveCamera(cameraPoint)
                    }

                    if (::userMarker.isInitialized) {
                        userMarker.remove()
                    }
                }
            } catch (ex: SecurityException) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION
                )
            }
        } else {
            gMap.isMyLocationEnabled = false

            if (filters.longitude != 0.0 && filters.latitude != 0.0) {
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
            .icon(
                bitmapDescriptorFromVector(
                    this.context!!,
                    R.drawable.ic_person_marker_24px
                )
            )
        )
    }

    private fun setUpClusterManager(googleMap: GoogleMap) {
        val clusterManager = ClusterManager<HobbyEvent>(this.context, googleMap)
        //adds items to cluster

        val markerClusterRenderer = MarkerClusterRenderer(this.context!!, googleMap, clusterManager, activity!!)
        clusterManager.renderer = markerClusterRenderer
        //googleMap.setInfoWindowAdapter(clusterManager.markerManager)

        //clusterManager.markerCollection.setOnInfoWindowAdapter(HobbyInfoWindowAdapter(this.context!!))

        for (event in hobbyEventArrayList) {
            clusterManager.addItem(event)
        }

        googleMap.setOnCameraIdleListener(clusterManager)
        googleMap.setOnMarkerClickListener(clusterManager)

        clusterManager.setOnClusterClickListener { cluster ->

            var hobbyEvents = ArrayList<HobbyEvent>()

            for (location in cluster.items) {
                hobbyEvents.add(location)
            }
            //SHOW DIALOG
            createHobbyEventListDialog(hobbyEvents, tittle = "")

            true
        }

        clusterManager.setOnClusterItemClickListener {hobbyEvent ->

            var hobbyEvents = ArrayList<HobbyEvent>()

            for (event in hobbyEventArrayList) {
                if (event.hobby.location.id == hobbyEvent!!.hobby!!.location.id) {
                    hobbyEvents.add(event)
                }
            }
            //TITTLE
            var tittle = hobbyEvent!!.hobby!!.location.name

            //SHOW DIALOG
            createHobbyEventListDialog(hobbyEvents, tittle!!)

            true
        }
    }

    fun closeIcon(dialog:Dialog) {
        //CLOSE_ICON
        val closeIcon = dialog.findViewById<ImageView>(R.id.close_icon)
        closeIcon.setOnClickListener {
            dialog.dismiss()
        }

        dialog.window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.show()
    }

    fun createHobbyEventListDialog(hobbyList: ArrayList<HobbyEvent>, tittle: String) {
        val dialog = Dialog(this.context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_hobby_list)

        val titleText = dialog.findViewById<TextView>(R.id.title)
        titleText.text = tittle

        val recyclerView = dialog.findViewById<RecyclerView>(R.id.hobby_list)

        val hobbyEventListAdapter =
            HobbyEventListAdapter(hobbyList) { hobbyEvent: HobbyEvent, hobbyImage: ImageView ->
                hobbyItemClicked(
                    hobbyEvent,
                    hobbyImage
                )
            }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = hobbyEventListAdapter
        }
        closeIcon(dialog)
    }

    private fun hobbyItemClicked(hobbyEvent: HobbyEvent, hobbyImage: ImageView) {
        val intent = Intent(context, HobbyDetailActivity::class.java)

        intent.putExtra("EXTRA_HOBBY", hobbyEvent)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)

        val sharedView: View = hobbyImage
        val transition = getString(R.string.item_detail)
        val transitionActivity =
            ActivityOptions.makeSceneTransitionAnimation(this.activity, sharedView, transition)
        startActivity(intent, transitionActivity.toBundle())
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    try {
                        // Request location updates
                        locationManager?.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            0L,
                            0f,
                            locationListener
                        )
                        locationManager?.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            0L,
                            0f,
                            locationListener
                        )

                        if (!gMap.isMyLocationEnabled) {
                            gMap.isMyLocationEnabled = true

                            val myLocation =
                                locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            if (myLocation != null) {
                                val latLng = LatLng(myLocation.latitude, myLocation.longitude)
                                val cameraPoint = CameraUpdateFactory.newLatLngZoom(latLng, 10f)
                                gMap.moveCamera(cameraPoint)
                            }
                        }
                    } catch (ex: SecurityException) {
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
                URL(getString(R.string.API_URL) + createHobbyEventQueryUrl(filters, PAGE_SIZE)).readText()
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
                        val results = JSONObject(result)
                        val mJsonArray = results.getJSONArray("results")
                        hobbyEventArrayList.clear()
                        for (i in 0 until mJsonArray.length()) {
                            val sObject = mJsonArray.get(i).toString()
                            val hobbyObject = JSONObject(sObject)
                            val hobbyEvent = HobbyEvent(hobbyObject)
                            hobbyEventArrayList.add(hobbyEvent)
                        }
                        val hobbyEventSet: Set<HobbyEvent> = hobbyEventArrayList.toSet()
                        hobbyEventArrayList.clear()

                        for (hobbyEvent in hobbyEventSet) {
                            hobbyEventArrayList.add(hobbyEvent)
                        }
                        hobbyArrayList = uniqueByLocation(hobbyEventArrayList)
                        setUpClusterManager(gMap)
                        zoomToLocation(filters, settings)
                    } catch (e: JSONException) {

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
        for (hobby in hobbySet) {
            array.add(hobby)
        }

        return array
    }
}