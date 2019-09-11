package fi.haltu.harrastuspassi.models

import com.google.android.gms.maps.model.LatLng
import fi.haltu.harrastuspassi.utils.minutesToTime
import java.io.Serializable

class Filters : Serializable {
    var categories: HashSet<Int> = HashSet()
    var dayOfWeeks: HashSet<Int> = HashSet()
    // Insert time as minutes and use minutesToTime() -method to convert
    var startTimeFrom: Int = 0 // 0:00
    var startTimeTo: Int = 24 * 60 - 1 // 23:59
    //Location
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    override fun toString(): String {
        return "categories: $categories\ndayOfWeeks:$dayOfWeeks\n" +
               "startTimeFrom:${minutesToTime(startTimeFrom)}\n" +
               "startTimeTo:${minutesToTime(startTimeTo)}" +
                "Location(lat/long): $latitude, $longitude"
    }
}