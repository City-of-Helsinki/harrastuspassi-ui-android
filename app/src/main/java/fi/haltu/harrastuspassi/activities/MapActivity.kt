package fi.haltu.harrastuspassi.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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
import fi.haltu.harrastuspassi.models.Filters
import fi.haltu.harrastuspassi.models.HobbyEvent
import fi.haltu.harrastuspassi.utils.loadFilters

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    companion object {
        const val CENTER_LAT = 64.9600 //Center point of Finland
        const val CENTER_LON = 27.5900
    }

    private lateinit var gMap: GoogleMap
    private lateinit var filters: Filters
    private var hobbyEventArrayList = ArrayList<HobbyEvent>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        setContentView(R.layout.activity_map)
        filters = loadFilters(this)
        if (intent.hasExtra("EXTRA_HOBBY_BUNDLE")) {
            val bundle = intent.getBundleExtra("EXTRA_HOBBY_BUNDLE")
            hobbyEventArrayList = bundle.getSerializable("EXTRA_HOBBY_EVENT_LIST") as ArrayList<HobbyEvent>
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.hobby_map) as SupportMapFragment
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

        addMarkers(gMap, hobbyEventArrayList)
    }

    private fun addMarkers(googleMap: GoogleMap, eventList: ArrayList<HobbyEvent>) {
        googleMap.clear()
        for(event in eventList) {
            val location = LatLng(event.hobby.location.lat!!, event.hobby.location.lon!!)
            googleMap.addMarker(MarkerOptions().position(location)
                .title(event.hobby.name))
        }
    }

}
