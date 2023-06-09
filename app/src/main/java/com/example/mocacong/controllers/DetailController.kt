package com.example.mocacong.controllers

import android.util.Log
import com.example.mocacong.data.objects.RetrofitClient
import com.example.mocacong.data.request.CafeDetailRequest
import com.example.mocacong.data.response.CafeResponse
import com.example.mocacong.network.CafeDetailAPI

class DetailController {
    val api = RetrofitClient.create(CafeDetailAPI::class.java)

    suspend fun postCafe(cafe: CafeDetailRequest) {
        val response = api.postCafe(cafe)
        Log.d("POST cafe", "${response.raw()}")
    }

    suspend fun getCafeDetail(id: String): CafeResponse? {
        val response = api.getCafeResponse(cafeId = id)
        return if (response.isSuccessful)
            response.body()
        else {
            Log.d("Detail", "GET Error : ${response.errorBody()?.string()}")
            null
        }
    }

    suspend fun postFavorite(id: String): String {
        val response = api.postFavorite(cafeId = id)
        return if (response.isSuccessful)
            "즐겨찾기에 등록되었습니다"
        else {
            Log.d("Detail", "즐찾등록에러 : ${response.errorBody()?.string()}")
            "서버 오류"
        }
    }

    suspend fun deleteFavorite(id: String): String {
        val response = api.deleteFavorite(cafeId = id)
        return if (response.isSuccessful)
            "즐겨찾기에서 해제되었습니다"
        else {
            Log.d("Detail", "즐찾삭제에러 : ${response.errorBody()?.string()}")
            "서버 오류"
        }
    }


}