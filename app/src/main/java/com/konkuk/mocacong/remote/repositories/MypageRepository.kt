package com.konkuk.mocacong.remote.repositories

import com.konkuk.mocacong.objects.RetrofitClient
import com.konkuk.mocacong.remote.apis.MyPageAPI

class MypageRepository : BaseRepository() {

    val mypageApi = RetrofitClient.create(MyPageAPI::class.java)

    suspend fun getMyComments(page: Int) =
        makeRequest { mypageApi.getMyComments(page = page) }

    suspend fun getMyReviews(page: Int) =
        makeRequest { mypageApi.getMyReviews(page = page) }

    suspend fun getMyFavs(page: Int) =
        makeRequest { mypageApi.getMyFavorites(page = page) }
}