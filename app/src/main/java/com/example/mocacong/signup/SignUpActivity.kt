package com.example.mocacong.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.mocacong.data.request.SignUpRequest
import com.example.mocacong.databinding.ActivitySignUpBinding
import com.example.mocacong.signin.SignInActivity
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var controller: SignUpController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        controller = SignUpController()
        init()
    }

    private fun init() {


        //이메일 확인 버튼 클릭 이벤트
        binding.emailOk.setOnClickListener {
            controller.emailConfirm(binding.emailText.text.toString())
        }

        //닉네임 확인 버튼 클릭 이벤트
        binding.nicknameOk.setOnClickListener {
            controller.nicknameConfirm(binding.nicknameText.text.toString())
        }

        //비밀번호 형식 검증
        binding.pwText.addTextChangedListener {
            if (controller.passwordRegex(binding.pwText.text.toString())) {
                binding.pwConfirmText.visibility = View.INVISIBLE
            } else {
                binding.pwConfirmText.visibility = View.VISIBLE
            }
        }

        //비밀번호 일치 검증
        binding.pw2Text.addTextChangedListener {
            if (binding.pwText.text.toString() != binding.pw2Text.text.toString()) {
                binding.warningText.visibility = View.VISIBLE
                binding.warningText.text = "비밀번호가 일치하지 않습니다"
                binding.registerBtn.isEnabled = false
            } else {
                binding.warningText.text = "비밀번호가 일치합니다"
                binding.registerBtn.isEnabled = true
            }
        }


        //가입하기 버튼 클릭 이벤트
        binding.registerBtn.setOnClickListener {
            val member = SignUpRequest(
                binding.emailText.text.toString(),
                binding.phoneText.text.toString(),
                binding.pwText.text.toString(),
                binding.nicknameText.text.toString()
            )
            signUpEvent(member)


        }

        //취소
        binding.cancelBtn.setOnClickListener {
            onBackPressed()
        }

    }

    private fun signUpEvent(member : SignUpRequest){
        var code = 0
        lifecycleScope.launch {
            code = controller.signUp(member)
            Log.d("signUp", "signUpResponseCode : $code")
        }

        when(code){
            200->{
                val intent = Intent(this, SignInActivity::class.java)
                intent.putExtra("id", binding.emailText.text.toString())
                intent.putExtra("pw", binding.emailText.text.toString())
                startActivity(intent)
            }

        }

    }


}