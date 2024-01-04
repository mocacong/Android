package com.konkuk.mocacong.presentation.main.mypage

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.konkuk.mocacong.R
import com.konkuk.mocacong.databinding.FragmentMyCommentsBinding
import com.konkuk.mocacong.presentation.base.BaseFragment
import com.konkuk.mocacong.remote.models.response.MyComments
import dagger.hilt.android.AndroidEntryPoint


class MyCommentsFragment : BaseFragment<FragmentMyCommentsBinding>() {
    override val TAG: String
        get() = "Mypage"
    override val layoutRes: Int
        get() = R.layout.fragment_my_comments

    val mypageViewModel: MypageViewModel by activityViewModels()

    override fun afterViewCreated() {
        binding.vm = mypageViewModel

        val adapter = MyCommentsAdapter(object : MyCommentsAdapter.OnItemClickListener {
            override fun onMoreClicked() {
                mypageViewModel.requestMyComments()
            }

            override fun onItemClicked(myCafe: MyComments) {
                if(myCafe.roadAddress!=null) {
                    mypageViewModel.requestSearchAddress(myCafe.roadAddress, myCafe.mapId)
                }else{
                    mypageViewModel.requestSearchAddress(myCafe.name, myCafe.mapId)
                }
            }
        })

        binding.recyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL, false
        )
        binding.recyclerView.adapter = adapter

    }
}