package fi.haltu.harrastuspassi.adapters

import android.app.Activity
import android.content.Context
import android.graphics.Color
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import fi.haltu.harrastuspassi.R
import fi.haltu.harrastuspassi.models.HobbyEvent
import fi.haltu.harrastuspassi.utils.bitmapDescriptorFromVector
import fi.haltu.harrastuspassi.utils.loadFavorites

class MarkerClusterRenderer(
    val context: Context,
    val map: GoogleMap,
    clusterManager: ClusterManager<HobbyEvent>,
    val activity: Activity
) : DefaultClusterRenderer<HobbyEvent>(context, map, clusterManager) {
    companion object {
        const val MIN_CLUSTER_SIZE = 2
    }

    override fun getColor(clusterSize: Int): Int {
        return Color.parseColor("#77329B")
    }

    override fun onBeforeClusterItemRendered(item: HobbyEvent, markerOptions: MarkerOptions) { // 5
        //markerOptions.title(item.title)

        val favorites = loadFavorites(activity)
        if (favorites.contains(item.id)) {
            markerOptions.icon(
                bitmapDescriptorFromVector(
                    context,
                    R.drawable.ic_location_on_red_light_24dp
                )
            )
        } else {
            markerOptions.icon(
                bitmapDescriptorFromVector(
                    context,
                    R.drawable.ic_location_on_purple_light_24dp
                )
            )
        }
    }

    override fun onClusterItemRendered(clusterItem: HobbyEvent?, marker: Marker?) {
        marker?.tag = clusterItem

        super.onClusterItemRendered(clusterItem, marker)
    }

    override fun shouldRenderAsCluster(cluster: Cluster<HobbyEvent>?): Boolean {
        return cluster!!.size >= MIN_CLUSTER_SIZE
    }
}