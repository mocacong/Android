package com.konkuk.mocacong.presentation.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.mocacong.data.entities.BasicPlaceInfo
import com.konkuk.mocacong.presentation.models.CafeCommentsUiModel
import com.konkuk.mocacong.presentation.models.CafeDetailUiModel
import com.konkuk.mocacong.remote.models.request.ReviewRequest
import com.konkuk.mocacong.remote.models.response.CafeImage
import com.konkuk.mocacong.remote.models.response.CafeImageResponse
import com.konkuk.mocacong.remote.models.response.MyReviewResponse
import com.konkuk.mocacong.remote.repositories.CafeDetailRepository
import com.konkuk.mocacong.util.ApiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class CafeDetailViewModel @Inject constructor(private val cafeDetailRepository: CafeDetailRepository) : ViewModel() {
    val TAG = "CafeDetailViewModel"
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
                _cafeDetailInfo.value = CafeDetailUiModel.responseToUIModel(it)
                _isFavorite.value = it.favorite
            }
        )
    }

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    fun requestFavoritePost(isPost: Boolean) =
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
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

    private val _myReview = MutableLiveData<MyReviewResponse>()
    val myReview: LiveData<MyReviewResponse> = _myReview

    fun requestMyReview() {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                cafeDetailRepository.getMyReview(cafeId)
            }
            response.byState(
                onSuccess = {
                    Log.d(TAG, "$it")
                    _myReview.value = it
                    isMyFirstReview = it.myScore == 0
                }
            )
        }
    }

    var isMyFirstReview = true
    val saveReviewResponse = MutableLiveData<ApiState<Unit>>()
    fun saveMyReview(reviewRequest: ReviewRequest) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                return@withContext if (isMyFirstReview)
                    cafeDetailRepository.postReview(cafeId, reviewRequest)
                else
                    cafeDetailRepository.putReview(cafeId, reviewRequest)
            }
            saveReviewResponse.value = response
        }
    }


    var commentPage = 0

    private val _cafeComments = MutableLiveData<CafeCommentsUiModel>()
    val cafeComments: LiveData<CafeCommentsUiModel> = _cafeComments

    fun requestCafeComments(page: Int = commentPage) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                cafeDetailRepository.getComments(cafeId = cafeId, page = page)
            }
            response.byState(
                onSuccess = { commentsResponse ->
                    if (page == 0) {
                        val model = CafeCommentsUiModel.newInstance(page, commentsResponse)
                        _cafeComments.value = model
                    } else {
                        cafeComments.value?.let {
                            val comments = cafeComments.value
                            if (comments != null) {
                                _cafeComments.value = CafeCommentsUiModel.addComments(
                                    comments,
                                    page,
                                    commentsResponse
                                )
                            }
                        }
                    }
                    if (!commentsResponse.isEnd) commentPage++
                }
            )
        }
    }

    val postCommentResponse = MutableLiveData<ApiState<Unit>>()
    fun postComment(content: String) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                cafeDetailRepository.postComment(cafeId, content)
            }
            postCommentResponse.value = response
        }
    }

    var imagePage = 0

    private val _cafeImages = MutableLiveData<CafeImageResponse>()
    val cafeImages: LiveData<CafeImageResponse> = _cafeImages

    val postImagesResponse = MutableLiveData<ApiState<Unit>>()
    fun postMyImage(parts: List<MultipartBody.Part>) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                cafeDetailRepository.postCafeImage(cafeId, parts)
            }
            postImagesResponse.value = response
        }
    }

    fun requestCafeImages(currentPage: Int = imagePage) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                cafeDetailRepository.getCafeImages(cafeId, page = currentPage)
            }
            response.byState(
                onSuccess = {
                    if (imagePage == 0) {
                        //새로생성
                        _cafeImages.value = it
                    } else {
                        //기존에 add
                        val prev = cafeImages.value!!.cafeImages.toMutableList()
                        prev.addAll(it.cafeImages)
                        _cafeImages.value = it.copy(cafeImages = prev)
                    }
                    Log.d(TAG, "ImageResponse: $it")
                }
            )
        }
    }

    var currentImage: CafeImage? = null


    fun deleteComment(id: Long) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                cafeDetailRepository.deleteCafeComment(cafeId = cafeId, commentId = id.toString())
            }
            response.byState(
                onSuccess = {
                    commentPage = 0
                    requestCafeComments()
                }
            )
        }
    }

}