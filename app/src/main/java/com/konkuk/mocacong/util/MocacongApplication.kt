package com.konkuk.mocacong.util

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.kakao.sdk.common.KakaoSdk

class MocacongApplication : Application() {
    val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "auth")
    override fun onCreate() {
        super.onCreate()
        // 다른 초기화 코드들

        // Kakao SDK 초기화
        KakaoSdk.init(this, "eb2cc1ff484fdd2ba0934a003fd80b3b")
        TokenManager.setDataStore(dataStore)
    }
    
}
