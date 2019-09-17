package fi.haltu.harrastuspassi.utils

import android.app.Activity
import fi.haltu.harrastuspassi.R


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
    //TODO there should be translation later
    return weekDays[id]
}

fun minutesToTime(minutes:Int): String {
    val hour = minutes/60
    val minutes = minutes%60
    return "${hour.toString().padStart(2,'0')}:${minutes.toString().padStart(2,'0')}"
}
