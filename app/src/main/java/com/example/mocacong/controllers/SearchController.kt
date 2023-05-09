package com.example.mocacong.controllers

import android.util.Log
import com.example.mocacong.data.objects.KakaoLocalClient
import com.example.mocacong.data.response.LocalSearchResponse
import com.example.mocacong.network.KakaoKeywordBasedSearchAPI
import com.example.mocacong.network.KakaoLocationBasedSearchAPI

class SearchController {

    val locApi : KakaoLocationBasedSearchAPI
    val keyApi : KakaoKeywordBasedSearchAPI
    init {
        locApi = KakaoLocalClient.create(KakaoLocationBasedSearchAPI::class.java)
        keyApi = KakaoLocalClient.create(KakaoKeywordBasedSearchAPI::class.java)
    }

    suspend fun searchByKeyword(query : String) : LocalSearchResponse?{
        val response = keyApi.getKeywordSearchResponse(query)
        if(response.isSuccessful){
            return response.body()
        }else{
            Log.d("KAKAO","response 실패 ${response.errorBody()?.string()}")
            return null
        }
    }

    suspend fun searchByXY(mapx: String, mapy: String) : LocalSearchResponse?{
        val api = KakaoLocalClient.create(KakaoLocationBasedSearchAPI::class.java)
        val response = api.getLocalSearchResponse(x = mapx, y = mapy)
        if(response.isSuccessful){
            return response.body()
        }else{
            Log.d("KAKAO","response 실패 ${response.errorBody()?.string()}")
            return null
        }
    }



}