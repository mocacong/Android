package com.konkuk.mocacong.presentation.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.konkuk.mocacong.R
import com.konkuk.mocacong.databinding.FragmentDetailBinding
import com.konkuk.mocacong.presentation.base.BaseFragment
import com.konkuk.mocacong.presentation.models.CafeDetailUiModel
import com.konkuk.mocacong.remote.models.response.CafeResponse
import java.lang.Integer.min

class DetailFragment : BaseFragment<FragmentDetailBinding>() {
    override val TAG: String = "DetailFragment"
    override val layoutRes: Int = R.layout.fragment_detail
    private val viewModel: CafeDetailViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun afterViewCreated() {
        binding.basic = viewModel.cafeBasicInfo
        viewModel.cafeId = viewModel.cafeBasicInfo.id
        initObservers()
        initLayout()
        viewModel.requestCafeDetailInfo()
    }

    private fun initLayout() {
        binding.commentInputBar.setOnClickListener {
            //TODO: 댓글 입력창
        }
        binding.favBtn.setOnClickListener {
            viewModel.requestFavoritePost(isRegister = !it.isSelected)
        }
        binding.editBtn.setOnClickListener {
            //TODO: 리뷰팝업
        }
    }

    private fun initObservers() {
        viewModel.cafeDetailInfoResponse.observeLiveData(
            onSuccess = applyDetailInfo
        )
        viewModel.favoriteResponse.observeLiveData(
            onSuccess = {
                binding.favBtn.isSelected = !binding.favBtn.isSelected
                val str = if (binding.favBtn.isSelected) "즐겨찾기에 등록되었습니다" else "즐겨찾기에서 해제되었습니다"
                showToast(str)
            }
        )
    }

    private val applyDetailInfo: (CafeResponse) -> Unit = {
        binding.apply {
            val uiModel = CafeDetailUiModel.responseToModel(it)
            favBtn.isSelected = it.favorite
            binding.detail = uiModel
            binding.ratingView.rating = it.score

            val comments = listOf(comment1, comment2, comment3)
            if (it.commentsCount > 0) noCommentText.visibility = View.GONE
            for (i in 0 until min(it.commentsCount, 3))
                comments[i].setComment(it.comments[i])
        }
    }


}