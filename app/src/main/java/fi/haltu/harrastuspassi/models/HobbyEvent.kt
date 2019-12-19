package fi.haltu.harrastuspassi.models

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import fi.haltu.harrastuspassi.utils.getOptionalJSONObject
import org.json.JSONObject
import java.io.Serializable

class HobbyEvent(json: JSONObject? = null) : Serializable, ClusterItem {


    var id: Int = 0
    var startDate: String = ""
    var endDate: String = ""
    var startTime: String = ""
    var endTime: String = ""
    var startWeekday: Int = 0
    lateinit var hobby: Hobby

    init {
        if (json != null) {
            id = json.getInt("id")
            startDate = json.getString("start_date")
            endDate = json.getString("end_date")
            startTime = json.getString("start_time")
            endTime = json.getString("end_time")
            startWeekday = json.getInt("start_weekday")
            val hobbyObject = getOptionalJSONObject(json, "hobby")

            if (hobbyObject != null) {
                hobby = Hobby(hobbyObject)
            }
        }
    }

    override fun getSnippet(): String {
        return ""
    }

    override fun getTitle(): String {
        return this.hobby.name
    }

    override fun getPosition(): LatLng {
        return LatLng(hobby.location.lat!!, hobby.location.lon!!)
    }

    override fun equals(other: Any?): Boolean {
        val hobbyEvent = other as HobbyEvent
        return this.hobby.id == hobbyEvent.hobby.id
    }

    override fun hashCode(): Int {
        return this.hobby.id.hashCode()
    }
}