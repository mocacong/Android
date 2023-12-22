package com.konkuk.mocacong.presentation.main.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.konkuk.mocacong.data.entities.BasicPlaceInfo
import com.konkuk.mocacong.databinding.FragmentCafePreviewBinding
import com.konkuk.mocacong.presentation.detail.CafeDetailViewModel
import com.konkuk.mocacong.presentation.main.MainPage
import com.konkuk.mocacong.presentation.main.MainViewModel
import com.konkuk.mocacong.presentation.models.CafePreviewUiModel


class CafePreviewFragment : BottomSheetDialogFragment() {
    private val mapViewModel: MapViewModel by activityViewModels()
    private val detailViewModel: CafeDetailViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentCafePreviewBinding? = null
    private val binding get() = _binding!!

    private lateinit var placeInfo: BasicPlaceInfo

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
        mapViewModel.clickedMarker.value?.let { mapMarker ->
            placeInfo = mapMarker.getPlaceInfo()
        }
        binding.place = placeInfo
        mapViewModel.cafePreviewResponse.observe(this) { state ->
            state.byState(
                onSuccess = {
                    binding.preview = CafePreviewUiModel.responseToUIModel(it)
                }
            )
        }

        binding.root.setOnClickListener {
            detailViewModel.setBasicInfo(placeInfo)
            detailViewModel.cafeId = placeInfo.id
            detailViewModel.requestCafeDetailInfo()
            mainViewModel.goto(MainPage.DETAIL)
            dismiss()
        }
    }


    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}