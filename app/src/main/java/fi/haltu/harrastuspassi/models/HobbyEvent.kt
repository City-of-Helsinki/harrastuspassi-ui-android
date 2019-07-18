package fi.haltu.harrastuspassi.models

import java.io.Serializable

class HobbyEvent(_title: String, _place: String, _dateTime: String, _imageUrl: String) : Serializable {
    var title: String? = null
    var place: String? = null
    var dateTime: String? = null
    var imageUrl: String? = null

    init {
        if (_title == "null") {
            this.title = "Ei otsikkoa"
        } else {
            this.title = _title
        }

        if (_place == "null") {
            this.place = "Ei paikkaa"
        } else {
            this.place = _place
        }

        if (_dateTime == "null") {
            this.dateTime = "Ei ajankohtaa"
        } else {
            this.dateTime = _dateTime
        }

        this.imageUrl = _imageUrl
    }

}