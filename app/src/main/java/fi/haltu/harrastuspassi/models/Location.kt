package fi.haltu.harrastuspassi.models

import fi.haltu.harrastuspassi.utils.getOptionalDouble
import org.json.JSONObject
import java.io.Serializable

class Location(json: JSONObject? = null) : Serializable {
    var name: String? = ""
    var address: String? = ""
    var zipCode: String? = ""
    var city: String? = ""
    var lat: Double? = 0.0
    var lon: Double? = 0.0

    init {
        if(json != null) {
            name = json.getString("name")
            address = json.getString("address")
            zipCode = json.getString("zip_code")
            city = json.getString("city")
            lat = getOptionalDouble(json, "lat")
            lon = getOptionalDouble(json, "lon")

            if(lat == null||lon == null) {
                lat = 0.0
                lon = 0.0
            }
        }
    }

    override fun toString(): String {
        return "name: $name\naddress: $address\nzipCode: $zipCode\ncity: $city"
    }
}