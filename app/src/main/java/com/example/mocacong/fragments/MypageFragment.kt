package com.example.mocacong.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mocacong.data.objects.RetrofitClient
import com.example.mocacong.data.response.ProfileResponse
import com.example.mocacong.databinding.FragmentMypageBinding
import com.example.mocacong.network.MyPageAPI
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class MypageFragment : Fragment() {

    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)

        setInitialFragment()
        setTabLayout()
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

    private suspend fun getProfile(): ProfileResponse? {
        val api = RetrofitClient.create(MyPageAPI::class.java)
        val response = api.getMyProfile()
        return if (response.isSuccessful) {
            Log.d("myPage", "프로필 조회 성공 : ${response.body()}")
            response.body()
        } else {
            Log.d("myPage", "프로필 조회 실패 : ${response.errorBody()?.string()}")
            null
        }
    }

    private fun setTabLayout() {

        binding.tabLayout.apply {
            addTab(newTab().setText("즐겨찾기"))
            addTab(newTab().setText("나의 리뷰"))
            addTab(newTab().setText("작성 댓글"))
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val position = tab.position
                showFragment(position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun setInitialFragment() {
        val initialPosition = 0 // Set the initial tab position
        showFragment(initialPosition)
    }

    private fun showFragment(position: Int) {
        val fragment = getFragment(position)

        childFragmentManager.beginTransaction().apply {
            replace(binding.frameLayout.id, fragment)
            commit()
        }
    }



    private fun getFragment(position: Int): Fragment {
        Log.d("Mypage","getFragment $position")
        return when (position) {
            0 -> MyFavoritesFragment()
            1 -> MyReviewsFragment()
            2 -> MyCommentsFragment()
            else -> Fragment()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}

