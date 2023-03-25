package com.example.mocacong.signin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mocacong.R
import com.example.mocacong.data.request.SignInRequest
import com.example.mocacong.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {

    private lateinit var binding :ActivitySignInBinding
    private lateinit var controller: SignInController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }


    fun init(){
        binding.apply {
            signInBtn.setOnClickListener {
                val id = emailText.text.toString()
                val pw = pwText.text.toString()
                val request = SignInRequest(id,pw)
                controller.signIn(request)


            }

            signUpBtn.setOnClickListener {
                TODO("회원가입 페이지 이동하기")
            }

            findBtn.setOnClickListener {
                TODO("안됩니다 다이얼로그")
            }

            kakaoBtn.setOnClickListener {
                TODO("KAKAO OAuth")
            }

            naverBtn.setOnClickListener {
            TODO("Naver OAuth")
            }
        }

    }

}