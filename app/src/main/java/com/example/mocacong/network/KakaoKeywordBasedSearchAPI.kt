package com.example.mocacong.network

import com.example.mocacong.data.response.LocalSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface KakaoKeywordBasedSearchAPI {

    @GET("v2/local/search/keyword.json")
    suspend fun getKeywordSearchResponse(
        @Query("query") query : String,
        @Query("category_group_code") code: String = "CE7"
    ) : Response<LocalSearchResponse>

}