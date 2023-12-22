package com.konkuk.mocacong.presentation.detail

import androidx.fragment.app.activityViewModels
import com.konkuk.mocacong.R
import com.konkuk.mocacong.databinding.FragmentDetailBinding
import com.konkuk.mocacong.presentation.base.BaseFragment

class DetailFragment : BaseFragment<FragmentDetailBinding>() {
    override val TAG: String = "DetailFragment"
    override val layoutRes: Int = R.layout.fragment_detail
    private val viewModel: CafeDetailViewModel by activityViewModels()

    override fun afterViewCreated() {
        binding.vm = viewModel
        initObservers()
        initLayout()
    }

    private fun initLayout() {
        binding.commentInputBar.setOnClickListener {
            //TODO: 댓글 입력창
        }
        binding.favBtn.setOnClickListener {
            viewModel.requestFavoritePost(!it.isSelected)
        }
        binding.editBtn.setOnClickListener {
            //TODO: 리뷰팝업
        }
    }

    private fun initObservers() {

    }


}