package fi.haltu.harrastuspassi.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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

fun saveFilters(filters: Filters, activity: FragmentActivity) {
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

fun saveFavorite(favorites:HashSet<Int>, activity: Activity) {

    val preferences = activity.getSharedPreferences("FAVORITE_PREFERENCE", Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor = preferences.edit()
    val gson = Gson()
    val favoritesJson = gson.toJson(favorites)
    editor.putString("favorites", favoritesJson)
    editor.apply()
}

fun loadFavorites(activity: FragmentActivity): HashSet<Int> {
    return try {
        val preferences = activity.getSharedPreferences("FAVORITE_PREFERENCE", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = preferences.getString("favorites", "")
        gson.fromJson(json, object : TypeToken<HashSet<Int>>() {}.type)
    } catch(e: IllegalStateException) {
        return HashSet()
    }
}

fun loadFavorites(activity: Activity): HashSet<Int> {
    return try {
        val preferences = activity.getSharedPreferences("FAVORITE_PREFERENCE", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = preferences.getString("favorites", "")
        gson.fromJson(json, object : TypeToken<HashSet<Int>>() {}.type)
    } catch(e: IllegalStateException) {
        return HashSet()
    }
}


fun saveUsedPromotions(usedPromotions:HashSet<Int>, activity: Activity) {

    val preferences = activity.getSharedPreferences("USED_PROMOTIONS_PREFERENCE", Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor = preferences.edit()
    val gson = Gson()
    val usedPromotionsJson = gson.toJson(usedPromotions)
    editor.putString("used_promotions", usedPromotionsJson)
    editor.apply()
}

fun loadUsedPromotions(activity: FragmentActivity): HashSet<Int> {
    return try {
        val preferences = activity.getSharedPreferences("USED_PROMOTIONS_PREFERENCE", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = preferences.getString("used_promotions", "")
        gson.fromJson(json, object : TypeToken<HashSet<Int>>() {}.type)
    } catch(e: IllegalStateException) {
        return HashSet()
    }
}
