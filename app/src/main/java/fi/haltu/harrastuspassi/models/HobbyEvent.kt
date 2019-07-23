package fi.haltu.harrastuspassi.models

import java.io.Serializable

class HobbyEvent(_title: String, _place: String, _dateTime: String, _imageUrl: String) : Serializable {
    var title: String? = null
    var place: String? = null
    var dateTime: String? = null
    var imageUrl: String? = null
    var description: String? = null
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

        this.description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit." +
                " Vestibulum id arcu eget sem facilisis condimentum in in tellus. " +
                "In nec eros consequat, tristique orci tempus, ornare nisl. " +
                "Sed id varius lacus. Integer luctus urna massa, eget interdum quam " +
                "tincidunt vulputate. Fusce massa ante, semper sit amet augue et, imperdiet " +
                "luctus lectus. Etiam posuere auctor ullamcorper. Etiam neque nisi, pellentesque " +
                "porta arcu eu, pellentesque iaculis tellus. Fusce vulputate lacinia nisl, at vestibulum " +
                "augue dignissim eget. Duis blandit enim vel sem eleifend, vulputate semper tellus mattis."
    }

}