package com.example.mocacong.network

import com.example.mocacong.data.response.LocalSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface KakaoLocationBasedSearchAPI {

    @GET("v2/local/search/category.json")
    suspend fun getLocalSearchResponse(
        @Query("category_group_code") code : String = "CE7",
        @Query("x") x : String,
        @Query("y") y : String,
        @Query("radius") radius : Int = 500,
        @Query("page") page : Int = 2,
        @Query("sort") sort : String = "distance",
    ) : Response<LocalSearchResponse>
}