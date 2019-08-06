package fi.haltu.harrastuspassi.utils

import org.json.JSONObject

fun getLocation(json: JSONObject, key: String): JSONObject? {
    return if (json.isNull(key))
        null
    else
        json.getJSONObject(key)
}

fun getLatLon(json: JSONObject, key: String): Double? {
    // http://code.google.com/p/android/issues/detail?id=13830
    return if (json.isNull(key))
        null
    else
        json.getDouble(key)
}