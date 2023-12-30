package com.konkuk.mocacong.remote.apis

import com.konkuk.mocacong.remote.models.request.ReIssueRequest
import com.konkuk.mocacong.remote.models.response.ReIssueResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TokenAPI {
    @POST("/login/reissue")
    suspend fun updateAccessToken(
        @Body reIssueRequest: ReIssueRequest
    ): Response<ReIssueResponse>
}