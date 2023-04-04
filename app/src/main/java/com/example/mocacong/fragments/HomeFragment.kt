package com.example.mocacong.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mocacong.R
import com.example.mocacong.controllers.SearchController
import com.example.mocacong.data.response.Place
import com.example.mocacong.databinding.FragmentHomeBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.coroutines.launch


class HomeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private lateinit var searchController: SearchController
    private val markers = ArrayList<Marker>()

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        searchController = SearchController()

        getMapFragment()
        //setLocation()
        return binding.root
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

        naverMap.locationSource = locationSource

        val locationOverlay = naverMap.locationOverlay
        locationOverlay.isVisible = true

        naverMap.addOnCameraChangeListener { i, b ->
            binding.refreshBtn.visibility = View.VISIBLE
        }

        binding.refreshBtn.setOnClickListener {
            //현 지도에서 검색 클릭 시 카메라 시점 기반 500m 카페 검색
            //최대 15개
            delMarkers()
            binding.refreshBtn.visibility = View.INVISIBLE
            val y = naverMap.cameraPosition.target.latitude.toString()
            val x = naverMap.cameraPosition.target.longitude.toString()
            lifecycleScope.launch {
                val response = searchController.searchByXY(x, y)

                Log.d("MAP", "x:$x, y:$y")
                Log.d("MAP", response.toString())

                if (response != null) {
                    setMarkers(response.documents)
                }
            }

        }

    }

    private fun setMarkers(places: List<Place>) {
        places.forEach {
            val marker = Marker()
            markers.add(marker)
            marker.position = LatLng(it.y.toDouble(), it.x.toDouble())
            marker.captionText = it.place_name
            marker.captionMinZoom = 9.0
            marker.icon = OverlayImage.fromResource(R.drawable.custom_marker)

            marker.map = naverMap
        }
    }

    private fun delMarkers() {
        if (markers.isEmpty())
            return
        markers.forEach {
            it.map = null
        }

        markers.clear()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
