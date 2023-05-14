package com.example.mocacong.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mocacong.data.objects.RetrofitClient
import com.example.mocacong.data.response.ProfileResponse
import com.example.mocacong.databinding.FragmentMypageBinding
import com.example.mocacong.network.MyPageAPI
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class MypageFragment : Fragment() {

    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)

        setTablayout()
        setInfoLayout()

        return binding.root
    }

    private fun setInfoLayout() {
        lifecycleScope.launch {
            val response = getProfile()
            response?.let {
                binding.nickNameText.text = it.nickname

            }
        }
    }

    suspend fun getProfile() : ProfileResponse? {
        val api = RetrofitClient.create(MyPageAPI::class.java)
        val response = api.getMyReview()
        return if(response.isSuccessful){
            Log.d("myPage","프로필 조회 성공 : ${response.body()}")
            response.body()
        }else{
            Log.d("myPage","프로필 조회 실패 : ${response.errorBody()?.string()}")
            null
        }
    }
    private fun setTablayout() {
        binding.viewPager.adapter = MyPagerAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.viewPager){tab, pos->
            tab.text = when(pos){
                0->"즐겨찾기"
                1->"나의 리뷰"
                2->"작성 댓글"
                else->""
            }
        }.attach()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}

class MyPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val NUM_TABS = 3
    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->MyFavoritesFragment()
            1->MyReviewsFragment()
            2->MyCommentsFragment()
            else -> Fragment()
        }
    }


}
