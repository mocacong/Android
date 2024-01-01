package com.konkuk.mocacong.remote.repositories

import com.konkuk.mocacong.remote.apis.TokenAPI
import com.konkuk.mocacong.remote.models.request.ReIssueRequest
import com.konkuk.mocacong.remote.models.response.ReIssueResponse
import com.konkuk.mocacong.util.ApiState
import com.konkuk.mocacong.util.RetrofitClient

class TokenRepository: BaseRepository() {
    val api = RetrofitClient.create(TokenAPI::class.java)

    suspend fun refresh(reIssueRequest: ReIssueRequest): ApiState<ReIssueResponse> =
        makeRequest { api.updateAccessToken(reIssueRequest) }

}