package fi.haltu.harrastuspassi.utils

import android.content.Context
import android.net.ConnectivityManager
import android.support.v4.app.FragmentActivity
import java.io.IOException

class InternetCheck {
    fun verifyAvailableNetwork(activity: FragmentActivity): Boolean {
        return try {
            val connectivityManager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            networkInfo != null && networkInfo.isConnected
        } catch (e: IOException) {
            return false
        }
    }
}