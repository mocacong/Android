package com.konkuk.mocacong.presentation.main.map

import androidx.fragment.app.activityViewModels
import com.konkuk.mocacong.R
import com.konkuk.mocacong.data.entities.BasicPlaceInfo
import com.konkuk.mocacong.databinding.FragmentCafePreviewBinding
import com.konkuk.mocacong.presentation.base.BaseBottomSheet
import com.konkuk.mocacong.presentation.detail.CafeDetailViewModel
import com.konkuk.mocacong.presentation.main.MainPage
import com.konkuk.mocacong.presentation.main.MainViewModel
import com.konkuk.mocacong.presentation.models.CafePreviewUiModel

class CafePreviewFragment : BaseBottomSheet<FragmentCafePreviewBinding>() {
    private val mapViewModel: MapViewModel by activityViewModels()
    private val detailViewModel: CafeDetailViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var placeInfo: BasicPlaceInfo
    override val TAG: String = "CafePreview"
    override val layoutRes: Int = R.layout.fragment_cafe_preview

    override fun afterViewCreated() {
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

}