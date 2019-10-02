package fi.haltu.harrastuspassi.models

import fi.haltu.harrastuspassi.utils.getOptionalJSONObject
import org.json.JSONObject
import java.io.Serializable

class Hobby(json: JSONObject) : Serializable {
    var id: Int = 0
    var name: String = ""
    var imageUrl: String? = null
    var category: Int = 0
    var description: String = ""
    var organizer: Organizer? = null
    lateinit var location: Location

    init {
        id = json.getInt("id")
        name = json.getString("name")
        imageUrl = json.getString("cover_image")
        category = json.getInt("category")
        description = json.getString("description")
        val organizerObject = getOptionalJSONObject(json, "organizer")
        if(organizerObject != null) {
            organizer = Organizer(organizerObject)
        }
        val locationObject = getOptionalJSONObject(json, "location")
        if (locationObject != null) {
            location = Location(locationObject)
        }
    }
}