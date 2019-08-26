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
    val preferences = activity.getSharedPreferences("FILTER_PREFERENCE", Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor = preferences.edit()
    val gson = Gson()
    val filtersJson = gson.toJson(filters)
    editor.putString("filters", filtersJson)
    editor.apply()
}

fun loadFilters(activity: FragmentActivity):Filters{
    var result = Filters()
    try {
        val preferences = activity.getSharedPreferences("FILTER_PREFERENCE", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = preferences.getString("filters", "")
        result = gson.fromJson(json, Filters::class.java)
    } catch (e: IllegalStateException) {
        //TODO error handling here!
    }
    return result
}