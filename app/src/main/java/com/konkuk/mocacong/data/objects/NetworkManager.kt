package com.konkuk.mocacong.data.objects

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.fragment.app.FragmentManager

class NetworkManager {

    companion object{
        fun Context.isNetworkConnected(context: Context): Boolean {
            val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
            val currentNetwork = connectivityManager.activeNetwork ?: return false
            val actNetwork = connectivityManager.getNetworkCapabilities(currentNetwork) ?: return false
            return (actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))

        }

        fun Context.showCheckDialog(supportFragmentManager : FragmentManager){
//            MessageDialog("인터넷 연결 상태를 확인해주세요!").show(supportFragmentManager, "MessageDialog")
        }
    }
}