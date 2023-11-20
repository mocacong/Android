package com.konkuk.mocacong.presentaion.main.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.konkuk.mocacong.R
import com.konkuk.mocacong.databinding.FragmentSettingsBinding
import com.konkuk.mocacong.objects.Member
import com.konkuk.mocacong.presentaion.login.WebViewActivity

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        setLayout()


        return binding.root
    }

    private fun setLayout() {
        binding.apply {
            editProfileBtn.setOnClickListener {
                val intent = Intent(requireContext(), EditProfileActivity::class.java)
                startActivity(intent)

            }

            logoutBtn.setOnClickListener {
                //멤버초기화하고 로그인액티비티로
                Member.deleteInfo()
                val intent = Intent(requireContext(), SignInActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

            withdrawBtn.setOnClickListener {
                //계정탈퇴하기
                //1. 비번 확인 2. 맞으면 탈퇴 틀리면 오류
//                MessageDialog("서비스 준비 중입니다").show(childFragmentManager, "MessageDialog")
            }

            termsBtn.setOnClickListener {
                gotoTermsActivity(getString(R.string.termsUrl))
            }

            locationTermsBtn.setOnClickListener {
                gotoTermsActivity(getString(R.string.locationTermsUrl))
            }

            privacyBtn.setOnClickListener {
                gotoTermsActivity(getString(R.string.privacyUrl))
            }

            qnaBtn.setOnClickListener {
                gotoTermsActivity(getString(R.string.qnaUrl))
            }

        }
    }

    private fun gotoTermsActivity(urlString: String) {
        val intent = Intent(requireActivity(), WebViewActivity::class.java)
        intent.putExtra("urlString", urlString)
        startActivity(intent)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}

