package com.konkuk.mocacong.presentation.main.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.konkuk.mocacong.R
import com.konkuk.mocacong.data.entities.MapMarker
import com.konkuk.mocacong.databinding.FragmentHomeBinding
import com.konkuk.mocacong.presentation.base.BaseFragment
import com.konkuk.mocacong.presentation.login.WebViewActivity
import com.konkuk.mocacong.presentation.main.MainPage
import com.konkuk.mocacong.presentation.main.MainViewModel
import com.konkuk.mocacong.presentation.main.mypage.MypageViewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import gun0912.tedimagepicker.builder.TedImagePicker
import gun0912.tedimagepicker.builder.type.MediaType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.lang.Integer.min

class HomeFragment : BaseFragment<FragmentHomeBinding>(), OnMapReadyCallback {
    override val TAG: String = "HomeFragment"
    override val layoutRes: Int = R.layout.fragment_home
    private val mapViewModel: MapViewModel by activityViewModels()
    private val mypageViewModel: MypageViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var locationSource: FusedLocationSource
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var naverMap: NaverMap

    private val markerFav: OverlayImage = OverlayImage.fromResource(R.drawable.map_ic_fav)
    private val markerNone: OverlayImage = OverlayImage.fromResource(R.drawable.map_ic_none)
    private val markerMocacong: OverlayImage = OverlayImage.fromResource(R.drawable.map_ic_mocacong)


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
        binding.searchBar.setOnClickListener {
            mapViewModel.currentLocation = naverMap.cameraPosition
            mainViewModel.goto(MainPage.SEARCH)
        }

        binding.menuIcon.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.END)
        }
        setDrawer()
    }

    private fun setDrawer() {

        setNavHeader()

        binding.navigationMenu.setNavigationItemSelectedListener { menuItem ->
            binding.drawerLayout.closeDrawers()
            when (menuItem.itemId) {
                R.id.myReviews -> {
                    mypageViewModel.reviewPage = 0
                    mypageViewModel.requestMyReviews()
                    mainViewModel.goto(MainPage.MyReview)
                }
                R.id.myFavs -> {
                    mypageViewModel.favPage = 0
                    mypageViewModel.requestMyFavs()
                    mainViewModel.goto(MainPage.MyFav)
                }

                R.id.myComments -> {
                    mypageViewModel.commentPage = 0
                    mypageViewModel.requestMyComments()
                    mainViewModel.goto(MainPage.MyComment)
                }
                R.id.customerCenter -> {
                    val intent = Intent(requireContext(), WebViewActivity::class.java)
                    intent.putExtra("urlString", resources.getString(R.string.qnaUrl))
                    startActivity(intent)
                }
                R.id.appInfo -> {
                    val intent = Intent(requireContext(), WebViewActivity::class.java)
                    intent.putExtra("urlString", resources.getString(R.string.termsUrl))
                    startActivity(intent)
                }
                R.id.qna -> {
                    val intent = Intent(requireContext(), WebViewActivity::class.java)
                    intent.putExtra("urlString", resources.getString(R.string.qnaUrl))
                    startActivity(intent)
                }
            }

            return@setNavigationItemSelectedListener false
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

        mypageViewModel.myProfile.observe(this) {
            Log.d("Profile", "ProfileResposne SUCCEED")

            val header = binding.navigationMenu.getHeaderView(0)
            val imageView = header.findViewById<ImageButton>(R.id.nav_header_profileImg)
            if (it.imgUrl.isNullOrBlank()) imageView.setImageResource(R.drawable.img_no_profile)
            else Glide.with(imageView.context).load(it.imgUrl).into(imageView)
            val nickname = header.findViewById<TextView>(R.id.nav_header_nickname)
            nickname.text = it.nickname

            imageView.invalidate()
            imageView.requestLayout()

            header.invalidate()
            header.requestLayout()
        }
    }

    private fun setNavHeader() {
        val header = binding.navigationMenu.getHeaderView(0)
        val profileClickedListener = {
            TedImagePicker.with(requireContext())
                .mediaType(MediaType.IMAGE)
                .start { uri ->

                    val file = File(absolutePath(uri))
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    val part = MultipartBody.Part.createFormData("file", file.name, requestFile)

                    mypageViewModel.putMyProfileImg(part)
                }
        }

        val pfImg = header.findViewById<ImageButton>(R.id.nav_header_profileImg)
        pfImg.setOnClickListener {
            profileClickedListener()
        }
        pfImg.clipToOutline = true

        header.findViewById<ImageButton>(R.id.nav_header_editbtn).setOnClickListener {
            profileClickedListener()
        }
    }

    private val createPreview: (Unit) -> Unit = {
        mapViewModel.clickedMarker.value?.let {
            mapViewModel.requestPreviewInfo(it.mapId)
            val previewFragment = CafePreviewFragment()
            previewFragment.show(childFragmentManager, previewFragment.tag)
        }
    }

    private fun setMarkersVisible(mapMarkers: List<MapMarker>) = lifecycleScope.launch {
        mapMarkers.forEach {
            when (it.type) {
                MapMarker.Type.NONE -> {
                    it.marker.icon = markerNone
                }
                else -> {
                    //GROUP, SOLO, BOTH
                    it.marker.icon = markerMocacong
                }
            }
            if (it.isFavorite) it.marker.icon = markerFav
            it.marker.map = naverMap
            logging("[Filtered Marker] : $it")
        }
    }


    override fun onMapReady(naverMap: NaverMap) {
        //지도 객체 세팅
        Log.d("MAP", "객체 초기화 완료")
        this.naverMap = naverMap
        setMapSettings(naverMap)
        setCurrentLocation()
        collectLocation()

        val projection = naverMap.projection

        naverMap.addOnCameraIdleListener {
            val y = naverMap.cameraPosition.target.latitude.toString()
            val x = naverMap.cameraPosition.target.longitude.toString()
            val radius = min(projection.metersPerDp.toInt() * 200, 20000)
            logging("카메라 멈춤 x: $x, y: $y")
            mapViewModel.updateMarkers(x = x, y = y, radius)

            Log.d(TAG, "현재 축적: 1dp당 ${projection.metersPerDp}m")
        }

        naverMap.setOnMapClickListener { _, _ ->
            mapViewModel.clickedMarker.value?.let {
                Log.d(TAG, "mapclickListener clicked: ${it.name}")
                mapViewModel.requestFiltering(listOf(it.mapId))
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


    private fun setCurrentLocation() {
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
    }


    private fun collectLocation() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.locationFlow.collect { place ->
                    if (place != null) {
                        Log.d(TAG, "Place Changed COLLECTED: $place")
                        if (place.id.isBlank()) return@collect

                        val cameraUpdate = CameraUpdate.scrollAndZoomTo(
                            LatLng(place.y.toDouble(), place.x.toDouble()),
                            18.0
                        )
                        naverMap.moveCamera(cameraUpdate)

                        delay(500)
                        mapViewModel.mapMarkers[place.id]?.marker?.performClick()
                        mainViewModel.consumeLocation()
                    } else {

                    }
                }
            }
        }
    }

    private fun absolutePath(uri: Uri?): String {
        val proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        val c: Cursor? = requireActivity().contentResolver.query(uri!!, proj, null, null, null)
        val index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()

        val result = c?.getString(index!!)
        c?.close()

        return result!!
    }
}
