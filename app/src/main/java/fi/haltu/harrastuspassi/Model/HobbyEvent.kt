package fi.haltu.harrastuspassi.Model

import java.io.Serializable

class HobbyEvent : Serializable {
    var title: String? = null
    var place: String? = null
    var dateTime: String? = null
    var image: Int = 0
}