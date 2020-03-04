package fi.haltu.harrastuspassi.activities

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.models.Filters

class LocationSelectActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var closeButton: ImageView
    companion object {
        const val CENTER_LAT = 64.9600 //Center point of Finland
        const val CENTER_LON = 27.5900
    }

    private lateinit var gMap: GoogleMap
    private var filters: Filters = Filters()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_select)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        closeButton = findViewById(R.id.map_select_close_button)
        closeButton.setOnClickListener {
            this.onBackPressed()
        }
        filters = intent.extras!!.getSerializable("EXTRA_FILTERS") as Filters
        findViewById<Button>(R.id.use_location_button).setOnClickListener {
            intent.putExtra("EXTRA_FILTERS", filters)
            setResult(1, intent)
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap

        if (filters.longitude != 0.0 && filters.latitude != 0.0) {
            val userLocation = LatLng(filters.latitude, filters.longitude)
            val cameraPoint = CameraUpdateFactory.newLatLngZoom(userLocation, 10f)
            gMap.moveCamera(cameraPoint)
        } else {
            val defaultPoint = LatLng(CENTER_LAT, CENTER_LON)
            val cameraPoint = CameraUpdateFactory.newLatLngZoom(defaultPoint, 5f)
            gMap.moveCamera(cameraPoint)
        }

        gMap.setOnCameraIdleListener {
            setLatLong()
        }

        gMap.setOnCameraMoveListener {
            setLatLong()
        }
    }

    private fun setLatLong() {
        val cameraPosition = gMap.cameraPosition
        var currentCenter = cameraPosition.target
        filters.latitude = currentCenter.latitude
        filters.longitude = currentCenter.longitude
    }

}
