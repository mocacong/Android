package com.konkuk.mocacong.presentation.detail

import androidx.fragment.app.activityViewModels
import com.konkuk.mocacong.R
import com.konkuk.mocacong.databinding.FragmentCafeDetailBinding
import com.konkuk.mocacong.presentation.base.BaseFragment

class CafeDetailFragment : BaseFragment<FragmentCafeDetailBinding>() {
    override val TAG: String = "DetailFragment"
    override val layoutRes: Int = R.layout.fragment_cafe_detail
    private val viewModel: CafeDetailViewModel by activityViewModels()

    override fun afterViewCreated() {
        binding.vm = viewModel
        initObservers()
        initLayout()
    }

    private fun initLayout() {
        binding.commentInputLayout.root.setOnClickListener {
            //TODO: 댓글 입력창
        }
        binding.favBtn.setOnClickListener {
            viewModel.requestFavoritePost(!it.isSelected)
        }
        binding.editBtn.setOnClickListener {
            viewModel.requestMyReview()
            val editReviewFragment = EditReviewFragment()
            editReviewFragment.show(childFragmentManager,editReviewFragment.TAG)
        }
    }

    private fun initObservers() {

    }


}