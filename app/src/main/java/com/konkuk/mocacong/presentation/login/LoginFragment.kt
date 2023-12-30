package com.konkuk.mocacong.presentation.login

import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.konkuk.mocacong.R
import com.konkuk.mocacong.databinding.FragmentLoginBinding
import com.konkuk.mocacong.presentation.base.BaseFragment
import com.konkuk.mocacong.presentation.main.MainActivity
import com.konkuk.mocacong.remote.models.response.KakaoLoginResponse
import com.konkuk.mocacong.util.ApiState
import com.konkuk.mocacong.util.TokenManager
import kotlinx.coroutines.launch

class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    override val TAG: String = "LoginFragment"
    override val layoutRes: Int = R.layout.fragment_login
    private val viewModel: LoginViewModel by activityViewModels()


    override fun afterViewCreated() {
        binding.kakaoBtn.setOnClickListener {
            onKakaoBtnClicked()
        }
        observeData()
    }

    private fun observeData() {
        viewModel.kakaoLoginResponse.observeLiveData(
            onSuccess = onLoginSucceed
        )
    }

    private val onLoginSucceed: (kakaoLoginResponse: KakaoLoginResponse) -> Unit = {
        Log.d(TAG, "[onLoginSucceed] response: $it")
        if (it.isRegistered) {
            lifecycleScope.launch {
                TokenManager.saveAccessToken(it.accessToken)
                TokenManager.saveRefreshToken(it.refreshToken)
            }
            startNextActivity(MainActivity::class.java)
        } else {
            //회원가입 페이지로
            showToast("회원 정보 없음. 가입을 시작합니다")
            viewModel.mKakaoLoginResponse.value = ApiState.Loading()
            viewModel.goto(LoginPage.JOIN)
        }
    }


    private fun onKakaoBtnClicked() {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
            UserApiClient.instance.loginWithKakaoTalk(requireContext()) { token, error ->
                if (error != null) {
                    Log.e(TAG, "카카오톡으로 로그인 실패", error)
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }
                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(
                        requireContext(),
                        callback = kakaoCallBack
                    )
                } else if (token != null) {
                    Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                    postServerLogin()
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = kakaoCallBack)
        }
    }

    private val kakaoCallBack: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(TAG, "카카오계정으로 로그인 실패", error)
        } else if (token != null) {
            Log.i(TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")
            postServerLogin()
        }
    }

    private fun postServerLogin() {
        //카카오 sdk로부터 계정의 id와 email을 가져옴
        Log.d(TAG, "postServerLogin 실행")
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e(TAG, "사용자 정보 요청 실패", error)
            } else if (user != null) {
                viewModel.kakaoEmail = user.kakaoAccount?.email.toString()
                viewModel.kakaoID = user.id.toString()
                viewModel.postKakaoLogin()
            }
        }
    }

}