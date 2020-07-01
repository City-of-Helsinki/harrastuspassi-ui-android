package fi.haltu.harrastuspassi.models

import com.google.gson.Gson
import java.io.Serializable

class Settings : Serializable {
    var selectedIndex = 0
    var useCurrentLocation = false
    var locationList = ArrayList<Location>()
    // Whether user is using the application fist time after installation
    var isFirstTime = true
    companion object {
        const val MAX_SIZE = 5
    }

    fun add(element: Location): Boolean {
        if (locationList.size >= MAX_SIZE) {
            locationList.removeAt(locationList.size - 1)
        }
        selectedIndex = 0
        locationList.add(0, element)
        return true
    }

    fun moveChosenLocationToFirst() {
        if (locationList.isNotEmpty()) {
            val location = locationList[selectedIndex]
            locationList.removeAt(selectedIndex)
            selectedIndex = 0
            add(location)
        }
    }

    fun clone(): Settings {
        val stringFilters = Gson().toJson(this, Settings::class.java)
        return Gson().fromJson<Settings>(stringFilters, Settings::class.java)
    }

    override fun toString(): String {
        return "selectedIndex: $selectedIndex\nuserCurrentLocation: $useCurrentLocation\n locations: $locationList"
    }
}