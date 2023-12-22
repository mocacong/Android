package com.konkuk.mocacong.presentation.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.mocacong.data.entities.BasicPlaceInfo
import com.konkuk.mocacong.presentation.models.CafeDetailUiModel
import com.konkuk.mocacong.remote.models.request.ReviewRequest
import com.konkuk.mocacong.remote.models.response.CommentsResponse
import com.konkuk.mocacong.remote.models.response.MyReviewResponse
import com.konkuk.mocacong.remote.repositories.CafeDetailRepository
import com.konkuk.mocacong.util.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CafeDetailViewModel(private val cafeDetailRepository: CafeDetailRepository) : ViewModel() {
    lateinit var cafeId: String

    private val _cafeBasicInfo = MutableLiveData<BasicPlaceInfo>()
    val cafeBasicInfo: LiveData<BasicPlaceInfo> = _cafeBasicInfo

    private val _cafeDetailInfo = MutableLiveData<CafeDetailUiModel>()
    val cafeDetailInfo: LiveData<CafeDetailUiModel> = _cafeDetailInfo

    fun setBasicInfo(placeInfo: BasicPlaceInfo) {
        _cafeBasicInfo.value = placeInfo
    }

    fun requestCafeDetailInfo() = viewModelScope.launch {
        val response = withContext(Dispatchers.IO) {
            cafeDetailRepository.getCafeDetailInfo(cafeId)
        }
        response.byState(
            onSuccess = {
                _cafeDetailInfo.value = CafeDetailUiModel.responseToModel(it)
                _isFavorite.value = it.favorite
            }
        )
    }

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite = _isFavorite

    fun requestFavoritePost(isPost: Boolean) =
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO){
                if (isPost) {
                    cafeDetailRepository.postFavorite(cafeId)
                } else {
                    cafeDetailRepository.deleteFavorite(cafeId)
                }
            }
            response.byState(
                onSuccess = {
                    _isFavorite.value = isPost
                })
        }


    var mCommentsResponseResponse: MutableLiveData<ApiState<CommentsResponse>> =
        MutableLiveData(ApiState.Loading())
    var commentsResponseResponse: LiveData<ApiState<CommentsResponse>> = mCommentsResponseResponse

    var mCommentPostResponse: MutableLiveData<ApiState<Unit>> = MutableLiveData(ApiState.Loading())
    var commentPostResponse: LiveData<ApiState<Unit>> = mCommentPostResponse

    var mPostReviewResponse: MutableLiveData<ApiState<Unit>> = MutableLiveData(ApiState.Loading())
    var postReviewResponse: LiveData<ApiState<Unit>> = mPostReviewResponse

    var mPutReviewResponse: MutableLiveData<ApiState<Unit>> = MutableLiveData(ApiState.Loading())
    var putReviewResponse: LiveData<ApiState<Unit>> = mPutReviewResponse

    var mMyReviewResponse: MutableLiveData<ApiState<MyReviewResponse>> =
        MutableLiveData(ApiState.Loading())
    var myReviewResponse: LiveData<ApiState<MyReviewResponse>> = mMyReviewResponse

    var isCommentEditing: Boolean = false

    //isLoading, errorState livedata로 한 번에 관리


    fun requestCafeComments(page: Int) = viewModelScope.launch(Dispatchers.IO) {
        mCommentsResponseResponse.postValue(ApiState.Loading())
        mCommentsResponseResponse.postValue(cafeDetailRepository.getComments(cafeId, page))
    }

    fun postMyComment(content: String) = viewModelScope.launch(Dispatchers.IO) {
        mCommentPostResponse.postValue(ApiState.Loading())
        mCommentPostResponse.postValue(cafeDetailRepository.postComment(cafeId, content))
    }

    fun postMyReview(review: ReviewRequest) = viewModelScope.launch(Dispatchers.IO) {
        mPostReviewResponse.postValue(ApiState.Loading())
        mPostReviewResponse.postValue(cafeDetailRepository.postReview(cafeId, review))
    }

    fun putMyReview(review: ReviewRequest) = viewModelScope.launch(Dispatchers.IO) {
        mPutReviewResponse.postValue(ApiState.Loading())
        mPutReviewResponse.postValue(cafeDetailRepository.putReview(cafeId, review))
    }

    fun getMyReview() = viewModelScope.launch(Dispatchers.IO) {
        mMyReviewResponse.postValue(ApiState.Loading())
        mMyReviewResponse.postValue(cafeDetailRepository.getMyReview(cafeId))
    }
}