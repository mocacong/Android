package com.konkuk.mocacong.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.konkuk.mocacong.activities.CafeDetailActivity
import com.konkuk.mocacong.adapter.CafeCommentsAdapter
import com.konkuk.mocacong.data.objects.Utils
import com.konkuk.mocacong.data.objects.Utils.showKeyboard
import com.konkuk.mocacong.data.response.Comment
import com.konkuk.mocacong.data.util.ApiState
import com.konkuk.mocacong.data.util.TokenExceptionHandler
import com.konkuk.mocacong.databinding.FragmentCafeCommentsBinding
import com.konkuk.mocacong.viewmodels.CafeDetailViewModel
import kotlinx.coroutines.launch

class CafeCommentsFragment : BottomSheetDialogFragment(),
    CafeCommentsAdapter.OnCommentBtnClickedListener {
    private val TAG = "CafeComments"

    private var _binding: FragmentCafeCommentsBinding? = null
    private val binding get() = _binding!!
    private val cafeViewModel: CafeDetailViewModel by activityViewModels()

    private lateinit var cafeId: String

    private lateinit var adapter: CafeCommentsAdapter
    private val comments = mutableListOf<Comment>()
    private var currentPage = 0
    private var isEnd = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme).apply {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = false
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCafeCommentsBinding.inflate(inflater, container, false)

        cafeId = arguments?.getString("cafeId")!!
        setLayout()
        return binding.root
    }

    private fun setLayout() {
        setRecyclerLayout()
        binding.completeBtn.setOnClickListener {
            if (cafeViewModel.isCommentEditing)
                putComment()
            else postComment()
        }


        val isFocusToTextField = arguments?.getBoolean("isFocusToTextField", false)!!
        if (isFocusToTextField)
            binding.content.showKeyboard()
    }

    private fun putComment() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("댓글 수정")
            .setMessage("댓글을 정말 수정하시겠습니까?")
            .setPositiveButton("확인", null)
            .setNegativeButton("취소", null)
            .show()


        //레포단 작업 완료
        cafeViewModel.isCommentEditing = false
    }


    private fun refreshCommentsList() {
        currentPage = 0
        comments.clear()
        addComments(currentPage++)
    }


    private fun postComment() {
        val content = binding.content.text.toString()
        if (content.isBlank()) {
//            MessageDialog("공백일 수 없습니다").show(childFragmentManager, "MessageDialog")
            return
        }
        if (content.length > 200) {
//            MessageDialog("댓글은 최대 200자까지 가능합니다").show(childFragmentManager, "MessageDialog")
            return
        }

        lifecycleScope.launch {
            cafeViewModel.apply {
                postMyComment(cafeId, content).join()
                when (val apiState = commentPostFlow.value) {
                    is ApiState.Success -> {
                        binding.content.text = null
                        refreshCommentsList()
                        Utils.showToast(requireContext(), "코멘트가 등록되었습니다")
                    }
                    is ApiState.Error -> {
                        apiState.errorResponse?.let { er ->
                            TokenExceptionHandler.handleTokenException(requireContext(), er)
                        }
                    }
                    is ApiState.Loading -> {}
                }
            }
        }
    }

    private fun setRecyclerLayout() {
        adapter = CafeCommentsAdapter(comments)
        adapter.commentBtnClickedListener = this
        binding.commentsRecycler.let {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false
            )
            it.setOnScrollChangeListener { view, _, _, _, _ ->
                if (!view.canScrollVertically(1) && !isEnd)
                    addComments(currentPage++)
            }
        }
        addComments(currentPage++)
    }

    private fun addComments(page: Int) = lifecycleScope.launch {
        cafeViewModel.apply {
            requestCafeComments(cafeId, page).join()
            when (val apiState = commentsResponseFlow.value) {
                is ApiState.Success -> {
                    apiState.data?.let { commentsResponse ->
                        isEnd = commentsResponse.isEnd
                        comments.addAll(commentsResponse.comments)
                        adapter.notifyDataSetChanged()
                        commentsResponse.count?.let {
                            val str = "댓글 ($it)"
                            binding.commentsCountText.text = str
                        }
                    }
                }
                is ApiState.Error -> {
                    apiState.errorResponse?.let { er ->
                        TokenExceptionHandler.handleTokenException(requireContext(), er)
                        Log.e(TAG, er.message)
                    }
                    mCommentsResponseFlow.value = ApiState.Loading()
                }
                is ApiState.Loading -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as CafeDetailActivity).refreshDetailInfo()
        _binding = null
    }

    override fun onEditClicked(comment: Comment) {
        cafeViewModel.isCommentEditing = true
        binding.content.apply {
            setText(comment.content)
            showKeyboard()
        }
        //뷰모델에 편집/새로작성 구분하는 state 생성하기

    }

    override fun onDeleteClicked(comment: Comment) {
        TODO("Not yet implemented")
    }


}