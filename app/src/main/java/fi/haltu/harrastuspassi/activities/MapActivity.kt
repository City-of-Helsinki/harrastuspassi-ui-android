package fi.haltu.harrastuspassi.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
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
import fi.haltu.harrastuspassi.adapters.HobbyInfoWindowAdapter
import com.google.maps.android.clustering.ClusterManager
import fi.haltu.harrastuspassi.activities.HobbyCategoriesActivity.Companion.ERROR
import fi.haltu.harrastuspassi.adapters.MarkerClusterRenderer
import fi.haltu.harrastuspassi.utils.*
import kotlinx.android.synthetic.main.activity_hobby_detail.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URL


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
    private lateinit var mapFragment: SupportMapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        setContentView(R.layout.activity_map)

        settings = loadSettings(this)
        filters = loadFilters(this)

        if (intent.hasExtra("EXTRA_HOBBY_BUNDLE")) {
            val bundle = intent.getBundleExtra("EXTRA_HOBBY_BUNDLE")
            hobbyEventArrayList = bundle.getSerializable("EXTRA_HOBBY_EVENT_LIST") as ArrayList<HobbyEvent>
        }

        mapFragment = supportFragmentManager.findFragmentById(R.id.hobby_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            filters = data!!.extras.getSerializable("EXTRA_FILTERS") as Filters
            if(filters.isModified) {
                GetHobbyEvents().execute()
                filters.isModified = false
                saveFilters(filters, this)
            }
        } else if(requestCode == 2) {
            settings = data!!.extras.getSerializable("EXTRA_SETTINGS") as Settings
            filters = data!!.extras.getSerializable("EXTRA_FILTERS") as Filters

            GetHobbyEvents().execute()
            zoomToLocation(filters, settings)
        }
        Log.d("listUpdate", "onActivityResult")
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_filter -> {
                val intent = Intent(this, FilterViewActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivityForResult(intent, 1)
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
                startActivityForResult(intent, 2)
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

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        setUpClusterManager(gMap)
        zoomToLocation(filters, settings)
    }

    private fun zoomToLocation(filters: Filters, settings: Settings) {
        if(settings.useCurrentLocation) {
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
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION)
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
        val clusterManager = ClusterManager<HobbyEvent>(this, googleMap) // 1
        //adds items to cluster

        val markerClusterRenderer = MarkerClusterRenderer(this, googleMap, clusterManager) // 2
        clusterManager.renderer =  markerClusterRenderer
        googleMap.setInfoWindowAdapter(clusterManager.markerManager)//3

        clusterManager.markerCollection.setOnInfoWindowAdapter(HobbyInfoWindowAdapter(this))//4


        for(event in hobbyEventArrayList) {
            clusterManager.addItem(event)
        }
        googleMap.setOnCameraIdleListener(clusterManager)
        googleMap.setOnInfoWindowClickListener {
            val hobbyEvent: HobbyEvent? = it.tag as HobbyEvent?
            val intent = Intent(this, HobbyDetailActivity::class.java)

            intent.putExtra("EXTRA_HOBBY", hobbyEvent)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            //val sharedView: View = hobbyImage
            //val transition = getString(R.string.item_detail)
            //val transitionActivity = ActivityOptions.makeSceneTransitionAnimation(this.activity, sharedView, transition)
            //startActivity(intent, transitionActivity.toBundle())
            startActivity(intent)

            true
        }
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
    internal inner class GetHobbyEvents : AsyncTask<Void, Void, String>() {

        override fun doInBackground(vararg params: Void?): String {
            return try {
                URL(getString(R.string.API_URL) + createQueryUrl(filters)).readText()
            } catch (e: IOException) {
                return ERROR
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            when (result) {
                ERROR -> {
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

                        setUpClusterManager(gMap)
                    } catch(e: JSONException) {

                    }
                    Log.d("listUpdate", "Updated")
                }
            }
        }
    }
}
