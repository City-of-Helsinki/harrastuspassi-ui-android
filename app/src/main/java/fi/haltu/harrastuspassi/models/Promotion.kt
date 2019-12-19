package fi.haltu.harrastuspassi.models

import org.json.JSONObject
import java.io.Serializable

class Promotion(json: JSONObject? = null) : Serializable {
    var id: Int = 0
    var title: String = ""
    var description: String = ""
    var imageUrl: String? = null
    var startDate: String = ""
    var endDate: String = ""
    var isUsed: Boolean = false
    var organizer: Int = 5
    var municipality: String? = null
    var availableCount: Int = 0
    var usedCount: Int = 0

    init {
        if (json != null) {
            id = json.getInt("id")
            title = json.getString("name")
            description = json.getString("description")
            imageUrl = json.getString("cover_image")
            startDate = json.getString("start_date")
            endDate = json.getString("end_date")
            organizer = json.getInt("organizer")
            availableCount = json.getInt("available_count")
            usedCount = json.getInt("used_count")
            municipality = json.getString("municipality")
            if (municipality == "null") {
                municipality = "Haltu"
            }
        }
    }
}
