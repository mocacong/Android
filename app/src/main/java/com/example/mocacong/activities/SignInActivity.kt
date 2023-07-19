package com.example.mocacong.activities


import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mocacong.controllers.SignInController
import com.example.mocacong.data.objects.Utils
import com.example.mocacong.data.request.SignInRequest
import com.example.mocacong.databinding.ActivitySignInBinding
import com.example.mocacong.ui.MessageDialog
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
            val request = SignInRequest(emailID, pwText)
            signInEvent(request)
        }

        binding.signUpBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.findBtn.setOnClickListener {
            val msg = "서비스 준비 중입니다"
            val dialog = MessageDialog(msg)
            dialog.show(supportFragmentManager, "MessageDialog")
        }

        binding.kakaoBtn.setOnClickListener {
            kakaoLogin()
        }

    }

    private fun kakaoLogin() {
        val intent = Intent(this, KakaoLoginActivity::class.java)
        startActivity(intent)
    }


    private fun signInEvent(member: SignInRequest) {
        lifecycleScope.launch {
            val msg = async { controller.signIn(member) }.await()
            if (msg == "로그인 성공") {
                Utils.showToast(this@SignInActivity, "로그인 성공. 환영합니다")
                val intent = Intent(this@SignInActivity, MainActivity::class.java)
                startActivity(intent)
            } else
                MessageDialog(msg).show(supportFragmentManager, "MessageDialog")
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

