package com.konkuk.mocacong.presentation.main.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.konkuk.mocacong.data.entities.MapMarker
import com.konkuk.mocacong.remote.models.request.FilteringRequest
import com.konkuk.mocacong.remote.models.request.PostCafeRequest
import com.konkuk.mocacong.remote.models.response.CafePreviewResponse
import com.konkuk.mocacong.remote.models.response.Place
import com.konkuk.mocacong.remote.repositories.MapRepository
import com.konkuk.mocacong.util.ApiState
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.MarkerIcons
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapViewModel(val mapRepository: MapRepository) : ViewModel() {
    private val TAG = "MapViewModel"

    val mapMarkers = HashMap<String, MapMarker>()


    val _newPlaces = MutableLiveData<List<String>>()
    val newPlaces : LiveData<List<String>> = _newPlaces

    fun updateMarkers(x: String, y: String, radius: Int) = viewModelScope.launch(Dispatchers.IO) {
        withContext(Dispatchers.Main) {
            if (mapMarkers.size > 70) removeMarkers(mapMarkers.keys.toList())
        }
        Log.d(TAG, "updateMarkers")
        mapRepository.getPlaces(x, y, radius).byState({ response->
            response.documents.let {
                Log.d(TAG, "카카오 검색 결과: $it")
                _newPlaces.postValue(it.filter { place ->
                    //현 지도 없는 마커만 필터링
                    !mapMarkers.containsKey(place.id)
                }.map { place ->
                    //장소 추가, newMarkers값 바꿈
                    createMarker(place)
                    place.id
                })
            }
        })
    }

    private fun createMarker(place: Place): MapMarker {
        Log.d(TAG, "[New Marker] ${place.place_name}")
        val marker = Marker(LatLng(place.y.toDouble(), place.x.toDouble()))
        val mapMarker = MapMarker(
            marker,
            mapId = place.id,
            name = place.place_name,
            roadAddress = place.road_address_name.toString(),
            phoneNumber = place.phone.toString()
        )
        mapMarkers[place.id] = mapMarker
        marker.apply {
            captionText = place.place_name
            tag = 0
            captionMinZoom = 9.0
            isHideCollidedSymbols = true
            setOnClickListener {
                onMarkerClicked(mapMarker)
                true
            }
        }

        return mapMarker
    }



    val _filteredPlaces = MutableLiveData<List<MapMarker>>()
    val filteredPlaces : LiveData<List<MapMarker>> = _filteredPlaces
    fun requestFiltering(ids: List<String>) {
        val fr = FilteringRequest(ids)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                filteringAsync(fr, MapMarker.Type.SOLO)
                filteringAsync(fr, MapMarker.Type.GROUP)
            }
            withContext(Dispatchers.IO) {
                filteringAsync(fr, MapMarker.Type.BOTH)
            }
            withContext(Dispatchers.IO) {
                filteringAsync(fr, MapMarker.Type.FAV)
            }

            _filteredPlaces.value = mapMarkers.filter {
                ids.contains(it.key)
            }.map { it.value }

        }
    }

    private suspend fun filteringAsync(fr: FilteringRequest, type: MapMarker.Type) =
        viewModelScope.async(Dispatchers.IO) {
            if (type == MapMarker.Type.FAV) {
                mapRepository.filterFavorite(fr).byState(
                    onSuccess = { response ->
                        Log.d(TAG, "[Filtered Response] Type = $type, markers = $response")
                        val markers = response.mapIds.map { id ->
                            mapMarkers[id]?.type = type
                            mapMarkers[id]
                        }
                        return@byState markers
                    })
            } else {
                mapRepository.filterStudyType(type.name.lowercase(), fr).byState(
                    onSuccess = { response ->
                        Log.d(TAG, "[Filtered Response] Type = $type, markers = $response")
                        val markers = response.mapIds.map { id ->
                            mapMarkers[id]?.type = type
                            mapMarkers[id]
                        }
                        return@byState markers
                    })
            }
        }.await()

    val _clickedMarker = MutableLiveData<MapMarker?>(null)
    val clickedMarker : LiveData<MapMarker?> = _clickedMarker

    private fun onMarkerClicked(mapMarker: MapMarker) {
        clickedMarker.value?.let {
            requestFiltering(listOf(it.mapId))
        }
        Log.d(TAG, "[Clicked] ${mapMarker.name}")
        _clickedMarker.value = mapMarker
        mapMarker.marker.icon = MarkerIcons.YELLOW
    }

    private val _postCafeResponse = MutableLiveData<ApiState<Unit>>()
    val postCafeResponse : LiveData<ApiState<Unit>> = _postCafeResponse
    fun postCafe(mapMarker: MapMarker) = viewModelScope.launch(Dispatchers.IO) {
        val request = PostCafeRequest(mapId = mapMarker.mapId, name = mapMarker.name, roadAddress = mapMarker.roadAddress, phoneNumber = mapMarker.phoneNumber)
        Log.d(TAG, "[Post Cafe] $request")
        this@MapViewModel._postCafeResponse.postValue(mapRepository.postCafe(request))
    }

    fun removeMarkers(ids: List<String>) {
        Log.d(TAG, "[Remove] $ids")
        ids.forEach {
            mapMarkers[it]?.marker?.map = null
            mapMarkers.remove(it)
        }
    }

    private val _cafePreviewResponse = MutableLiveData<ApiState<CafePreviewResponse>>()
    val cafePreviewResponse: LiveData<ApiState<CafePreviewResponse>> = _cafePreviewResponse

    fun requestPreviewInfo(id: String) = viewModelScope.launch(Dispatchers.IO) {
        _cafePreviewResponse.postValue(mapRepository.getPreview(id))
    }
}