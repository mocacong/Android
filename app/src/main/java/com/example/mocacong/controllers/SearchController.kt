package com.example.mocacong.controllers

import android.util.Log
import com.example.mocacong.data.objects.KakaoRetrofitClient
import com.example.mocacong.data.response.LocalSearchResponse
import com.example.mocacong.network.KakaoLocationBasedSearchAPI

class SearchController {

    suspend fun searchByXY(mapx: String, mapy: String) : LocalSearchResponse?{
        val api = KakaoRetrofitClient.create(KakaoLocationBasedSearchAPI::class.java)
        val response = api.getLocalSearchResponse(x = mapx, y = mapy)
        if(response.isSuccessful){
            return response.body()
        }else{
            Log.d("KAKAO","response 실패 ${response.errorBody()?.string()}")
            return null
        }
    }



}