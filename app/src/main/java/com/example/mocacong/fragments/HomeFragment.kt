package com.example.mocacong.fragments

import android.Manifest
import android.content.Intent
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
import androidx.lifecycle.lifecycleScope
import com.example.mocacong.R
import com.example.mocacong.activities.SearchActivity
import com.example.mocacong.controllers.SearchController
import com.example.mocacong.data.objects.RetrofitClient
import com.example.mocacong.data.objects.Utils
import com.example.mocacong.data.request.FilteringRequest
import com.example.mocacong.data.response.FilteringResponse
import com.example.mocacong.data.response.Place
import com.example.mocacong.databinding.FragmentHomeBinding
import com.example.mocacong.network.MapApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.Serializable

class HomeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var searchController: SearchController


    private val markers = HashMap<Place, Marker>()
    private lateinit var markerImg: OverlayImage
    private var clickedMarker: Marker? = null

    private var isSoloClicked = false
    private var isGroupClicked = false
    private var isFavClicked = false

    private val LOCATION_PERMISSION_REQUEST_CODE = 1000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        searchController = SearchController()
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
                    searchedPlace.y.toDouble(),
                    searchedPlace.x.toDouble()
                )
            )
                .animate(CameraAnimation.Easing)
                .finishCallback {
                    setMarkers(listOf(searchedPlace))
                    markers[searchedPlace]?.performClick()
                }

            naverMap.moveCamera(cameraUpdate)
        }
    }

    private fun setLayout() {
        binding.searchBar.setOnClickListener {
            val intent = Intent(activity, SearchActivity::class.java)
            startActivity(intent)
        }

        binding.filteringSoloBtn.setOnClickListener {
            filterMarkers("solo")
        }

        binding.filteringGroupBtn.setOnClickListener {
            filterMarkers("group")
        }

        binding.favBtn.setOnClickListener {
            filterFavs()
        }

        binding.refreshBtn.setOnClickListener {
            refreshMarkerList()
        }
    }

    private fun filterMarkers(type: String) {
        delMarkers()
        val y = naverMap.cameraPosition.target.latitude.toString()
        val x = naverMap.cameraPosition.target.longitude.toString()
        lifecycleScope.launch {
            val response = async { searchController.searchByXY(x, y) }.await()

            val nonFilteredPlaces = HashMap<String, Place>()
            response?.documents?.forEach {
                nonFilteredPlaces[it.id] = it
            }
            val filteredIds =
                withContext(Dispatchers.Default) {
                    getFilteredIds(
                        type,
                        nonFilteredPlaces.keys.toList()
                    )
                }?.mapIds
            if (filteredIds?.size == 0) {
                val msg = if (type == "solo") "혼카콩" else "모카콩"
                Utils.showToast(requireContext(), "현재 지도 반경 500m 내 ${msg} 카페가 없습니다")
            }
            filteredIds?.forEach {
                addMarker(nonFilteredPlaces[it])
            }
        }
    }

    private fun filterFavs() {
        delMarkers()
        val y = naverMap.cameraPosition.target.latitude.toString()
        val x = naverMap.cameraPosition.target.longitude.toString()
        lifecycleScope.launch {
            val response = async { searchController.searchByXY(x, y) }.await()
            val nonFilteredPlaces = HashMap<String, Place>()
            response?.documents?.forEach {
                nonFilteredPlaces[it.id] = it
            }
            val filteredIds =
                async { getFavFilteredIds(nonFilteredPlaces.keys.toList()) }.await()?.mapIds
            if (filteredIds?.size == 0)
                Utils.showToast(requireContext(), "현재 지도 반경 500m 내 즐겨찾기 결과가 없습니다")
            filteredIds?.forEach {
                addMarker(nonFilteredPlaces[it])
            }
        }
    }


    private suspend fun getFilteredIds(type: String, ids: List<String>): FilteringResponse? {
        val filteringApi = RetrofitClient.create(MapApi::class.java)
        val req = FilteringRequest(ids)
        Log.d("filtering", "필터링 보낸다 나 stt : $type, req : $req")
        val response = filteringApi.getFilteredCafes(studyType = type, filteringRequest = req)
        return if (response.isSuccessful) {
            Log.d("filtering", "filtering response 성공 : ${response.body()}")
            response.body()
        } else {
            null
        }
    }

    private suspend fun getFavFilteredIds(ids: List<String>): FilteringResponse? {
        val filteringApi = RetrofitClient.create(MapApi::class.java)
        val req = FilteringRequest(ids)
        Log.d("filtering", "필터링 보낸다 나 stt :  req : $req")
        val response = filteringApi.getFavCafes(filteringRequest = req)
        return if (response.isSuccessful) {
            Log.d("filtering", "fav filtering response 성공 : ${response.body()}")
            response.body()
        } else {
            null
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
            LatLng(37.5666102, 126.9783881),
            16.0
        )
        naverMap.locationSource = locationSource
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        setCurrentLocation()

        markerImg = OverlayImage.fromResource(R.drawable.custom_marker)

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
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { loc ->
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
        delMarkers()
        binding.refreshBtn.visibility = View.INVISIBLE
        val y = naverMap.cameraPosition.target.latitude.toString()
        val x = naverMap.cameraPosition.target.longitude.toString()
        lifecycleScope.launch {
            val response = searchController.searchByXY(x, y)
            if (response != null) {
                setMarkers(response.documents)
            }
        }
    }

    private fun addMarker(place: Place?) {
        place?.let {
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
            marker.map = naverMap
        }
    }


    private fun setMarkers(places: List<Place>) {
        places.forEach { place ->
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
            marker.map = naverMap
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
        if (markers.isEmpty()) return
        markers.forEach {
            it.value.map = null
        }
        markers.clear()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
