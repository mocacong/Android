package com.konkuk.mocacong.presentation.detail.comment

import android.view.MenuItem
import android.view.View
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.konkuk.mocacong.R
import com.konkuk.mocacong.data.entities.Comment
import com.konkuk.mocacong.databinding.FragmentCafeCommentsBinding
import com.konkuk.mocacong.presentation.base.BaseBottomSheet
import com.konkuk.mocacong.presentation.detail.CafeDetailViewModel
import com.konkuk.mocacong.presentation.main.mypage.MypageViewModel

class CafeCommentsFragment : BaseBottomSheet<FragmentCafeCommentsBinding>() {
    override val TAG: String = "CafeCommentsFragment"
    override val layoutRes: Int = R.layout.fragment_cafe_comments
    private val detailViewModel: CafeDetailViewModel by activityViewModels()
    private val mypageViewModel: MypageViewModel by activityViewModels()

    override fun afterViewCreated() {
        binding.vm = detailViewModel
        binding.mypageVm = mypageViewModel
        val clickListener = object : CafeCommentsAdapter.ButtonClickListener {
            override fun onMenuClicked(comment: Comment, menu: View) {
                //메뉴
                showMenu(menu, R.menu.comment_mine, comment)
            }

            override fun onMoreClicked(isEnd: Boolean) {
                //더보기
                detailViewModel.requestCafeComments()
            }

        }

        val adapter = CafeCommentsAdapter(clickListener)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter

        binding.commentInputLayout.root.setOnClickListener {
            val writeCommentFragment = WriteCommentFragment()
            writeCommentFragment.show(childFragmentManager, writeCommentFragment.tag)
        }

    }

    private fun showMenu(v: View, @MenuRes menuRes: Int, comment: Comment) {
        val popup = PopupMenu(requireContext(), v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            // Respond to menu item click.
            //삭제하기
            makeDialog(
                msg = "댓글을 정말 삭제하시겠습니까?",
                action = { detailViewModel.deleteComment(comment.id) })
            true
        }
        popup.setOnDismissListener {

        }
        // Show the popup menu.
        popup.show()
    }

    private fun makeDialog(msg: String, action: () -> Unit) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(msg)
            .setNeutralButton("취소") { dialog, which ->
                // Respond to neutral button press
                dismiss()
            }
            .setPositiveButton("확인") { dialog, which ->
                // Respond to positive button press
                action()
            }
            .show()
    }

}