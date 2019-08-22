package fi.haltu.harrastuspassi.models

import java.io.Serializable

class Filters : Serializable {
    var categories: Set<Int> = setOf()
    var dayOfWeeks: Set<String> = setOf()
    //add startTime and endTime filter

}