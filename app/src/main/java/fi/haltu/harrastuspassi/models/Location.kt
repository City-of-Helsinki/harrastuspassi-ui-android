package fi.haltu.harrastuspassi.models

import java.io.Serializable

class Location : Serializable {
    var name: String? = ""
    var address: String? = ""
    var zipCode: String? = ""
    var city: String? = ""
    var lat: Double? = 0.0
    var lon: Double? = 0.0
}