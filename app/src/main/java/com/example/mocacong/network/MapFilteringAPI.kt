package com.example.mocacong.network

import com.example.mocacong.data.objects.Member
import com.example.mocacong.data.request.FilteringRequest
import com.example.mocacong.data.response.ProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MapFilteringAPI {

    @GET("/cafes")
    suspend fun getFilteredCafes(
        @Header("Authorization") token: String? = "Bearer ${Member.getAuthToken()}",
        @Query("studyType") studyType : String,
        @Body filteringRequest: FilteringRequest
    ): Response<List<String>>

}