package com.konkuk.mocacong.util

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MocacongApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // 다른 초기화 코드들

        // Kakao SDK 초기화
        KakaoSdk.init(this, "eb2cc1ff484fdd2ba0934a003fd80b3b")
        TokenManager.setDataStore(dataStore)
    }
    
}
