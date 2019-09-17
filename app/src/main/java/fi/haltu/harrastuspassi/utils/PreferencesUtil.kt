package fi.haltu.harrastuspassi.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.support.v4.app.FragmentActivity
import com.google.gson.Gson
import fi.haltu.harrastuspassi.models.Filters
import fi.haltu.harrastuspassi.models.Settings
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
    return try {
        val preferences = activity.getSharedPreferences("FILTER_PREFERENCE", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = preferences.getString("filters", "")
        gson.fromJson(json, Filters::class.java)
    } catch (e: IllegalStateException) {
        Filters()
    }
}

fun saveSettings(locations: Settings, activity: Activity) {
    val preferences = activity.getSharedPreferences("LOCATION_PREFERENCE", Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor = preferences.edit()
    val gson = Gson()
    val filtersJson = gson.toJson(locations)
    editor.putString("locations", filtersJson)
    editor.apply()
}

fun loadSettings(activity: Activity):Settings{
    return try {
        val preferences = activity.getSharedPreferences("LOCATION_PREFERENCE", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = preferences.getString("locations", "")
        gson.fromJson(json, Settings::class.java)
    } catch (e: IllegalStateException) {
        Settings()
    }
}