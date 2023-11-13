package com.konkuk.mocacong.presentaion.login

import androidx.appcompat.app.AppCompatActivity

class KakaoLoginActivity : AppCompatActivity() {
    //todo : 닉네임 입력 뷰 프래그먼트로 생성하기
//
//    private lateinit var binding: ActivityKakaoLoginBinding
//
//    private val REST_API_KEY = "0c5797613ead2a6d69354f77254c6a25"
//    private val REDIRECT_URI = "http://3.37.64.38:8080/login/kakao"
//
////    private val REDIRECT_URI = "https://mocacong.com/login/kakao"
//
//    private lateinit var authCode: String
//    private lateinit var responseBody: KakaoLoginResponse
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityKakaoLoginBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        kakaoOAuthLogin()
//        setListener()
//    }
//
//    private fun setListener() {
//        binding.nextBtn.setOnClickListener {
//            val nickname = binding.nickEditText.text.toString()
//            //중복체크
//            lifecycleScope.launch {
//                val signUpApi = RetrofitClient.create(SignUpAPI::class.java)
//                val isDuplicated =
//                    withContext(Dispatchers.Default) {
//                        val duplicateResponse = signUpApi.checkNickname(nickname)
//                        if (duplicateResponse.isSuccessful) duplicateResponse.body()
//                        else throw Exception("닉네임 중복체크 에러")
//                    }?.result
//
//                if (isDuplicated == true) {
//                    TODO()
//                    binding.nickEditText.performClick()
//                } else {
//                    val request =
//                        com.konkuk.mocacong.remote.models.request.OAuthRequest(
//                            responseBody.email,
//                            nickname,
//                            "kakao",
//                            responseBody.platformId
//                        )
//                    val signUpResponse = withContext(Dispatchers.Default) {
//                        signUpApi.oAuthSignUp(request)
//                    }
//                    if (signUpResponse.isSuccessful) {
//                        Log.d("kakaoOauth", "모카콩 서버 회원가입 성공 : ${signUpResponse.body()}")
//                        Utils.showToast(this@KakaoLoginActivity, "회원가입 성공")
//                        kakaoOAuthLogin()
//                    } else {
//                        Log.d(
//                            "kakaoOauth",
//                            "모카콩 서버 회원가입 실패 : ${signUpResponse.errorBody()?.string()}"
//                        )
//                        TODO()
//                    }
//                }
//            }
//        }
//    }
//
//    private fun kakaoOAuthLogin() {
//        val webView = binding.webView
//        var uri =
//            "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=${REST_API_KEY}&redirect_uri=${REDIRECT_URI}"
//
//        webView.settings.javaScriptEnabled = true
//        webView.webViewClient = object : WebViewClient() {
//
//            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
//                if (url?.startsWith(REDIRECT_URI) == true) {
//                    authCode = Uri.parse(url).getQueryParameter("code").toString()
//                    Log.d("kakaoOauth", "authcode : $authCode")
//                    Log.i("kakaoOauth", "url : ${url}")
//                    webView.visibility = View.GONE
//                    sendCode()
//                }
//                super.onPageStarted(view, url, favicon)
//            }
//        }
//        webView.loadUrl(uri)
//    }
//
//
//    private fun sendCode() {
//        lifecycleScope.launch {
//            val signInApi = RetrofitClient.create(SignInAPI::class.java)
//            val response = signInApi.kakaoLoginPost(authCode)
//            Log.d("kakaoOauth", "모카콩 Server login POST code = ${authCode}")
//
//            if (response.isSuccessful) {
//                Log.d("kakaoOauth", "모카콩 Server login POST Success : ${response.body().toString()}")
//                responseBody = response.body()!!
//                responseBody.let {
//                    if (it.isRegistered) {
//                        //회원가입되어있는 경우
//                        Member.setAuthToken(it.token)
//                        val intent = Intent(this@KakaoLoginActivity, MainActivity::class.java)
//                        //스택의 하위 액티비티 지움 -> MainActivity를 단일 최상위 액티비티로 설정
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//                        startActivity(intent)
//                    } else {
//                        //회원가입해야함
//                        Utils.showToast(this@KakaoLoginActivity, "회원 정보 없음. 가입을 시작합니다")
//                        //닉네임 입력 - 회원가입 POST - 로그인 POST 필요
//                        setNicknameVisible()
//                    }
//                }
//            } else {
//                Log.d(
//                    "kakaoOauth",
//                    "모카콩 Server login POST error : ${response.errorBody()?.string()}"
//                )
//
//            }
//        }
//    }
//
//    private fun setNicknameVisible() {
//        binding.webView.visibility = View.GONE
//        binding.nickEditText.visibility = View.VISIBLE
//        binding.nextBtn.visibility = View.VISIBLE
//        binding.nickLabel.visibility = View.VISIBLE
//    }
}