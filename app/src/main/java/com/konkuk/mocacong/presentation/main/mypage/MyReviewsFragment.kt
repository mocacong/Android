package com.konkuk.mocacong.presentation.main.mypage

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.konkuk.mocacong.R
import com.konkuk.mocacong.databinding.FragmentMyReviewsBinding
import com.konkuk.mocacong.presentation.base.BaseFragment
import com.konkuk.mocacong.remote.models.response.MyReviews

class MyReviewsFragment : BaseFragment<FragmentMyReviewsBinding>() {
    override val TAG: String
        get() = "Mypage"
    override val layoutRes: Int
        get() = R.layout.fragment_my_reviews

    val mypageViewModel: MypageViewModel by activityViewModels()
    override fun afterViewCreated() {
        binding.vm = mypageViewModel

        val adapter = MyReviewsAdapter(object : MyReviewsAdapter.OnItemClickListener {
            override fun onMoreClicked() {
                mypageViewModel.requestMyReviews()
            }

            override fun onItemClicked(myReviews: MyReviews) {
                //TODO: 지도 연결
            }

        })

        binding.recyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        binding.recyclerView.adapter = adapter

    }
}