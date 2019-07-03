package fi.haltu.harrastuspassi.Model

import java.io.Serializable

class Hobby: Serializable {
    var title: String? = null
    var description: String? = null
    var place: String? = null
    var distance: Double = 0.0
    var duration: String? = null
    var image: Int = 0
    var organizer: String? = null

}