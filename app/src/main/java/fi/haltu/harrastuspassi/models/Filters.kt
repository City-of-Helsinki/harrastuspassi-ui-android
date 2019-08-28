package fi.haltu.harrastuspassi.models

import java.io.Serializable

class Filters : Serializable {
    var categories: HashSet<Int> = HashSet()
    var dayOfWeeks: HashSet<Int> = HashSet()
    //add startTime and endTime filter
    fun isEmpty(): Boolean {
        return categories.isEmpty() && dayOfWeeks.isEmpty()
    }
    override fun toString(): String {
        return "categories: $categories\ndayOfWeeks:$dayOfWeeks"
    }
}