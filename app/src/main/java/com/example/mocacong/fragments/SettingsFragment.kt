package com.example.mocacong.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mocacong.activities.EditProfileActivity
import com.example.mocacong.activities.SignInActivity
import com.example.mocacong.data.objects.Member
import com.example.mocacong.databinding.FragmentSettingsBinding

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


            }

        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}

