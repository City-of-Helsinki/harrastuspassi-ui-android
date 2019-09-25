package fi.haltu.harrastuspassi.utils

import fi.haltu.harrastuspassi.models.Filters

fun createQueryUrl(filters: Filters): String {
    var query = "hobbyevents/?include=hobby_detail"
    val categoryArrayList = filters.categories.toArray()
    val weekDayArrayList = filters.dayOfWeeks.toArray()
    if(categoryArrayList.isNotEmpty()) {
        query += "&"
        for (i in 0 until categoryArrayList.size) {
            val categoryId = categoryArrayList[i]
            query += if (i == categoryArrayList.indexOfLast{ true }) {
                "category=$categoryId"
            } else {
                "category=$categoryId&"
            }
        }
    }
    if(weekDayArrayList.isNotEmpty()) {
        query += "&"
        for(i in 0 until weekDayArrayList.size) {
            val weekId = weekDayArrayList[i]
            query += if(i == weekDayArrayList.indexOfLast { true }) {
                "start_weekday=$weekId"
            } else {
                "start_weekday=$weekId&"
            }
        }
    }
    query += "&start_time_from=${minutesToTime(filters.startTimeFrom)}"
    query += "&start_time_to=${minutesToTime(filters.startTimeTo)}"

    /* if(filters.latitude != 0.0 && filters.longitude != 0.0) {
         query += "&latitude=${filters.latitude}"
         query += "&longitude=${filters.longitude}"

     }*/

    return query
}