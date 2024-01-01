package com.konkuk.mocacong.presentation.detail

import androidx.fragment.app.activityViewModels
import com.konkuk.mocacong.R
import com.konkuk.mocacong.databinding.FragmentCafeDetailBinding
import com.konkuk.mocacong.presentation.base.BaseFragment
import com.konkuk.mocacong.presentation.main.MainPage
import com.konkuk.mocacong.presentation.main.MainViewModel

class CafeDetailFragment : BaseFragment<FragmentCafeDetailBinding>() {
    override val TAG: String = "DetailFragment"
    override val layoutRes: Int = R.layout.fragment_cafe_detail
    private val mainViewModel: MainViewModel by activityViewModels()
    private val detailViewModel: CafeDetailViewModel by activityViewModels()

    override fun afterViewCreated() {
        binding.vm = detailViewModel
        detailViewModel.requestCafeDetailInfo()
        initLayout()
    }

    private fun initLayout() {
        //TODO: images preview
        binding.imagesLayout.setOnClickListener {
            detailViewModel.imagePage = 0
            detailViewModel.requestCafeImages()
            mainViewModel.goto(MainPage.IMAGES)
        }

        binding.commentInputLayout.root.setOnClickListener {
            val writeCommentFragment = WriteCommentFragment()
            writeCommentFragment.show(childFragmentManager, writeCommentFragment.tag)
        }

        binding.favBtn.setOnClickListener {
            detailViewModel.requestFavoritePost(!it.isSelected)
        }
        binding.editBtn.setOnClickListener {
            detailViewModel.requestMyReview()
            val editReviewFragment = EditReviewFragment()
            editReviewFragment.show(childFragmentManager, editReviewFragment.TAG)
        }

        binding.allCommentText.setOnClickListener {
            detailViewModel.commentPage = 0
            detailViewModel.requestCafeComments()

            mainViewModel.goto(MainPage.COMMENTS)
        }

    }


}