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

    suspend fun getCafeDetail(id: String) :CafeResponse? {
        val response = api.getCafeResponse(cafeId = id)
        return if(response.isSuccessful)
            response.body()
        else{
            Log.d("Detail", "GET Error : ${response.errorBody()?.string()}")
            null
        }
    }


}