package com.konkuk.mocacong.remote.repositories

import com.konkuk.mocacong.remote.apis.CafeDetailAPI
import com.konkuk.mocacong.remote.models.request.ReviewRequest
import com.konkuk.mocacong.remote.models.response.CafeImageResponse
import com.konkuk.mocacong.remote.models.response.CafeResponse
import com.konkuk.mocacong.remote.models.response.CommentsResponse
import com.konkuk.mocacong.remote.models.response.MyReviewResponse
import com.konkuk.mocacong.util.ApiState
import com.konkuk.mocacong.util.MocacongRetrofit
import okhttp3.MultipartBody
import retrofit2.Retrofit
import javax.inject.Inject

class CafeDetailRepository @Inject constructor(private val api: CafeDetailAPI,@MocacongRetrofit retrofit: Retrofit) :
    BaseRepository(retrofit) {

    suspend fun getCafeDetailInfo(id: String): ApiState<CafeResponse> =
        makeRequest { api.getCafeResponse(cafeId = id) }

    suspend fun postFavorite(favoriteId: String): ApiState<Unit> =
        makeRequest { api.postFavorite(cafeId = favoriteId) }

    suspend fun deleteFavorite(favoriteId: String): ApiState<Unit> =
        makeRequest { api.deleteFavorite(cafeId = favoriteId) }

    suspend fun getComments(cafeId: String, page: Int): ApiState<CommentsResponse> =
        makeRequest { api.getCafeComment(cafeId = cafeId, page = page) }

    suspend fun postComment(cafeId: String, content: String): ApiState<Unit> =
        makeRequest { api.postCafeComment(cafeId = cafeId, content = content) }

    suspend fun postReview(cafeId: String, reviewRequest: ReviewRequest): ApiState<Unit> =
        makeRequest { api.postReview(cafeId = cafeId, myReview = reviewRequest) }

    suspend fun putReview(cafeId: String, reviewRequest: ReviewRequest): ApiState<Unit> =
        makeRequest { api.putReview(cafeId = cafeId, myReview = reviewRequest) }

    suspend fun getMyReview(cafeId: String): ApiState<MyReviewResponse> =
        makeRequest { api.getMyReview(cafeId = cafeId) }

    suspend fun postCafeImage(cafeId: String, body: List<MultipartBody.Part>): ApiState<Unit> =
        makeRequest { api.postCafeImages(cafeId = cafeId, files = body) }

    suspend fun getCafeImages(cafeId: String, page: Int): ApiState<CafeImageResponse> =
        makeRequest { api.getCafeImages(cafeId = cafeId, page = page) }

    suspend fun deleteCafeComment(cafeId: String, commentId: String): ApiState<Unit> =
        makeRequest { api.deleteComment(cafeId = cafeId, commentId = commentId) }
}