package com.konkuk.mocacong.presentaion.main.mypage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.konkuk.mocacong.R
import com.konkuk.mocacong.presentaion.main.settings.EditProfileActivity
import com.konkuk.mocacong.presentaion.main.MainActivity
import com.konkuk.mocacong.util.ApiState
import com.konkuk.mocacong.util.TokenExceptionHandler
import com.konkuk.mocacong.databinding.FragmentMypageBinding
import com.konkuk.mocacong.presentaion.main.map.MapViewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class MypageFragment : Fragment() {

    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!

    private val mapViewModel: MapViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)

        setTabLayout()
        setLayout()
        setInitialFragment()
        return binding.root
    }

    override fun onResume() {
        setProfileInfo()
        super.onResume()
    }

    private fun setLayout() {
        binding.editProfileBtn.setOnClickListener {
            val activity = requireActivity() as MainActivity
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            activity.editProfileLauncher.launch(intent)
        }
    }

    private fun setProfileInfo() {
        lifecycleScope.launch {
            mapViewModel.apply {
                requestMemeberProfile().join()
                when (val apiState = profileInfo.value) {
                    is ApiState.Success -> {
                        apiState.data?.let {
                            binding.nickNameText.text = it.nickname
                            if (it.imgUrl == null) {
                                binding.profileImg.setImageResource(R.drawable.profile_no_image)
                            } else {
                                Glide.with(this@MypageFragment).load(it.imgUrl)
                                    .into(binding.profileImg)
                            }
                        }
                    }
                    is ApiState.Error ->{
                        apiState.errorResponse?.let {
                            TokenExceptionHandler.handleTokenException(requireContext(), it)
                            Log.e(TAG, it.message)
                        }
                    }
                    is ApiState.Loading ->{

                    }
                }
            }
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
        Log.d("Mypage", "getFragment $position")
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

