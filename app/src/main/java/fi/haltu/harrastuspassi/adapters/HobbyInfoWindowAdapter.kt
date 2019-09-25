package fi.haltu.harrastuspassi.adapters

import android.app.Activity
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.squareup.picasso.Picasso
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.models.HobbyEvent
import kotlinx.android.synthetic.main.adapter_custom_info_window.view.*

class HobbyInfoWindowAdapter(val context: Context): GoogleMap.InfoWindowAdapter {

    override fun getInfoContents(p0: Marker?): View? {
        return null
    }

    override fun getInfoWindow(p0: Marker?): View? {
        var mInfoView = (context as Activity).layoutInflater.inflate(R.layout.adapter_custom_info_window, null)
        var mInfoWindow: HobbyEvent? = p0?.tag as HobbyEvent?


        Picasso.with(context)
            .load(mInfoWindow?.hobby?.imageUrl)
            .placeholder(R.drawable.image_placeholder_icon)
            .error(R.drawable.image_placeholder_icon)
            .into(mInfoView.info_image)
        mInfoView.info_title.text = mInfoWindow?.hobby?.name
        mInfoView.info_date.text = mInfoWindow?.startDate

        return mInfoView
    }
}