package com.konkuk.mocacong.presentation.detail

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
import com.konkuk.mocacong.databinding.FragmentCafeCommentsBinding
import com.konkuk.mocacong.objects.Utils
import com.konkuk.mocacong.objects.Utils.showKeyboard
import com.konkuk.mocacong.remote.models.response.Comment
import com.konkuk.mocacong.util.ApiState
import com.konkuk.mocacong.util.TokenExceptionHandler
import kotlinx.coroutines.launch

class CafeCommentsFragment : BottomSheetDialogFragment(){
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
                cafeViewModel
            else postComment()
        }

        val isFocusToTextField = arguments?.getBoolean("isFocusToTextField", false)!!
        if (isFocusToTextField)
            binding.content.showKeyboard()
    }


    private fun refreshCommentsList() {
        currentPage = 0
        comments.clear()
        addComments(currentPage++)
    }

    private fun showAlertDialog(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setPositiveButton("확인", null)
            .setMessage(message)
            .setTitle("Warning")
            .show()
    }


    private fun postComment() {
        val content = binding.content.text.toString()
        if (content.isBlank()) {
            showAlertDialog("댓글은 공백일 수 없습니다")
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
                            val str = "댓글 ($commentsResponse)"
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

    private fun putComment(comment: Comment) {
        binding.content.setText(comment.content)
    }

}