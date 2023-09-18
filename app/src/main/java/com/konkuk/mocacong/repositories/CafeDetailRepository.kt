package com.konkuk.mocacong.repositories

import com.konkuk.mocacong.data.objects.NetworkUtil
import com.konkuk.mocacong.data.objects.RetrofitClient
import com.konkuk.mocacong.data.request.ReviewRequest
import com.konkuk.mocacong.data.response.CafeResponse
import com.konkuk.mocacong.data.response.CommentsResponse
import com.konkuk.mocacong.data.response.MyReviewResponse
import com.konkuk.mocacong.data.util.ApiState
import com.konkuk.mocacong.network.CafeDetailAPI

class CafeDetailRepository  {

    private val api = RetrofitClient.create(CafeDetailAPI::class.java)

    suspend fun getCafeDetailInfo(id: String): ApiState<CafeResponse> {
        val response = api.getCafeResponse(cafeId = id)
        return if (response.isSuccessful) ApiState.Success(response.body())
        else {
            val errorResponse = NetworkUtil.getErrorResponse(response.errorBody()!!)
            ApiState.Error(errorResponse = errorResponse!!)
        }
    }

    suspend fun postFavorite(favoriteId: String): ApiState<Void> {
        val response = api.postFavorite(cafeId = favoriteId)
        return if (response.isSuccessful) ApiState.Success(response.body())
        else {
            val errorResponse = NetworkUtil.getErrorResponse(response.errorBody()!!)
            ApiState.Error(errorResponse = errorResponse!!)
        }
    }

    suspend fun deleteFavorite(favoriteId: String): ApiState<Void> {
        val response = api.deleteFavorite(cafeId = favoriteId)
        return if (response.isSuccessful) ApiState.Success(response.body())
        else {
            val errorResponse = NetworkUtil.getErrorResponse(response.errorBody()!!)
            ApiState.Error(errorResponse = errorResponse!!)
        }
    }

    suspend fun getComments(cafeId: String, page: Int): ApiState<CommentsResponse> {
        val response = api.getCafeComment(cafeId = cafeId, page = page)
        return if (response.isSuccessful) ApiState.Success(response.body())
        else {
            val errorResponse = NetworkUtil.getErrorResponse(response.errorBody()!!)
            ApiState.Error(errorResponse = errorResponse!!)
        }
    }

    suspend fun postComment(cafeId: String, content: String) : ApiState<Void>{
        val response = api.postCafeComment(cafeId = cafeId, content = content)
        return if (response.isSuccessful) ApiState.Success(response.body())
        else {
            val errorResponse = NetworkUtil.getErrorResponse(response.errorBody()!!)
            ApiState.Error(errorResponse = errorResponse!!)
        }
    }

    suspend fun postReview(cafeId: String, reviewRequest: ReviewRequest) : ApiState<Void>{
        val response = api.postReview(cafeId = cafeId, myReview = reviewRequest)
        return if (response.isSuccessful) ApiState.Success(response.body())
        else {
            val errorResponse = NetworkUtil.getErrorResponse(response.errorBody()!!)
            ApiState.Error(errorResponse = errorResponse!!)
        }
    }

    suspend fun putReview(cafeId: String, reviewRequest: ReviewRequest) : ApiState<Void>{
        val response = api.putReview(cafeId = cafeId, myReview = reviewRequest)
        return if (response.isSuccessful) ApiState.Success(response.body())
        else {
            val errorResponse = NetworkUtil.getErrorResponse(response.errorBody()!!)
            ApiState.Error(errorResponse = errorResponse!!)
        }
    }
    suspend fun getMyReview(cafeId: String) : ApiState<MyReviewResponse>{
        val response = api.getMyReview(cafeId = cafeId)
        return if (response.isSuccessful) ApiState.Success(response.body())
        else {
            val errorResponse = NetworkUtil.getErrorResponse(response.errorBody()!!)
            ApiState.Error(errorResponse = errorResponse!!)
        }
    }

}