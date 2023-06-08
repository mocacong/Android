package com.example.mocacong.activities

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mocacong.data.objects.Member
import com.example.mocacong.data.objects.RetrofitClient
import com.example.mocacong.data.request.OAuthRequest
import com.example.mocacong.data.response.KakaoLoginResponse
import com.example.mocacong.databinding.ActivityKakaoLoginBinding
import com.example.mocacong.network.SignInAPI
import com.example.mocacong.network.SignUpAPI
import kotlinx.coroutines.launch

class KakaoLoginActivity : AppCompatActivity() {
    //todo : 닉네임 입력 뷰 프래그먼트로 생성하기

    private lateinit var binding: ActivityKakaoLoginBinding

    private val REST_API_KEY = "0c5797613ead2a6d69354f77254c6a25"
    private val REDIRECT_URI = "http://3.37.64.38:8080/login/kakao"

//    private val REDIRECT_URI = "http://localhost:8080/login/kakao"
    private lateinit var authCode: String
    private lateinit var responseBody: KakaoLoginResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKakaoLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //카카오서버에서 인가코드 받기
        kakaoOAuthLogin()
        setListener()
    }

    private fun setListener() {
        binding.nextBtn.setOnClickListener {
            binding.nickEditText.text.toString()
            postSignUp()
        }
    }


    private fun postSignUp() {
        val nickName = binding.nickEditText.text.toString()
        val signUpApi = RetrofitClient.create(SignUpAPI::class.java)
        val request = OAuthRequest(responseBody.email,nickName,"kakao", responseBody.platformId)
        lifecycleScope.launch {
            val signUpResponse =
                signUpApi.oAuthSignUp(request)
            Log.d("kakaoOauth","모카콩 서버 회원가입 post : ${request}")
            if (signUpResponse.isSuccessful) {
                Log.d("kakaoOauth", "모카콩 서버 회원가입 성공 : ${signUpResponse.body()}")
                Toast.makeText(this@KakaoLoginActivity, "회원가입 성공", Toast.LENGTH_SHORT).show()
                kakaoOAuthLogin()
            } else {
                Log.d("kakaoOauth", "모카콩 서버 회원가입 실패 : ${signUpResponse.errorBody()?.string()}")
            }
        }

    }


    private fun kakaoOAuthLogin() {
        val webView = binding.webView
        var uri =
            "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=${REST_API_KEY}&redirect_uri=${REDIRECT_URI}"

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                if (url?.startsWith(REDIRECT_URI) == true) {
                    authCode = Uri.parse(url).getQueryParameter("code").toString()
                    Log.d("kakaoOauth", "authcode : ${authCode}")
                    Log.i("kakaoOauth", "url : ${url}")
                    webView.visibility=View.GONE
                    sendCode()

                }
                super.onPageStarted(view, url, favicon)
            }
        }

        webView.loadUrl(uri)
    }


    private fun sendCode() {
        lifecycleScope.launch {
            val signInApi = RetrofitClient.create(SignInAPI::class.java)
            val response = signInApi.kakaoLoginPost(authCode)
            Log.d("kakaoOauth", "모카콩 Server login POST code = ${authCode}")

            if (response.isSuccessful) {
                Log.d("kakaoOauth", "모카콩 Server login POST Success : ${response.body().toString()}")
                responseBody = response.body()!!
                responseBody.let {
                    if (it.isRegistered) {
                        //회원가입되어있는 경우
                        Member.setAuthToken(it.token)
                        val intent = Intent(this@KakaoLoginActivity, MainActivity::class.java)
                        //스택의 하위 액티비티 지움 -> MainActivity를 단일 최상위 액티비티로 설정
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@KakaoLoginActivity,"회원 정보 없음. 가입을 시작합니다",Toast.LENGTH_SHORT).show()
                        //닉네임 입력 - 회원가입 POST - 로그인 POST 필요
                        setNicknameVisible()
                    }
                }

            } else {
                Log.d("kakaoOauth", "모카콩 Server login POST error : ${response.errorBody()?.string()}")

            }
        }
    }

    private fun setNicknameVisible() {
        binding.webView.visibility = View.GONE
        binding.nickEditText.visibility = View.VISIBLE
        binding.nextBtn.visibility = View.VISIBLE
        binding.nickLabel.visibility = View.VISIBLE
    }
}