package com.example.mocacong.signin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.mocacong.data.request.SignInRequest
import com.example.mocacong.databinding.ActivitySignInBinding
import com.example.mocacong.signup.SignUpActivity
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var controller: SignInController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        controller = SignInController()
        layoutInit()
    }

    private fun layoutInit() {

        binding.signInBtn.setOnClickListener {
            val emailid = binding.emailText.text.toString()
            val pwText = binding.pwText.text.toString()
            val request = SignInRequest(emailid, pwText)
            signInEvent(request)
        }

        binding.signUpBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.findBtn.setOnClickListener {
        }

        binding.kakaoBtn.setOnClickListener {
        }

        binding.naverBtn.setOnClickListener {
        }
    }

    private fun signInEvent(member: SignInRequest) {
        var toastMsg = ""
        lifecycleScope.launch {
            toastMsg = controller.signIn(member)
            Log.d("signIn", "signInResponseMSG : $toastMsg")
            Toast.makeText(applicationContext, toastMsg, Toast.LENGTH_SHORT).show()
        }


    }

}

