package com.konkuk.mocacong.presentation.login

import android.util.Log
import androidx.fragment.app.activityViewModels
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.konkuk.mocacong.R
import com.konkuk.mocacong.databinding.FragmentLoginBinding
import com.konkuk.mocacong.objects.Member
import com.konkuk.mocacong.presentation.base.BaseFragment
import com.konkuk.mocacong.presentation.main.MainActivity
import com.konkuk.mocacong.remote.apis.KakaoRequest
import com.konkuk.mocacong.remote.models.response.KakaoLoginResponse

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
            onSuccess = onKakaoLoginSucceed,
            onFailure = {
                //로그인 다시시도하기 or 회원가입
            }
        )

        viewModel.kakaoSignUpResponse.observeLiveData(
            onSuccess = {
                //로그인하기
            },
            onFailure = {
                //가입실패
            }
        )


    }

    private val onKakaoLoginSucceed: (kakaoLoginResponse: KakaoLoginResponse) -> Unit = {
        if (it.isRegistered) {
            Log.d(TAG, "카카오 계정 로그인 성공")
            gotoMainActivity(it.token)
        } else {
            //회원가입 페이지로

        }
    }

    private fun gotoMainActivity(token: String) {
        Member.setAuthToken(token)
        startNextActivity(MainActivity::class.java)
    }

    private val kakaoCallBack: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(TAG, "카카오계정으로 로그인 실패", error)
        } else if (token != null) {
            Log.i(TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")
            val pair = getUserInfo()
            sendKakaoLogin(KakaoRequest(platformId = pair.first, email = pair.second))
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
                    val pair = getUserInfo()
                    sendKakaoLogin(KakaoRequest(platformId = pair.first, email = pair.second))
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = kakaoCallBack)
        }
    }

    private fun sendKakaoLogin(kakaoRequest: KakaoRequest) {
        viewModel.postKakaoLogin(kakaoRequest)
    }

    private fun getUserInfo(): Pair<String, String> {
        //카카오 서버로부터 계정의 id와 email을 가져옴
        var id = ""
        var email = ""
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e(TAG, "사용자 정보 요청 실패", error)
            } else if (user != null) {
                Log.d(
                    TAG,
                    KakaoRequest(
                        user.kakaoAccount!!.email!!,
                        user.id.toString()
                    ).toString()
                )

                id = user.id.toString()
                email = user.kakaoAccount?.email.toString()
                sendKakaoLogin((KakaoRequest(email = email, platformId = id)))
            }
        }
        return Pair(id, email)
    }
}