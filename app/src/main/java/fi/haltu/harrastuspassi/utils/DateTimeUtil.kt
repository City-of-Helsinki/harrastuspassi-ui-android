package fi.haltu.harrastuspassi.utils

import android.app.Activity
import android.content.Context
import fi.haltu.harrastuspassi.R
import java.text.SimpleDateFormat
import java.util.*


fun idToWeekDay(id: Int, activity: Activity): String? {
    val monday = activity.getString(R.string.monday)
    val tuesday = activity.getString(R.string.tuesday)
    val wednesday = activity.getString(R.string.wednesday)
    val thursday = activity.getString(R.string.thursday)
    val friday = activity.getString(R.string.friday)
    val saturday = activity.getString(R.string.saturday)
    val sunday = activity.getString(R.string.sunday)
    val weekDays: Map<Int, String> = mapOf(1 to monday,
        2 to tuesday,
        3 to wednesday,
        4 to thursday,
        5 to friday,
        6 to saturday,
        7 to sunday)
    return weekDays[id]
}


fun idToWeekDay(id: Int, context: Context): String? {
    val monday = context.getString(R.string.monday)
    val tuesday = context.getString(R.string.tuesday)
    val wednesday = context.getString(R.string.wednesday)
    val thursday = context.getString(R.string.thursday)
    val friday = context.getString(R.string.friday)
    val saturday = context.getString(R.string.saturday)
    val sunday = context.getString(R.string.sunday)
    val weekDays: Map<Int, String> = mapOf(1 to monday,
        2 to tuesday,
        3 to wednesday,
        4 to thursday,
        5 to friday,
        6 to saturday,
        7 to sunday)
    return weekDays[id]
}

fun minutesToTime(minutes:Int): String {
    val hour = minutes/60
    val minutes = minutes%60
    return "${hour.toString().padStart(2,'0')}:${minutes.toString().padStart(2,'0')}"
}

fun convertToDateRange(startDate: String, endDate: String): String {
    val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val formatterDDMM = SimpleDateFormat("dd.MM", Locale.US)
    val formatterDDMMYYYY = SimpleDateFormat("dd.MM.yyyy", Locale.US)
    var startDateForm = ""
    var endDateForm = ""
    try {
        startDateForm = formatterDDMM.format(parser.parse(startDate))
        endDateForm = formatterDDMMYYYY.format(parser.parse(endDate))
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return "$startDateForm. - $endDateForm"
}

fun convertToTimeRange(startTime:String, endTime: String): String {
    val timeParser = SimpleDateFormat("HH:mm:ss", Locale.US)
    val timeFormatter = SimpleDateFormat("HH.mm", Locale.US)
    var startTimeForm = ""
    var endTimeForm = ""
    try {
        startTimeForm = timeFormatter.format(timeParser.parse(startTime))
        endTimeForm =  timeFormatter.format(timeParser.parse(endTime))
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return "$startTimeForm - $endTimeForm"
}