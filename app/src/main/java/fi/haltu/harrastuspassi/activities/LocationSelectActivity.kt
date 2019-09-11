package fi.haltu.harrastuspassi.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.models.Filters

class LocationSelectActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var filters: Filters = Filters()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_select)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        filters = intent.extras!!.getSerializable("EXTRA_FILTERS") as Filters
        findViewById<Button>(R.id.use_location_button).setOnClickListener{
            intent.putExtra("EXTRA_FILTERS", filters)
            setResult(1, intent)
            finish()
        }
    }

    override fun onBackPressed() {
        intent.putExtra("EXTRA_FILTERS", filters)
        setResult(1, intent)
        finish()
        super.onBackPressed()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        if(filters.longitude != 0.0 && filters.latitude != 0.0) {
            val userLocation = LatLng(filters.latitude, filters.longitude)
            val cameraPoint = CameraUpdateFactory.newLatLngZoom(userLocation, 10f)
            mMap.moveCamera(cameraPoint)
            mMap.addMarker(MarkerOptions()
                .position(userLocation)
                .title("Sijaintisi")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
            )
        } else {
            //Tampere
            val defaultPoint = CameraUpdateFactory.newLatLngZoom(LatLng(61.9241, 25.7482), 5f)
            mMap.moveCamera( defaultPoint)
        }

        mMap.setOnMapClickListener {
            filters.latitude = it.latitude
            filters.longitude = it.longitude
            mMap.clear()
            mMap.addMarker(MarkerOptions()
                .position(it)
                .title("Sijaintisi")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
            )
            Toast.makeText(this, filters.toString(), Toast.LENGTH_SHORT).show()
        }
    }

}
