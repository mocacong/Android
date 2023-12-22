package com.konkuk.mocacong.presentation.main.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.konkuk.mocacong.databinding.FragmentCafePreviewBinding
import com.konkuk.mocacong.presentation.detail.CafeDetailViewModel
import com.konkuk.mocacong.presentation.main.MainPage
import com.konkuk.mocacong.presentation.main.MainViewModel
import com.konkuk.mocacong.util.ApiState


class CafePreviewFragment : BottomSheetDialogFragment() {
    private val mapViewModel: MapViewModel by activityViewModels()
    private val detailViewModel: CafeDetailViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentCafePreviewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCafePreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initLayout()
    }

    private fun initLayout() {
        mapViewModel.cafePreviewResponse.observe(this) { state ->
            when (state) {
                is ApiState.Success -> {
                    state.data?.let {
                        mapViewModel.clickedMarker.value?.let { mapMarker ->
                            binding.cafeName.text = mapMarker.name
                            binding.addrText.text = mapMarker.roadAddress
                        }
                        if (it.reviewsCount == 0) return@let
                        when (it.studyType) {
                            "solo" -> {
                                binding.soloBtn.visibility = View.VISIBLE
                                binding.groupBtn.visibility = View.GONE
                            }
                            "group" -> {
                                binding.soloBtn.visibility = View.GONE
                                binding.groupBtn.visibility = View.VISIBLE
                            }
                            "both" -> {
                                binding.soloBtn.visibility = View.VISIBLE
                                binding.groupBtn.visibility = View.VISIBLE
                            }
                            else -> {
                                binding.soloBtn.visibility = View.GONE
                                binding.groupBtn.visibility = View.GONE
                            }
                        }
                        binding.apply {
                            val scoreText = String.format("X %.1f", it.score)
                            rating.text = scoreText
                            reviewCount.text = "${it.reviewsCount}개"
                        }
                    }
                }
                is ApiState.Error -> {
                    state.errorResponse.let { er ->

                    }
                }
                is ApiState.Loading -> {
                }
            }
        }

        binding.root.setOnClickListener {
            mapViewModel.clickedMarker.value?.let { mapMarker ->
                detailViewModel.cafeBasicInfo = mapMarker.getPlaceInfo()
                mainViewModel.goto(MainPage.DETAIL)
                dismiss()
            }
        }
    }


    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}