package com.konkuk.mocacong.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.mocacong.data.request.FilteringRequest
import com.konkuk.mocacong.data.response.*
import com.konkuk.mocacong.data.util.ApiState
import com.konkuk.mocacong.repositories.MapRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel(private val mapRepository: MapRepository) : ViewModel() {
    val TAG = "Map"

    init {
        Log.d(TAG, "Map 뷰모델 생성됨")
    }

    var mFilteredCafesFlow: MutableStateFlow<ApiState<FilteringResponse>> =
        MutableStateFlow(ApiState.Loading())
    var filteredCafesFlow: StateFlow<ApiState<FilteringResponse>> = mFilteredCafesFlow

    var mfavoriteFlow: MutableStateFlow<ApiState<FilteringResponse>> =
        MutableStateFlow(ApiState.Loading())
    var favoriteFlow: StateFlow<ApiState<FilteringResponse>> = mfavoriteFlow

    var mPlaceByLocation: MutableStateFlow<ApiState<LocalSearchResponse>> =
        MutableStateFlow(ApiState.Loading())
    var placeByLocation: StateFlow<ApiState<LocalSearchResponse>> = mPlaceByLocation

    var mPreviewInfo: MutableStateFlow<ApiState<CafePreviewResponse>> =
        MutableStateFlow(ApiState.Loading())
    var previewInfo: StateFlow<ApiState<CafePreviewResponse>> = mPreviewInfo

    var mProfileInfo:MutableStateFlow<ApiState<ProfileResponse>> =
        MutableStateFlow(ApiState.Loading())
    var profileInfo: StateFlow<ApiState<ProfileResponse>> = mProfileInfo


    fun requestFilterStudyType(type: String, filteringRequest: FilteringRequest) =
        viewModelScope.launch(Dispatchers.IO) {
            mFilteredCafesFlow.value = ApiState.Loading()
            val result = mapRepository.getFilterings(type, filteringRequest)
            mFilteredCafesFlow.value = result
        }

    fun requestFavorites(filteringRequest: FilteringRequest) =
        viewModelScope.launch(Dispatchers.IO) {
            mfavoriteFlow.value = ApiState.Loading()
            val result = mapRepository.getFavorites(filteringRequest)
            mfavoriteFlow.value = result
        }

    fun requestMapCafeLists(mapx: String, mapy: String) = viewModelScope.launch(Dispatchers.IO) {
        mPlaceByLocation.value = ApiState.Loading()
        val result = mapRepository.getLocationBasedCafes(mapx, mapy)
        mPlaceByLocation.value = result
    }

    fun requestPreviewInfo(cafeId: String) = viewModelScope.launch(Dispatchers.IO) {
        mPreviewInfo.value = ApiState.Loading()
        mPreviewInfo.value = mapRepository.getPreviewInfo(cafeId = cafeId)
    }

    fun requestMemeberProfile() = viewModelScope.launch (Dispatchers.IO){
        mProfileInfo.value = ApiState.Loading()
        mProfileInfo.value = mapRepository.getProfileInfo()
    }
}