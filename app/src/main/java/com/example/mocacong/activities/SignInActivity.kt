package com.example.mocacong.activities


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import com.example.mocacong.data.request.SignInRequest
import com.example.mocacong.databinding.ActivitySignInBinding
import com.example.mocacong.controllers.SignInController
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
        }

        binding.kakaoBtn.setOnClickListener {
            kakaoLogin()
        }

        binding.naverBtn.setOnClickListener {
        }
    }

    private fun kakaoLogin() {
        val intent = Intent(this, KakaoLoginActivity::class.java)
        startActivity(intent)
        //post하기
        //회원가입-로그인 처리
    }


    private fun signInEvent(member: SignInRequest) {
        var toastMsg: String
        lifecycleScope.launch {
            toastMsg = controller.signIn(member)
            Log.d("signIn", "signInResponseMSG : $toastMsg")
            Toast.makeText(applicationContext, toastMsg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setBackBtn() {
        val onBackPressedCallback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if(backButtonPressedOnce) finish()
                else{
                    backButtonPressedOnce = true
                    Toast.makeText(this@SignInActivity, "한 번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show()
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

