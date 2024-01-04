package com.konkuk.mocacong.remote.repositories

import com.konkuk.mocacong.remote.apis.MyPageAPI
import com.konkuk.mocacong.util.MocacongRetrofit
import okhttp3.MultipartBody
import retrofit2.Retrofit
import javax.inject.Inject

class MypageRepository @Inject constructor(
    private val mypageApi: MyPageAPI,
    @MocacongRetrofit retrofit: Retrofit
) : BaseRepository(retrofit) {

    suspend fun getMyComments(page: Int) =
        makeRequest { mypageApi.getMyComments(page = page) }

    suspend fun getMyReviews(page: Int) =
        makeRequest { mypageApi.getMyReviews(page = page) }

    suspend fun getMyFavs(page: Int) =
        makeRequest { mypageApi.getMyFavorites(page = page) }

    suspend fun getMyProfile() =
        makeRequest { mypageApi.getMyProfile() }

    suspend fun putMyProfileImg(part: MultipartBody.Part) =
        makeRequest { mypageApi.putMyProfileImage(file = part) }
}