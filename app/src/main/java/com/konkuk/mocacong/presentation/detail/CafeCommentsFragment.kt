package com.konkuk.mocacong.presentation.detail

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.konkuk.mocacong.R
import com.konkuk.mocacong.data.entities.Comment
import com.konkuk.mocacong.databinding.FragmentCafeCommentsBinding
import com.konkuk.mocacong.presentation.base.BaseBottomSheet

class CafeCommentsFragment : BaseBottomSheet<FragmentCafeCommentsBinding>() {
    override val TAG: String = "CafeCommentsFragment"
    override val layoutRes: Int = R.layout.fragment_cafe_comments
    private val detailViewModel: CafeDetailViewModel by activityViewModels()

    override fun afterViewCreated() {
        binding.vm = detailViewModel
        val clickListener = object : CafeCommentsAdapter.ButtonClickListener{
            override fun onMenuClicked(comment: Comment) {
                //메뉴
            }

            override fun onMoreClicked(isEnd: Boolean) {
                //더보기
                detailViewModel.requestCafeComments()
            }

        }

        val adapter = CafeCommentsAdapter(clickListener)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter

        binding.commentInputLayout.root.setOnClickListener {
            val writeCommentFragment = WriteCommentFragment()
            writeCommentFragment.show(childFragmentManager, writeCommentFragment.tag)
        }
    }

}