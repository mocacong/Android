package com.konkuk.mocacong.presentation.main.mypage

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.konkuk.mocacong.R
import com.konkuk.mocacong.databinding.FragmentMyFavsBinding
import com.konkuk.mocacong.presentation.base.BaseFragment
import com.konkuk.mocacong.remote.models.response.MyFavorites

class MyFavsFragment : BaseFragment<FragmentMyFavsBinding>() {
    override val TAG: String
        get() = "Mypage"
    override val layoutRes: Int
        get() = R.layout.fragment_my_favs

    val mypageViewModel: MypageViewModel by activityViewModels()
    override fun afterViewCreated() {
        binding.vm = mypageViewModel

        val adapter = MyFavsAdapter(object : MyFavsAdapter.OnItemClickListener {
            override fun onMoreClicked() {
                mypageViewModel.requestMyFavs()
            }

            override fun onItemClicked(myCafe: MyFavorites) {
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