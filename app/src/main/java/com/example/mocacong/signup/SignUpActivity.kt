package com.example.mocacong.signup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.example.mocacong.data.request.SignUpRequest
import com.example.mocacong.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var controller : SignUpController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        controller = SignUpController(this)
        init()
    }

    private fun init(){


        binding.apply {

            //이메일 확인 버튼 클릭 이벤트
            emailOk.setOnClickListener {
                controller.emailConfirm(emailText.text.toString())
            }

            //닉네임 확인 버튼 클릭 이벤트
            nicknameOk.setOnClickListener {
                controller.nicknameConfirm(nicknameText.text.toString())
            }

            //비밀번호 형식 검증
            pwText.addTextChangedListener {
                if(controller.passwordRegex(pwText.text.toString())){
                    pwConfirmText.visibility = View.INVISIBLE
                }else{
                    pwConfirmText.visibility = View.VISIBLE
                }
            }

            //비밀번호 일치 검증
            pw2Text.addTextChangedListener {
                if(pwText.text.toString()!=pw2Text.text.toString()) {
                    warningText.visibility = View.VISIBLE
                    warningText.text = "비밀번호가 일치하지 않습니다"
                    registerBtn.isEnabled = false
                }
                else{
                    warningText.text = "비밀번호가 일치합니다"
                    registerBtn.isEnabled = true
                }
            }


            //가입하기 버튼 클릭 이벤트
            registerBtn.setOnClickListener {
                val member = SignUpRequest(emailText.text.toString(), phoneText.text.toString(), pwText.text.toString(), nicknameText.text.toString())
                controller.signUp(member)
            }

            //취소
            cancelBtn.setOnClickListener{
                TODO()
            }

        }
    }

}