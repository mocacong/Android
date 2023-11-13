package com.konkuk.mocacong.presentaion.login


import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.konkuk.mocacong.databinding.ActivitySignInBinding
import com.konkuk.mocacong.objects.NetworkManager.Companion.isNetworkConnected
import com.konkuk.mocacong.objects.NetworkManager.Companion.showCheckDialog
import com.konkuk.mocacong.objects.Utils
import com.konkuk.mocacong.presentaion.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var controller: SignInController
    private var backButtonPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        controller = SignInController()
        layoutInit()
        setBackBtn()
    }


    private fun layoutInit() {
        binding.signInBtn.setOnClickListener {
            val emailID = binding.emailText.text.toString()
            val pwText = binding.pwText.text.toString()
            val request = com.konkuk.mocacong.remote.models.request.SignInRequest(emailID, pwText)
            signInEvent(request)
        }

        binding.signUpBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.findBtn.setOnClickListener {
            val msg = "서비스 준비 중입니다"
            TODO()
        }

        binding.kakaoBtn.setOnClickListener {
            kakaoLogin()
        }

    }

    private fun kakaoLogin() {
        val intent = Intent(this, KakaoLoginActivity::class.java)
        startActivity(intent)
    }


    private fun signInEvent(member: com.konkuk.mocacong.remote.models.request.SignInRequest) {
        lifecycleScope.launch {
            if (!isNetworkConnected(this@SignInActivity)) {
                showCheckDialog(supportFragmentManager)
                return@launch
            }
            val msg = withContext(Dispatchers.IO) {
                controller.signIn(member)
            }
            if (msg == "로그인 성공") {
                Utils.showToast(this@SignInActivity, "로그인 성공. 환영합니다")
                val intent = Intent(this@SignInActivity, MainActivity::class.java)
                startActivity(intent)
            } else
                TODO()
        }
    }

    private fun setBackBtn() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (backButtonPressedOnce) finish()
                else {
                    backButtonPressedOnce = true

                    Utils.showToast(this@SignInActivity, "한 번 더 누르면 종료됩니다")
                    lifecycleScope.launch {
                        delay(2000)
                        backButtonPressedOnce = false
                    }
                }
            }
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

}

