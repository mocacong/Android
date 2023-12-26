package com.konkuk.mocacong.presentation.detail

import android.view.View
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.konkuk.mocacong.R
import com.konkuk.mocacong.databinding.FragmentEditReviewBinding
import com.konkuk.mocacong.presentation.base.BaseBottomSheet
import com.konkuk.mocacong.remote.models.request.ReviewRequest

class EditReviewFragment : BaseBottomSheet<FragmentEditReviewBinding>() {

    private val viewModel: CafeDetailViewModel by activityViewModels()
    override val TAG: String
        get() = "EditReview"
    override val layoutRes: Int
        get() = R.layout.fragment_edit_review

    override fun afterViewCreated() {
        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from<View>(bottomSheet!!)
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.vm = viewModel

        binding.cancelBtn.setOnClickListener {
            dismiss()
        }

        getStudyType()

        binding.completeBtn.setOnClickListener {
            val rating = binding.ratingView.rating.toInt()
            if (rating == 0) {
                showToast("평점을 입력해주세요")
                return@setOnClickListener
            }
            if (studyType == null) {
                showToast("코딩 타입을 입력해주세요")
                return@setOnClickListener
            }
            if (binding.desk.getSelectedLabel() == null) {
                showToast("카페의 책상에 대해 평가를 남겨주세요")
                return@setOnClickListener
            }
            if (binding.power.getSelectedLabel() == null) {
                showToast("카페의 콘센트에 대해 평가를 남겨주세요")
                return@setOnClickListener
            }

            viewModel.saveMyReview(
                ReviewRequest(
                    myScore = rating,
                    myStudyType = studyType,
                    myWifi = binding.wifi.getSelectedLabel(),
                    myParking = binding.parking.getSelectedLabel(),
                    myToilet = binding.toilet.getSelectedLabel(),
                    myPower = binding.power.getSelectedLabel(),
                    mySound = binding.sound.getSelectedLabel(),
                    myDesk = binding.desk.getSelectedLabel()
                )
            )
        }

        viewModel.saveReviewResponse.observe(this) {
            it.byState(
                onSuccess = {
                    showToast("리뷰가 성공적으로 저장되었습니다.")
                    dismiss()
                },
                onFailure = {
                    showToast("리뷰 저장에 실패하였습니다. reason: ${it.message}")
                }
            )
        }
        viewModel.myReview.observe(this) {
            binding.ratingView.rating = it.myScore.toFloat()
        }

    }

    var studyType: String? = null
    var isSoloChecked = false
    var isGroupChecked = false
    private fun getStudyType() {
        binding.toggleButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (checkedId == binding.groupedSoloBtn.id) isSoloChecked = isChecked
            else if (checkedId == binding.groupedGroupBtn.id) isGroupChecked = isChecked

            studyType = when {
                isSoloChecked && isGroupChecked -> "both"
                isSoloChecked -> "solo"
                isGroupChecked -> "group"
                else -> null
            }
        }
    }


}