package com.konkuk.mocacong.presentaion.main.map

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.konkuk.mocacong.R
import com.konkuk.mocacong.presentaion.detail.CafeDetailActivity
import com.konkuk.mocacong.objects.RetrofitClient
import com.konkuk.mocacong.objects.Utils.bundleSerializable
import com.konkuk.mocacong.remote.models.request.CafeDetailRequest
import com.konkuk.mocacong.data.response.CafePreviewResponse
import com.konkuk.mocacong.data.response.Place
import com.konkuk.mocacong.databinding.FragmentCafePreviewBinding
import com.konkuk.mocacong.remote.apis.CafeDetailAPI
import com.konkuk.mocacong.remote.apis.MapApi
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CafePreviewFragment : BottomSheetDialogFragment() {
    private lateinit var cafe: Place
    private var info: CafePreviewResponse? = null
    private var _binding: FragmentCafePreviewBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            cafe = it.bundleSerializable("cafe", Place::class.java)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCafePreviewBinding.inflate(inflater, container, false)
        lifecycleScope.launch {
            withContext(Dispatchers.Default) {
                postCafe()
            }

        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        setLayout()
    }

    private fun setLayout() {
        lifecycleScope.launch {
            info = withContext(Dispatchers.IO) {
                getPreviewInfo(cafe.id)
            }
            info?.let {
                binding.apply {
                    Log.d("qaTest", "isFavorite = ${it.favorite}")
                    if (it.favorite) favIcon.setImageResource(R.drawable.preview_icon_fill_heart)
                    else favIcon.setImageResource(R.drawable.preview_icon_empty_heart)
                    reviewCountText.text = it.reviewsCount.toString()
                    val str = " x ${String.format("%.1f", it.score)}"
                    scoreText.text = str
                    studyTypeText.text =
                        when (it.studyType) {
                            "group" -> {
                                studyTypeIcon.setImageResource(R.drawable.preview_icon_group)
                                "group\ncoding"
                            }
                            "solo" -> {
                                studyTypeIcon.setImageResource(R.drawable.preview_icon_solo)
                                "solo\ncoding"
                            }
                            else -> {
                                studyTypeIcon.setImageResource(R.drawable.preview_icon_both)
                                "group & solo\ncoding"
                            }
                        }
                }
            }
        }

        binding.apply {
            cafeNameText.text = cafe.place_name
            addressText.text = cafe.road_address_name.let {
                if (it == "") "주소 정보가 없습니다"
                else it
            }
            callText.text = cafe.phone.let {
                if (it == "") "전화번호 정보가 없습니다"
                else it
            }

        }

        binding.root.setOnClickListener {
            gotoDetailActivity(cafe)
        }
    }

    private fun gotoDetailActivity(cafe: Place) {
        val intent = Intent(activity, CafeDetailActivity::class.java)
        intent.putExtra("cafe", cafe)
        (requireParentFragment() as HomeFragment).cafeDetailLauncher.launch(intent)
        this.dismiss()
    }

    override fun onDestroy() {
        (parentFragment as HomeFragment).revertMarker()
        _binding = null
        super.onDestroy()
    }

    private suspend fun getPreviewInfo(id: String): CafePreviewResponse? {
        val filteringApi = RetrofitClient.create(MapApi::class.java)
        Log.d("preview", "id: $id")
        val response = filteringApi.getPreview(cafeId = id)
        if (response.isSuccessful) return response.body()!!
        else {
            return null
        }
    }

    private suspend fun postCafe() {
        val api = RetrofitClient.create(CafeDetailAPI::class.java)
        val postResponse = api.postCafe(
            com.konkuk.mocacong.remote.models.request.CafeDetailRequest(
                cafe.id,
                cafe.place_name
            )
        )
        if (postResponse.isSuccessful) return
    }

    companion object {
        @JvmStatic
        fun newInstance(cafe: Place) =
            CafePreviewFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("cafe", cafe)
                }
            }
    }
}