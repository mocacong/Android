package com.konkuk.mocacong.presentation.main.mypage

import androidx.fragment.app.activityViewModels
import com.konkuk.mocacong.R
import com.konkuk.mocacong.databinding.FragmentMyCafesBinding
import com.konkuk.mocacong.presentation.base.BaseFragment
import com.konkuk.mocacong.util.ApiState

class MyCafesFragment : BaseFragment<FragmentMyCafesBinding>() {
    override val TAG: String = "MyCafesFragment"
    override val layoutRes: Int = R.layout.fragment_my_cafes
    private val mypageViewModel: MypageViewModel by activityViewModels()

    private val favDescription = listOf("즐겨찾는 카페", "즐겨찾기 한 카페를 한 눈에 확인해보세요")
    private val reviewsDescription = listOf("나의 리뷰", "카페에 남긴 나의 리뷰를 확인해보세요")
    private val commentsDescription = listOf("나의 댓글", "내가 남긴 댓글을 카페 별로 모아보세요")

    override fun afterViewCreated() {
        setHeaderText()
        setObservers()
    }

    private fun setObservers() {
        mypageViewModel.apply {
            when (type) {
                MypageViewModel.ListType.COMMENTS -> {
                    commentsResponse.observeLiveData(
                        onSuccess = {
                            //TODO: Create comments item adapter
                            mCommentsResponse.value = ApiState.Loading()
                        }
                    )
                }
                MypageViewModel.ListType.STARS -> {
                    favResponse.observeLiveData(
                        onSuccess = {
                            //TODO: Create Fav Item Adapter
                            mFavResponse.value = ApiState.Loading()
                        }
                    )
                }
                MypageViewModel.ListType.REVIEWS -> {
                    reviewsResponse.observeLiveData(
                        onSuccess = {
                            //TODO: Create Reviews Item Adapter
                        }
                    )
                }
            }
        }
    }

    private fun setHeaderText() {
        when (mypageViewModel.type) {
            MypageViewModel.ListType.COMMENTS -> {
                binding.pageTitleText.text = commentsDescription[0]
                binding.descriptionText.text = commentsDescription[1]
            }
            MypageViewModel.ListType.STARS -> {
                binding.pageTitleText.text = favDescription[0]
                binding.descriptionText.text = favDescription[1]
            }
            MypageViewModel.ListType.REVIEWS -> {
                binding.pageTitleText.text = reviewsDescription[0]
                binding.descriptionText.text = reviewsDescription[1]
            }
        }
    }
}