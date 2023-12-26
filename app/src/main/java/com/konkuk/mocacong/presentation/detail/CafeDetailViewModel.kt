package com.konkuk.mocacong.presentation.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.mocacong.data.entities.BasicPlaceInfo
import com.konkuk.mocacong.presentation.models.CafeDetailUiModel
import com.konkuk.mocacong.remote.models.response.MyReviewResponse
import com.konkuk.mocacong.remote.repositories.CafeDetailRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CafeDetailViewModel(private val cafeDetailRepository: CafeDetailRepository) : ViewModel() {
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
    val isFavorite : LiveData<Boolean> = _isFavorite

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

    private val _myReview = MutableLiveData<MyReviewResponse>()
    val myReview : LiveData<MyReviewResponse> = _myReview

    fun requestMyReview() {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO){
                cafeDetailRepository.getMyReview(cafeId)
            }
            response.byState(
                onSuccess = {
                    Log.d(TAG, "$it")
                    _myReview.value = it
                }
            )
        }
    }
}