package fi.haltu.harrastuspassi.adapters

import com.google.maps.android.clustering.view.DefaultClusterRenderer
import fi.haltu.harrastuspassi.models.HobbyEvent
import android.content.Context
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.android.gms.maps.GoogleMap


class MarkerClusterRenderer(
    val context: Context,
    val map: GoogleMap,
    clusterManager: ClusterManager<HobbyEvent>
) : DefaultClusterRenderer<HobbyEvent>(context, map, clusterManager) {

    override fun onBeforeClusterItemRendered(item: HobbyEvent, markerOptions: MarkerOptions) { // 5
        markerOptions.title(item.title)
        val customViewInfo = HobbyInfoWindowAdapter(context)
        map.setInfoWindowAdapter(customViewInfo)
        val marker = map.addMarker(markerOptions)
        marker.tag = item
    }
}