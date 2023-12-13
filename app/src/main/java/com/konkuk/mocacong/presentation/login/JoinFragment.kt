package com.konkuk.mocacong.presentation.login

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.activityViewModels
import com.konkuk.mocacong.R
import com.konkuk.mocacong.databinding.FragmentJoinBinding
import com.konkuk.mocacong.objects.Member
import com.konkuk.mocacong.objects.Utils.handleEnterKey
import com.konkuk.mocacong.presentation.base.BaseFragment
import com.konkuk.mocacong.presentation.main.MainActivity

class JoinFragment : BaseFragment<FragmentJoinBinding>() {
    override val TAG: String = javaClass.simpleName
    override val layoutRes: Int = R.layout.fragment_join
    val viewModel: LoginViewModel by activityViewModels()

    override fun afterViewCreated() {
        initLayout()
        initObserver()
    }

    private fun initObserver() {
        viewModel.nickDupResponse.observeLiveData(
            onSuccess = {
                Log.d(TAG, "닉네임 중복체크 response: $it")
                if (it.result) showToast("중복된 닉네임입니다")
                else viewModel.requestKakaoSignUp(binding.nicknameText.text.toString())
            }
        )

        viewModel.kakaoLoginResponse.observeLiveData(
            onSuccess = {
                showToast("로그인 성공. 환영합니다")
                Log.d(TAG, "카카오 OAUTH 로그인 성공")
                Member.setAuthToken(it.token)
//            navigateAction(R.id.action_global_to_main)
                startNextActivity(MainActivity::class.java)
            }
        )

        viewModel.kakaoSignUpResponse.observeLiveData(
            onSuccess = {
                Log.d(TAG, "카카오 OAUTH 가입 성공")
                viewModel.postKakaoLogin()
            }
        )
    }

    private fun initLayout() {
        binding.nicknameText.apply {
            handleEnterKey(onEnter = {
                if (binding.nicknameInputLayout.error == null)
                    binding.completeBtn.performClick()
            })
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (p0 == "") binding.nicknameInputLayout.error = "닉네임 형식을 확인해주세요"
                }

                override fun afterTextChanged(p0: Editable?) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    binding.nicknameInputLayout.error =
                        if (viewModel.nicknameRegex(p0.toString())) null
                        else "닉네임 형식을 확인해주세요"
                    binding.completeBtn.isEnabled = (binding.nicknameInputLayout.error == null)
                }
            })
        }

        binding.completeBtn.setOnClickListener {
            viewModel.requestNicknameCheck(binding.nicknameText.text.toString())
        }
    }

}