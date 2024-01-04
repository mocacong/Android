package com.konkuk.mocacong.remote.repositories

import com.konkuk.mocacong.remote.apis.TokenAPI
import com.konkuk.mocacong.remote.models.request.ReIssueRequest
import com.konkuk.mocacong.remote.models.response.ReIssueResponse
import com.konkuk.mocacong.util.ApiState
import com.konkuk.mocacong.util.MocacongRetrofit
import retrofit2.Retrofit
import javax.inject.Inject

class TokenRepository @Inject constructor(
    private val api: TokenAPI,
    @MocacongRetrofit retrofit: Retrofit
) :
    BaseRepository(retrofit) {

    suspend fun refresh(reIssueRequest: ReIssueRequest): ApiState<ReIssueResponse> =
        makeRequest { api.updateAccessToken(reIssueRequest) }

}