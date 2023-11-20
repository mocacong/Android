package com.konkuk.mocacong.presentation.login

import android.util.Log
import androidx.fragment.app.activityViewModels
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.konkuk.mocacong.R
import com.konkuk.mocacong.databinding.FragmentLoginBinding
import com.konkuk.mocacong.presentation.base.BaseFragment
import com.konkuk.mocacong.presentation.main.MainActivity
import com.konkuk.mocacong.remote.apis.KakaoRequest
import com.konkuk.mocacong.util.ApiState
import com.konkuk.mocacong.util.TokenExceptionHandler

class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    override val TAG: String = "LoginFragment"
    override val layoutRes: Int = R.layout.fragment_login
    private val viewModel: LoginViewModel by activityViewModels()

    private val REST_API_KEY = "0c5797613ead2a6d69354f77254c6a25"


    override fun afterViewCreated() {
        binding.kakaoBtn.setOnClickListener {
            onKakaoBtnClicked()
        }

        observeData()
    }

    private fun observeData() {
        viewModel.kakaoLoginResponse.observe(this) { state ->
            when (state) {
                is ApiState.Success -> {
                    state.data?.let {
                        if(it.isRegistered){
                            //로그인성공
                            Log.d(TAG, "카카오 계정 로그인 성공")
                            startNextActivity(MainActivity::class.java)
                        }else{
                            //회원가입 페이지로


                        }
                    }
                }
                is ApiState.Error -> {
                    state.errorResponse?.let { er ->
                        TokenExceptionHandler.handleTokenException(requireContext(), er)
                        Log.e(TAG, er.message)
                    }
                    //TODO: 다시시도
                }
                is ApiState.Loading -> {

                }
            }
        }

        viewModel.kakaoSignUpResponse.observe(this){state->
            when(state){
                is ApiState.Success->{
                    //회원가입 성공
                }
                is ApiState.Error -> TODO()
                is ApiState.Loading -> TODO()
            }
        }
    }

    private val kakaoCallBack: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(TAG, "카카오계정으로 로그인 실패", error)
        } else if (token != null) {
            Log.i(TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")

            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    Log.e(TAG, "사용자 정보 요청 실패", error)
                } else if (user != null) {
                    Log.d(
                        TAG,
                        KakaoRequest(user.kakaoAccount!!.email!!, user.id.toString()).toString()
                    )

                    val id = user.id.toString()
                    val email = user.kakaoAccount?.email.toString()
                    sendKakaoLogin((KakaoRequest(email = email, platformId = id)))
                }
            }
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

                            val id = user.id.toString()
                            val email = user.kakaoAccount?.email.toString()
                            sendKakaoLogin((KakaoRequest(email = email, platformId = id)))
                        }
                    }
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = kakaoCallBack)
        }
    }

    private fun sendKakaoLogin(kakaoRequest: KakaoRequest) {
        viewModel.postKakaoLogin(kakaoRequest)
    }


}