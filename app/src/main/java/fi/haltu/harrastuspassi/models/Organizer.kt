package fi.haltu.harrastuspassi.models

import org.json.JSONObject
import java.io.Serializable

class Organizer(json: JSONObject) : Serializable {
    var name: String = ""

    init {
        name = json.getString("name")
    }
}