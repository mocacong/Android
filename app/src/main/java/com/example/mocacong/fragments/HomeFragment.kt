package com.example.mocacong.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.mocacong.R
import com.example.mocacong.activities.MainActivity
import com.example.mocacong.data.objects.Utils
import com.example.mocacong.data.request.FilteringRequest
import com.example.mocacong.data.response.Place
import com.example.mocacong.data.util.ApiState
import com.example.mocacong.data.util.TokenExceptionHandler
import com.example.mocacong.databinding.FragmentHomeBinding
import com.example.mocacong.viewmodels.MapViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.coroutines.launch
import java.io.Serializable

class HomeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val TAG = "Map"

    private val mapViewModel: MapViewModel by activityViewModels()

    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val markers = HashMap<Place, Marker>()
    private lateinit var markerImg: OverlayImage
    private lateinit var markerFavImg: OverlayImage

    private var clickedMarker: Marker? = null

    private var isLoading = false

    private val LOCATION_PERMISSION_REQUEST_CODE = 1000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        getMapFragment()
        return binding.root
    }

    private fun <T : Serializable> Bundle.argSerializable(key: String, clazz: Class<T>): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.getSerializable(key, clazz)
        } else {
            this.getSerializable(key) as T?
        }
    }

    private fun gotoSearchedPlace() {
        val searchedPlace = arguments?.argSerializable("searchedPlace", Place::class.java)
        if (searchedPlace != null) {
            Log.d("search", "searched 받았음 : $searchedPlace")

            val cameraUpdate = CameraUpdate.scrollTo(
                LatLng(
                    searchedPlace.y.toDouble(), searchedPlace.x.toDouble()
                )
            ).animate(CameraAnimation.Easing).finishCallback {
                addMarker(searchedPlace)
                markers[searchedPlace]?.performClick()
            }
            naverMap.moveCamera(cameraUpdate)
        }
    }

    private fun setLayout() {
        binding.searchBar.setOnClickListener {
            val activity = requireActivity()
            if(activity is MainActivity){
                activity.addFragment(SearchFragment())
            }
        }

        binding.filteringSoloBtn.setOnClickListener {
            it.isSelected = !it.isSelected
            refreshMarkerList()
        }

        binding.filteringGroupBtn.setOnClickListener {
            it.isSelected = !it.isSelected
            refreshMarkerList()
        }

    }

    private fun requestFiltering() {
        val isGroupClicked = binding.filteringGroupBtn.isSelected
        val isSoloClicked = binding.filteringSoloBtn.isSelected
        when {
            isSoloClicked && isGroupClicked -> {
                filterMarkers("both")
            }
            isSoloClicked -> {
                filterMarkers("solo")
            }
            isGroupClicked -> {
                filterMarkers("group")
            }
            else -> {

            }
        }
    }

    private fun filterMarkers(type: String) =
        lifecycleScope.launch {
            mapViewModel.apply {
                val requestIds = FilteringRequest(markers.keys.map { it.id })
                requestFilterStudyType(type, requestIds).join()
                when (val apiState = filteredCafesFlow.value) {
                    is ApiState.Success -> {
                        apiState.data?.let { response ->
                            if (response.mapIds.isEmpty()) {
                                val msg = when (type) {
                                    "solo" -> {
                                        "혼카콩"
                                    }
                                    "group" -> {
                                        "모카콩"
                                    }
                                    else -> {
                                        "혼카콩/모카콩"
                                    }
                                }
                                Utils.showToast(
                                    requireContext(),
                                    "현재 지도 반경 500m 내 ${msg} 카페가 없습니다"
                                )
                            }
                            markers.forEach { (place, marker) ->
                                if (response.mapIds.contains(place.id)) {
                                    marker.alpha = 1F
                                    marker.setOnClickListener {
                                        if (marker.tag == 0) {
                                            markerFirstClicked(marker)
                                            createPreview(place)
                                            //마커 한 번 클릭
                                        }
                                        true
                                    }
                                } else {
                                    marker.alpha = 0.15f
                                    marker.setOnClickListener { true }
                                }
                            }
                        }
                    }
                    is ApiState.Error -> {
                        apiState.errorResponse?.let { er ->
                            TokenExceptionHandler.handleTokenException(requireContext(), er)
                            Log.e(TAG, er.message)
                        }
                        mFilteredCafesFlow.value = ApiState.Loading()
                    }
                    is ApiState.Loading -> {}
                }
            }
        }


    private fun filterFavs() =
        lifecycleScope.launch {
            mapViewModel.apply {
                val requestIds = FilteringRequest(markers.keys.map { it.id })
                requestFavorites(requestIds).join()
                when (val apiState = favoriteFlow.value) {
                    is ApiState.Success -> {
                        apiState.data?.let { response ->
                            markers.forEach { place, marker ->
                                if (response.mapIds.contains(place.id)) {
                                    marker.icon = markerFavImg
                                }
                            }
                        }
                    }
                    is ApiState.Error -> {
                        apiState.errorResponse?.let { er ->
                            TokenExceptionHandler.handleTokenException(requireContext(), er)
                            Log.e(TAG, er.message)
                        }
                        mfavoriteFlow.value = ApiState.Loading()
                    }
                    is ApiState.Loading -> {}
                }
            }
        }


    //위치 권한 요청
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        Log.d("map", "들어왔다")
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            } else {
                naverMap.locationTrackingMode = LocationTrackingMode.Follow // 현위치 버튼 컨트롤 활성
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    private fun getMapFragment() {
        //네이버 지도 Fragment 불러오기
        val fm = childFragmentManager
        val mapFragment =
            fm.findFragmentById(R.id.map) as MapFragment? ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap) {
        //지도 객체 세팅
        Log.d("MAP", "객체 초기화")
        this.naverMap = naverMap

        //현위치 버튼 활성 및 줌 버튼 제거
        naverMap.uiSettings.apply {
            isZoomControlEnabled = false
            isLocationButtonEnabled = true
        }
        naverMap.cameraPosition = CameraPosition(
            LatLng(37.5666102, 126.9783881), 16.0
        )
        naverMap.locationSource = locationSource
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        setCurrentLocation()

        markerImg = OverlayImage.fromResource(R.drawable.marker_origin)
        markerFavImg = OverlayImage.fromResource(R.drawable.marker_fav)

        naverMap.addOnCameraIdleListener {
            refreshMarkerList()
        }

        naverMap.setOnMapClickListener { _, _ ->
            revertMarker()
        }

        setLayout()
        gotoSearchedPlace()


    }

    private fun setCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { loc ->
            if (loc != null) {
                val cameraUpdate = CameraUpdate.scrollTo(
                    LatLng(
                        loc.latitude, loc.longitude
                    )
                )
                naverMap.moveCamera(cameraUpdate)
            } else {
                Utils.showToast(requireContext(), "현재 위치 정보를 가져올 수 없습니다")
            }
        }.addOnFailureListener { e ->
            Log.d("TAG", "위치정보 못가져옴 ${e.message}")
        }
    }

    fun revertMarker() {
        if (clickedMarker != null) {
            clickedMarker!!.icon = markerImg
            clickedMarker!!.captionColor = Color.BLACK
            clickedMarker!!.tag = 0
            clickedMarker = null
        }
    }

    private fun refreshMarkerList() {
        if (isLoading) return
        //마커 45개 이상 시 전부 삭제
        delMarkers()
        val y = naverMap.cameraPosition.target.latitude.toString()
        val x = naverMap.cameraPosition.target.longitude.toString()
        lifecycleScope.launch {
            isLoading = true
            mapViewModel.apply {
                requestMapCafeLists(x, y).join()
                when (val state = placeByLocation.value) {
                    is ApiState.Success -> {
                        state.data?.let {
                            it.documents.forEach { place ->
                                //없으면 추가하기
                                if (!markers.containsKey(place)) addMarker(place)
                            }
                            isLoading = false
                            filterFavs()
                            requestFiltering()
                            addMarkersToMap()
                        }
                    }
                    is ApiState.Error -> {
                        state.errorResponse?.let { er ->
                            TokenExceptionHandler.handleTokenException(requireContext(), er)
                            Log.e(TAG, er.message)
                        }
                        mPlaceByLocation.value = ApiState.Loading()
                    }
                    is ApiState.Loading -> {
                    }
                }
            }
        }
    }

    private fun addMarkersToMap() {
        markers.forEach{
            it.value.map = naverMap
        }
    }

    private fun addMarker(place: Place?) {
        place?.let {
            Log.d(TAG, "addMarker 호출 place = ${place.place_name}")
            val marker = Marker()
            markers[place] = marker
            marker.tag = 0
            marker.position = LatLng(place.y.toDouble(), place.x.toDouble())
            marker.captionText = place.place_name
            marker.captionMinZoom = 9.0
            marker.icon = markerImg
            marker.isHideCollidedSymbols = true

            marker.setOnClickListener {
                if (marker.tag == 0) {
                    markerFirstClicked(marker)
                    createPreview(place)
                    //마커 한 번 클릭
                }
                true
            }
        }
    }


    private fun createPreview(cafe: Place) {
        val previewFragment = CafePreviewFragment.newInstance(cafe)
        previewFragment.show(childFragmentManager, "CafePreviewFragment")
    }


    private fun markerFirstClicked(marker: Marker) {
        revertMarker()
        clickedMarker = marker
        marker.captionColor = Color.GREEN
        marker.icon = Marker.DEFAULT_ICON
        marker.tag = 1
    }


    private fun delMarkers() {
        markers.forEach { (_, marker) ->
            marker.map = null
        }
        markers.clear()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
