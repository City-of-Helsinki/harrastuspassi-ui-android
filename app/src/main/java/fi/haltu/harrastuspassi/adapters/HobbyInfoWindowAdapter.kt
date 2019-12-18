package fi.haltu.harrastuspassi.adapters

import android.app.Activity
import android.content.Context
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.squareup.picasso.Picasso
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.models.Hobby
import kotlinx.android.synthetic.main.adapter_custom_info_window.view.*

class HobbyInfoWindowAdapter(val context: Context) : GoogleMap.InfoWindowAdapter {

    override fun getInfoContents(p0: Marker?): View? {
        return null
    }

    override fun getInfoWindow(p0: Marker?): View? {
        val mInfoView =
            (context as Activity).layoutInflater.inflate(R.layout.adapter_custom_info_window, null)
        val hobby: Hobby? = p0?.tag as Hobby? ?: return mInfoView

        Picasso.with(context)
            .load(hobby?.imageUrl)
            .placeholder(R.drawable.image_placeholder_icon)
            .error(R.drawable.image_placeholder_icon)
            .into(mInfoView.info_image)
        mInfoView.info_title.text = hobby?.name
        //mInfoView.info_date.text = startDate

        return mInfoView
    }
}