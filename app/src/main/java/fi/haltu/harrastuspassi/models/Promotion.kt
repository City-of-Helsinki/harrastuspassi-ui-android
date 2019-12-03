package fi.haltu.harrastuspassi.models

import org.json.JSONObject
import java.io.Serializable

class Promotion(json: JSONObject? = null): Serializable {
    var id: Int = 0
    var title: String = ""
    var description: String = ""
    var imageUrl: String? = null
    var startDate: String = ""
    var endDate: String = ""
    var isUsed: Boolean = false

    init {
        if(json != null) {
            id = json.getInt("id")
            title = json.getString("name")
            description = json.getString("description")
            imageUrl = json.getString("cover_image")
            startDate = json.getString("start_date")
            endDate = json.getString("end_date")
        }
    }
}
