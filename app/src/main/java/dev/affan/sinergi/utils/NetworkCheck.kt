package dev.affan.sinergi.utils

import android.content.Context
import android.net.ConnectivityManager

object NetworkCheck {
    fun isConnect(context: Context): Boolean {
        try {
            val activeNetworkInfo =
                (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
                    ?: return false
            return activeNetworkInfo.isConnected /*|| activeNetworkInfo.isConnectedOrConnecting*/
        } catch (e: Exception) {
            return false
        }
    }
}
