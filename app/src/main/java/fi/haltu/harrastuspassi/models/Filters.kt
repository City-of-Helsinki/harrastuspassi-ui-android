package fi.haltu.harrastuspassi.models

import java.io.Serializable

class Filters : Serializable {
    var categories: HashSet<Int> = HashSet()
    var dayOfWeeks: Set<String> = setOf()
    //add startTime and endTime filter

}