package com.konkuk.mocacong.util

import com.kakao.sdk.common.Constants.AUTHORIZATION
import com.konkuk.mocacong.remote.apis.TokenAPI
import com.konkuk.mocacong.remote.models.request.ReIssueRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = runBlocking {
            TokenManager.getRefreshToken().first()
        }
        if (refreshToken.isNullOrBlank()) {
            response.close()
            return null
        }
        val newToken = getUpdatedToken(refreshToken)

        return if (!newToken.isNullOrBlank()) {
            CoroutineScope(Dispatchers.IO).launch {
                TokenManager.saveAccessToken(newToken)
            }
            newRequestWithToken(newToken, response.request)
        } else null
    }

    private fun newRequestWithToken(token: String, request: Request): Request =
        request.newBuilder()
            .header(AUTHORIZATION, token)
            .build()

    private fun getUpdatedToken(refreshToken: String): String? {
        val response = runBlocking {
            RetrofitClient.create(TokenAPI::class.java).updateAccessToken(
                ReIssueRequest(refreshToken)
            )
        }

        return if (response.isSuccessful && response.body() != null) {
            response.body()!!.accessToken
        } else null
    }

}