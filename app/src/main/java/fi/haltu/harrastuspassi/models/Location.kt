package fi.haltu.harrastuspassi.models

import fi.haltu.harrastuspassi.utils.getOptionalJSONObject
import org.json.JSONObject
import java.io.Serializable

class Location(json: JSONObject? = null) : Serializable {
    var id: Int = 0
    var name: String? = ""
    var address: String? = ""
    var zipCode: String? = ""
    var city: String? = ""
    var lat: Double? = 0.0
    var lon: Double? = 0.0

    init {
        if (json != null) {
            id = json.getInt("id")
            name = json.getString("name")
            address = json.getString("address")
            zipCode = json.getString("zip_code")
            city = json.getString("city")

            val coordinates = getOptionalJSONObject(json, "coordinates")

            if (coordinates != null) {
                val coordinatesString = coordinates.getString("coordinates")
                val coordinatesList =
                    coordinatesString.removeSurrounding("[", "]").split(",").map { it.toDouble() }
                lon = coordinatesList[0]
                lat = coordinatesList[1]
            }
        }
    }

    override fun toString(): String {
        return "{id:$id:, name: $name, address: $address, zipCode: $zipCode, city: $city}"
    }

}
