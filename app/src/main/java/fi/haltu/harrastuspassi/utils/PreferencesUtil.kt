package fi.haltu.harrastuspassi.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.support.v4.app.FragmentActivity
import android.util.Log
import com.google.gson.Gson
import fi.haltu.harrastuspassi.models.Filters
import java.lang.IllegalStateException

fun saveFilters(filters: Filters, activity: Activity) {
    val preferences = activity.getPreferences( Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor = preferences.edit()
    val gson = Gson()
    val filtersJson = gson.toJson(filters)
    editor.putString("filters", filtersJson)
    editor.apply()
}

fun loadFilters(activity: Activity):Filters{
    var result = Filters()
    try {
        val preferences = activity.getPreferences( Context.MODE_PRIVATE)
        var gson = Gson()
        var json = preferences.getString("filters", "")
        result = gson.fromJson(json, Filters::class.java)
        Log.d("checkFilter2", result.categories.toString())
    } catch (e: IllegalStateException) {
        Log.d("checkFilter2", "ERROR")
    }

    return result
}


//TODO Fix this
fun loadFilters(activity: FragmentActivity):Filters{
    var result = Filters()
    try {
        val preferences = activity.getPreferences( Context.MODE_PRIVATE)
        var gson = Gson()
        var json = preferences.getString("filters", "")
        result = gson.fromJson(json, Filters::class.java)
        Log.d("checkFilter2", result.categories.toString())
    } catch (e: IllegalStateException) {
        Log.d("checkFilter2", "ERROR")
    }
    return result
}