package fi.haltu.harrastuspassi.adapters

import com.google.maps.android.clustering.view.DefaultClusterRenderer
import fi.haltu.harrastuspassi.models.HobbyEvent
import android.content.Context
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.clustering.Cluster


class MarkerClusterRenderer(
    val context: Context,
    val map: GoogleMap,
    clusterManager: ClusterManager<HobbyEvent>
) : DefaultClusterRenderer<HobbyEvent>(context, map, clusterManager) {
    companion object {
        const val MIN_CLUSTER_SIZE = 5
    }

    override fun onBeforeClusterItemRendered(item: HobbyEvent, markerOptions: MarkerOptions) { // 5
        markerOptions.title(item.title)
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
    }

    override fun onClusterItemRendered(clusterItem: HobbyEvent?, marker: Marker?) {
        marker?.tag = clusterItem

        super.onClusterItemRendered(clusterItem, marker)
    }

    override fun shouldRenderAsCluster(cluster: Cluster<HobbyEvent>?): Boolean {
        return cluster!!.size >= MIN_CLUSTER_SIZE
    }
}