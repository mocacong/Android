package com.konkuk.mocacong.presentation.main.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.konkuk.mocacong.R
import com.konkuk.mocacong.data.entities.MapMarker
import com.konkuk.mocacong.databinding.FragmentHomeBinding
import com.konkuk.mocacong.presentation.base.BaseFragment
import com.konkuk.mocacong.presentation.main.mypage.MypageViewModel
import com.konkuk.mocacong.util.Extensions.Companion.safeNavigate
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : BaseFragment<FragmentHomeBinding>(), OnMapReadyCallback {
    override val TAG: String = "HomeFragment"
    override val layoutRes: Int = R.layout.fragment_home
    private val mapViewModel: MapViewModel by activityViewModels()
    private val mypageViewModel: MypageViewModel by activityViewModels()

    private lateinit var locationSource: FusedLocationSource
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var naverMap: NaverMap

    private val markerFav: OverlayImage = OverlayImage.fromResource(R.drawable.map_ic_fav)
    private val markerNone: OverlayImage = OverlayImage.fromResource(R.drawable.map_ic_none)
    private val markerMocacong: OverlayImage = OverlayImage.fromResource(R.drawable.map_ic_mocacong)

    override fun onResume() {
        super.onResume()
        mapViewModel.removeMarkers(mapViewModel.mapMarkers.keys.toList())
    }

    companion object {
        private val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getMapFragment()
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

    }

    override fun afterViewCreated() {
        Log.d(TAG, "afterViewCreated")
        setBackBtn()
        binding.searchBar.setOnClickListener {

        }

        binding.menuIcon.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.END)
        }
        setDrawer()
    }

    private fun setDrawer() {
        binding.navigationMenu.setNavigationItemSelectedListener {
            binding.drawerLayout.closeDrawers()
            mypageViewModel.type = enumValueOf(it.itemId.toString())
            findNavController().safeNavigate(HomeFragmentDirections.actionHomeFragmentToMyCafesFragment())
            return@setNavigationItemSelectedListener true
        }
    }

    private fun setObservers() {
        mapViewModel.newPlaces.observe(this) { ids ->
            logging("[New Place Changed] : $ids")
            mapViewModel.requestFiltering(ids)
        }

        mapViewModel.filteredPlaces.observe(this) { mapMarkers ->
            setMarkersVisible(mapMarkers)
        }

        mapViewModel.clickedMarker.observe(this) { mapMarker ->
            mapMarker?.let { mapViewModel.postCafe(it) }
        }

        mapViewModel.postCafeResponse.observeLiveData(
            onSuccess = createPreview,
            onFailure = {
                if (it.code == 2009) {
                    createPreview(Unit)
                }
            }
        )
    }

    private val createPreview: (Unit) -> Unit = {
        mapViewModel.clickedMarker.value?.let {
            mapViewModel.requestPreviewInfo(it.mapId)
            findNavController().safeNavigate(HomeFragmentDirections.actionHomeFragmentToCafePreviewFragment())
        }
    }

    private fun setMarkersVisible(mapMarkers: List<MapMarker>) = lifecycleScope.launch {
        mapMarkers.forEach {
            when (it.type) {
                MapMarker.Type.FAV -> {
                    it.marker.icon = markerFav
                }
                MapMarker.Type.NONE -> {
                    it.marker.icon = markerNone
                }
                else -> {
                    //GROUP, SOLO, BOTH
                    it.marker.icon = markerMocacong
                }
            }
            it.marker.map = naverMap
            logging("[Filtered Marker] : $it")
        }
    }


    override fun onMapReady(naverMap: NaverMap) {
        //지도 객체 세팅
        Log.d("MAP", "객체 초기화 완료")
        this.naverMap = naverMap
        setMapSettings(naverMap)
        setCurrentLocation(mapViewModel.lastCameraLocation)
        val projection = naverMap.projection

        naverMap.addOnCameraIdleListener {
            val y = naverMap.cameraPosition.target.latitude.toString()
            val x = naverMap.cameraPosition.target.longitude.toString()
            val radius = projection.metersPerDp.toInt() * 150
            logging("카메라 멈춤 x: $x, y: $y")
            mapViewModel.updateMarkers(x = x, y = y, radius)
            Log.d(TAG, "현재 축적: 1dp당 ${projection.metersPerDp}m")
        }

        naverMap.setOnMapClickListener { _, _ ->
            mapViewModel.clickedMarker.value?.let {
                setMarkersVisible(listOf(it))
            }
        }

        setObservers()

    }

    private fun setMapSettings(naverMap: NaverMap) {
        //현위치 버튼 활성 및 줌 버튼 제거
        naverMap.uiSettings.apply {
            isZoomControlEnabled = false
            isLocationButtonEnabled = true
        }
        naverMap.locationOverlay.isVisible = true
        naverMap.locationSource = locationSource
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
    }


    //현위치 권한 요청
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


    private fun setCurrentLocation(lastCameraLocation: LatLng?) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(PERMISSIONS, LOCATION_PERMISSION_REQUEST_CODE)
            showToast("위치 권한 설정이 필요합니다")
            return
        }
        if (lastCameraLocation == null) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { loc ->
                if (loc != null) {
                    val cameraUpdate = CameraUpdate.scrollTo(LatLng(loc.latitude, loc.longitude))
                    naverMap.moveCamera(cameraUpdate)
                } else {
                    showToast("현위치 정보를 가져올 수 없습니다")
                }
            }.addOnFailureListener { e ->
                Log.d("TAG", "위치정보 못가져옴 ${e.message}")
            }
        } else {
            val cameraUpdate = CameraUpdate.scrollTo(lastCameraLocation)
            naverMap.moveCamera(cameraUpdate)
        }
    }


    private var backButtonPressedOnce = false
    private fun setBackBtn() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backButtonPressedOnce) requireActivity().finish()
                else {
                    backButtonPressedOnce = true
                    showToast("한 번 더 누르면 종료됩니다")
                    lifecycleScope.launch {
                        delay(2000)
                        backButtonPressedOnce = false
                    }
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

}
