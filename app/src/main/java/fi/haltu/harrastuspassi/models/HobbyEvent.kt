package fi.haltu.harrastuspassi.models

import java.io.Serializable

class HobbyEvent: Serializable {
    var id: Int = 0
    var title: String = "Ei otsikkoa"
        set(value) {
            field = if (value == "null") {
                "Ei otsikkoa"
            } else {
                value
            }
        }
    var place: Location = Location()
    var dateTime: String = "Ei ajankohtaa"
        set(value) {
            field = if (value == "null") {
                "Ei ajankohtaa"
            } else {
                value
            }
        }
    var imageUrl: String? = null
    var description: String = "Ei tietoja"
        set(value) {
            field = if (value == "null") {
                "Ei tietoja"
            } else {
                value
            }
        }

}