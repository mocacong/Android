package com.konkuk.mocacong.presentation.detail

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.konkuk.mocacong.R
import com.konkuk.mocacong.databinding.FragmentWriteCommentBinding
import com.konkuk.mocacong.presentation.base.BaseBottomSheet

class WriteCommentFragment : BaseBottomSheet<FragmentWriteCommentBinding>() {
    override val TAG: String
        get() = "WriteCommentFragment"
    override val layoutRes: Int
        get() = R.layout.fragment_write_comment

    private val detailViewModel: CafeDetailViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetFragmentStyle)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return dialog
    }

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


        binding.vm = detailViewModel
        binding.completeBtn.setOnClickListener {
            if (binding.contentLayout.error != null) showToast("댓글 형식을 확인해주세요")
            else detailViewModel.postComment(binding.contentLayout.editText?.text.toString())
        }
        binding.cancelBtn.setOnClickListener {
            dismiss()
        }

        binding.contentLayout.editText?.let {
            it.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    val content = it.text.toString()
                    if (content.length > 200)
                        binding.contentLayout.error = "댓글은 200자를 초과할 수 없습니다"
                    else if (content.isBlank())
                        binding.contentLayout.error = "댓글은 공백 만으로 작성할 수 없습니다"
                    else
                        binding.contentLayout.error = null
                }

                override fun afterTextChanged(p0: Editable?) {}
            })
        }

        detailViewModel.postCommentResponse.observe(this) {
            it.byState(
                onSuccess = {
                    showToast("댓글이 등록되었습니다")
                    detailViewModel.requestCafeDetailInfo()
                    detailViewModel.requestCafeComments()
                    dismiss()
                },
                onFailure = {
                    showToast("댓글 등록에 실패하였습니다. reason: ${it.message}")
                }
            )
        }
    }
}