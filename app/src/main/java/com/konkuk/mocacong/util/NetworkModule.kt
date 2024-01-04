package com.konkuk.mocacong.util

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kakao.sdk.common.Constants.AUTHORIZATION
import com.konkuk.mocacong.remote.apis.*
import com.konkuk.mocacong.remote.models.response.ReIssueResponse
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MocacongRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class KakaoRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MocacongClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class KakaoClient

@Module
@InstallIn(SingletonComponent::class)

object NetworkModule {

    private const val TAG = "NetworkModule"
//    private const val BASE_URL = "http://3.37.64.38:8080/"

    private const val BASE_URL = "https://mocacong.com/"
    private const val KAKAO_BASE_URL = "https://dapi.kakao.com/"

    @MocacongClient
    @Provides
    @Singleton
    fun provideClient(
        interceptor: Interceptor, authenticator: Authenticator
    ): OkHttpClient = createClient(interceptor, authenticator)

    @KakaoClient
    @Provides
    @Singleton
    fun provideKakaoClient(
        interceptor: KakaoInterceptor
    ): OkHttpClient = createClient(interceptor, null)

    private fun createClient(
        interceptor: Interceptor?, authenticator: Authenticator?
    ): OkHttpClient {
        val client = OkHttpClient.Builder()
        if (authenticator != null) client.authenticator(authenticator)
        if (interceptor != null) client.addInterceptor(interceptor)
        return client.connectTimeout(5, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS).build()
    }


    // Retrofit 객체 생성
    @MocacongRetrofit
    @Provides
    @Singleton
    fun provideMocacongRetrofit(@MocacongClient client: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL).client(client).addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder().setLenient().create()
            )
        ).build()
    }

    @KakaoRetrofit
    @Provides
    @Singleton
    fun provideKakaoRetrofit(@KakaoClient client: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl(KAKAO_BASE_URL).client(client).addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder().setLenient().create()
            )
        ).build()
    }


    @Provides
    @Singleton
    fun provideInterceptor(): Interceptor = Interceptor { chain ->
        val token: String? = runBlocking {
            TokenManager.getAccessToken().first()
        }
        Log.d("Network", "interceptor accessToken : $token")

        val request = chain.request().newBuilder().header(AUTHORIZATION, "Bearer $token").build()
        Log.d("Network", "request proceed 전: $request")

        val response = chain.proceed(request)
        if (!response.isSuccessful) {
            val errorJson = JSONObject(response.peekBody(2048).string())
            try {
                val code = errorJson.getInt("code")
                val message = errorJson.getString("message")
                Log.e("interceptor", "Network Error 발생! code = $code, message = $message")
            } catch (e: JSONException) {
                Log.e("interceptor", "Json Body is null $errorJson")
            }
        }
        response
    }


    class KakaoInterceptor @Inject constructor() : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
            val newRequest = request().newBuilder()
                .addHeader("Authorization", "KakaoAK 3e7c72e35c239c324a59419fa82812a4").build()
            proceed(newRequest)
        }
    }


    @Provides
    @Singleton
    fun provideAuthenticator(
        @ApplicationContext context: Context
    ): Authenticator {

        val authenticatorClient = createClient(null, null)

        fun newRequestWithToken(token: String, request: Request): Request =
            request.newBuilder().removeHeader(AUTHORIZATION)
                .addHeader(AUTHORIZATION, "Bearer $token").build()

        fun refresh(response: Response): Request? {
            val refreshToken = runBlocking {
                TokenManager.getRefreshToken().first()
            }
            if (refreshToken.isNullOrBlank()) {
                Log.d(TAG, "RefreshToken is NULL")
                return null
            }

            val refreshRequest = Request.Builder().url(BASE_URL + "login/reissue").post(
                "{\"refreshToken\": \"${refreshToken}\"}".toRequestBody("application/json".toMediaType())
            ).build()

            val newRequest = runBlocking {
                val refreshResponse = authenticatorClient.newCall(refreshRequest).execute()
                if (refreshResponse.isSuccessful && refreshResponse.body != null) {
                    val newToken = refreshResponse.peekBody(2048).string()
                    val newTokenObj = Gson().fromJson(newToken, ReIssueResponse::class.java)
                    TokenManager.saveAccessToken(newTokenObj.accessToken)
                    return@runBlocking newRequestWithToken(
                        newTokenObj.accessToken, response.request
                    )
                } else throw java.lang.RuntimeException()
            }

            return newRequest
        }


        return Authenticator { route, response ->
            val TAG = "Authenticator"
            val errorJson = JSONObject(response.peekBody(2048).string())
            val code = errorJson.getInt("code")
            when (code) {
                1013 -> {
                    //로그인안됨
                }
                1014 -> {
                    //액세스 기한 만료
                    val newRequest = refresh(response)
                    return@Authenticator newRequest
                }
                1021 -> {
                    //올바르지 않은 리프레시 토큰
                    context.forceRestart()
                }
                1022 -> {
                    //액세스 만료되지 않았음
                    response.request
                }
            }

            null
        }
    }


    @Provides
    @Singleton
    fun provideDetailApi(@MocacongRetrofit retrofit: Retrofit): CafeDetailAPI =
        retrofit.create(CafeDetailAPI::class.java)

    @Provides
    @Singleton
    fun provideMapApi(@MocacongRetrofit retrofit: Retrofit): MapApi =
        retrofit.create(MapApi::class.java)


    @Provides
    @Singleton
    fun provideMypageApi(@MocacongRetrofit retrofit: Retrofit): MyPageAPI =
        retrofit.create(MyPageAPI::class.java)

    @Provides
    @Singleton
    fun provideTokenApi(@MocacongRetrofit retrofit: Retrofit): TokenAPI =
        retrofit.create(TokenAPI::class.java)

    @Provides
    @Singleton
    fun provideAuthApi(@MocacongRetrofit retrofit: Retrofit): AuthAPI =
        retrofit.create(AuthAPI::class.java)

    @Provides
    @Singleton
    fun provideKakaoSearchApi(@KakaoRetrofit retrofit: Retrofit): KakaoSearchAPI =
        retrofit.create(KakaoSearchAPI::class.java)
}
