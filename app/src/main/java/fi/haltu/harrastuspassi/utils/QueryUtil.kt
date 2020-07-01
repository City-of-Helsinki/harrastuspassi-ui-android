package fi.haltu.harrastuspassi.utils

import android.util.Log
import fi.haltu.harrastuspassi.models.Filters
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashSet

fun createHobbyEventQueryUrl(filters: Filters): String {
    var query = "hobbyevents/?include=location_detail&include=organizer_detail&include=hobby_detail"
    val categoryArrayList = filters.categories.toArray()
    val weekDayArrayList = filters.dayOfWeeks.toArray()
    if (categoryArrayList.isNotEmpty()) {
        query += "&"
        for (i in categoryArrayList.indices) {
            val categoryId = categoryArrayList[i]
            query += if (i == categoryArrayList.indexOfLast { true }) {
                "category=$categoryId"
            } else {
                "category=$categoryId&"
            }
        }
    } else if(filters.searchText != "") {
        query += "&search=${filters.searchText}"
    }
    if (weekDayArrayList.isNotEmpty()) {
        query += "&"
        for (i in weekDayArrayList.indices) {
            val weekId = weekDayArrayList[i]
            query += if (i == weekDayArrayList.indexOfLast { true }) {
                "start_weekday=$weekId"
            } else {
                "start_weekday=$weekId&"
            }
        }
    }

    if (filters.showFree) {
        query += "&price_type=free"
    }

    query += "&start_time_from=${minutesToTime(filters.startTimeFrom)}"
    query += "&start_time_to=${minutesToTime(filters.startTimeTo)}"

    query += "&ordering=nearest"
    query += "&near_latitude=${filters.latitude}"
    query += "&near_longitude=${filters.longitude}"

    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("fi", "FI"))
    query += "&start_date_from=${simpleDateFormat.format(Date())}"

    Log.d("query", query)

    return query
}

fun createPromotionQueryUrl(filters: Filters, searchText: String? = null): String {
    var query = "promotions/?exclude_past_events=true&usable_only=true&include=location_detail&ordering=nearest"
    if (filters.latitude != 0.0 && filters.longitude != 0.0) {
        query += "&ordering=nearest"
        query += "&near_latitude=${filters.latitude}"
        query += "&near_longitude=${filters.longitude}"
    }
    if(!searchText.isNullOrEmpty()) {
        query += "&search=$searchText"
    }
    Log.d("query_promotion", query)
    return query
}

fun createFavoriteQueryUrl(favorites: HashSet<Int>): String {
    val query = "hobbyevents/?include=hobby_detail&include=location_detail&include=organizer_detail"
    if (favorites.isNotEmpty()) {
        for (i in 0 until favorites.size) {
            //query += "&"
        }
    }
    return query
}