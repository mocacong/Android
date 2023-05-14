package com.example.mocacong.fragments

import android.content.Intent
import android.graphics.Color
import android.media.MediaDrm.PlaybackComponent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mocacong.R
import com.example.mocacong.activities.CafeDetailActivity
import com.example.mocacong.activities.SearchActivity
import com.example.mocacong.controllers.SearchController
import com.example.mocacong.data.response.Place
import com.example.mocacong.databinding.FragmentHomeBinding
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

    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private lateinit var searchController: SearchController
    private val markers = ArrayList<Marker>()
    private lateinit var markerImg: OverlayImage
    var clickedMarker: Marker? = null

    private val LOCATION_PERMISSION_REQUEST_CODE = 1000


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        searchController = SearchController()

        getMapFragment()
        setLayout()


        return binding.root
    }


    private fun <T : Serializable> Intent.intentSerializable(key: String, clazz: Class<T>): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.getSerializableExtra(key, clazz)
        } else {
            this.getSerializableExtra(key) as T?
        }
    }

    private fun <T:Serializable> Bundle.argSerializable(key:String, clazz: Class<T>) : T?{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.getSerializable(key, clazz)
        } else {
            this.getSerializable(key) as T?
        }
    }

    private fun gotoSearchedPlace() {
        val searchedPlace = arguments?.argSerializable("searchedPlace", Place::class.java)
        if(searchedPlace!=null){
            Log.d("search","searched 받았음 : ${searchedPlace}")

            val cameraUpdate = CameraUpdate.scrollTo(LatLng(searchedPlace.y.toDouble(),searchedPlace.x.toDouble()))
                .animate(CameraAnimation.Easing)
                .finishCallback {
                    setMarkers(listOf(searchedPlace))
                    markers[0].performClick()
                }

            naverMap.moveCamera(cameraUpdate)

        }
    }

    private fun setLayout() {
        binding.searchBar.setOnClickListener {
            val intent = Intent(activity, SearchActivity::class.java)
            startActivity(intent)
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

        naverMap.locationSource = locationSource

        val locationOverlay = naverMap.locationOverlay
        markerImg = OverlayImage.fromResource(R.drawable.custom_marker)

        naverMap.addOnCameraChangeListener { _, _ ->
            binding.refreshBtn.visibility = View.VISIBLE
        }

        naverMap.setOnMapClickListener { _, _ ->
            revertMarker()
        }

        binding.refreshBtn.setOnClickListener {
            //현 지도에서 검색 클릭 시 카메라 시점 기반 500m 카페 검색
            //최대 15개
            refreshMarkerList()
        }

        gotoSearchedPlace()

    }

    private fun revertMarker() {
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
            var response = searchController.searchByXY(x, y)

            if (response != null) {
                setMarkers(response.documents)
            }
        }
    }

    private fun setMarkers(places: List<Place>) {
        places.forEach { place ->
            val marker = Marker()
            markers.add(marker)
            marker.tag = 0
            marker.position = LatLng(place.y.toDouble(), place.x.toDouble())
            marker.captionText = place.place_name
            marker.captionMinZoom = 9.0
            marker.icon = markerImg
            marker.isHideCollidedSymbols = true


            marker.setOnClickListener {
                if (marker.tag == 0) {
                    markerFirstClicked(marker)
                    //마커 한 번 클릭
                } else {
                    //두 번 클릭
                    revertMarker()
                    gotoDetailActivity(place)
                }
                true
            }

            marker.map = naverMap
        }


    }

    private fun markerFirstClicked(marker: Marker) {
        revertMarker()
        clickedMarker = marker
        marker.captionColor = Color.GREEN
        marker.icon = Marker.DEFAULT_ICON
        marker.tag = 1
    }

    private fun gotoDetailActivity(cafe: Place) {
        val intent = Intent(activity, CafeDetailActivity::class.java)
        intent.putExtra("cafe", cafe)
        startActivity(intent)
    }


    private fun delMarkers() {
        if (markers.isEmpty()) return
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
