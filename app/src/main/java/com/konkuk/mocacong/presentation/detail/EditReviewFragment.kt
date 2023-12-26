package com.konkuk.mocacong.presentation.detail

import android.view.View
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.konkuk.mocacong.R
import com.konkuk.mocacong.databinding.FragmentEditReviewBinding
import com.konkuk.mocacong.presentation.base.BaseBottomSheet

class EditReviewFragment: BaseBottomSheet<FragmentEditReviewBinding>() {

    private val viewModel : CafeDetailViewModel by activityViewModels()
    override val TAG: String
        get() = "EditReview"
    override val layoutRes: Int
        get() = R.layout.fragment_edit_review

    override fun afterViewCreated() {
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
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
    }


}