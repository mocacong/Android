package com.example.mocacong.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.mocacong.controllers.SignUpController
import com.example.mocacong.data.objects.Utils
import com.example.mocacong.data.request.SignInRequest
import com.example.mocacong.data.request.SignUpRequest
import com.example.mocacong.databinding.ActivitySignUpBinding
import com.example.mocacong.network.ServerNetworkException
import com.example.mocacong.ui.MessageDialog
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var controller: SignUpController
    private var isEmailChecked = false
    private var isNicknameChecked = false
    private var isPasswordChecked = false
    private var isPasswordEqual = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        controller = SignUpController()

        try {
            initLayout()
        }catch (e: ServerNetworkException){
            MessageDialog(e.responseMessage)
        }
    }

    private fun initLayout() {
        //이메일 확인 버튼 클릭 이벤트
        binding.emailOk.setOnClickListener {
            checkEmail(binding.emailText.text.toString())
        }

        binding.emailText.addTextChangedListener {
            binding.emailConfirmText.visibility =
                if (controller.emailRegex(binding.emailText.text.toString())) {
                    binding.emailOk.isEnabled = true
                    View.INVISIBLE
                } else {
                    binding.emailOk.isEnabled = false
                    isEmailChecked = false
                    View.VISIBLE
                }
            isEmailChecked = false
        }

        //닉네임 확인 버튼 클릭 이벤트
        binding.nicknameOk.setOnClickListener {
            checkNickname(binding.nicknameText.text.toString())
        }

        binding.nicknameText.addTextChangedListener {
            binding.nicknameConfirmText.visibility =
                if (controller.nicknameRegex(binding.nicknameText.text.toString())) {
                    binding.nicknameOk.isEnabled = true
                    View.INVISIBLE
                } else {
                    binding.nicknameOk.isEnabled = false
                    isNicknameChecked = false
                    View.VISIBLE
                }
            isNicknameChecked = false
        }



        //비밀번호 형식 검증
        binding.pwText.addTextChangedListener {
            if (controller.passwordRegex(binding.pwText.text.toString())) {
                isPasswordChecked = true
                binding.pwConfirmText.visibility = View.INVISIBLE
            } else {
                isPasswordChecked = false
                binding.pwConfirmText.visibility = View.VISIBLE
            }
        }


        //비밀번호 일치 검증
        binding.pw2Text.addTextChangedListener {
            if (binding.pwText.text.toString() != binding.pw2Text.text.toString()) {
                binding.warningText.visibility = View.VISIBLE
                binding.warningText.text = "비밀번호가 일치하지 않습니다"
                isPasswordEqual = false
            } else {
                binding.warningText.visibility = View.INVISIBLE
                isPasswordEqual = true
            }
        }

        //가입하기 버튼 클릭 이벤트
        binding.registerBtn.setOnClickListener {
            val msg = checkInputText()
            if (msg == "") {
                val member = SignUpRequest(
                    binding.emailText.text.toString(),
                    binding.pwText.text.toString(),
                    binding.nicknameText.text.toString()
                )
                signUpEvent(member)
            } else {
                MessageDialog(msg).show(supportFragmentManager, "MessageDialog")
            }
        }

        //취소
        binding.cancelBtn.setOnClickListener {
            onBackPressed()
        }

    }

    private fun checkInputText(): String {
        if (!isEmailChecked) return "이메일을 확인해주세요"
        if (!isNicknameChecked) return "닉네임을 확인해주세요"
        if (!isPasswordChecked) return "비밀번호 형식을 확인해주세요"
        if (!isPasswordEqual) return "비밀번호를 확인해주세요"
        return ""
    }


    private fun checkEmail(email: String) {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            val isDuplicated = async {
                controller.emailConfirm(email)
            }.await()
            binding.progressBar.visibility = View.GONE
            val message =
                if (isDuplicated) {
                    isEmailChecked = false
                    "${email}은(는)\n중복된 이메일입니다"
                } else {
                    isEmailChecked = true
                    "${email}은(는)\n사용 가능한 이메일입니다"
                }
            val dialog = MessageDialog(message)
            dialog.show(supportFragmentManager, "MessageDialog")
        }
    }

    private fun checkNickname(nickName: String) {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            val isDuplicated = async { controller.nicknameConfirm(nickName) }.await()
            binding.progressBar.visibility = View.GONE
            val message =
                if (isDuplicated) {
                    isNicknameChecked = false
                    "${nickName}은(는)\n중복된 닉네임입니다"
                } else {
                    isNicknameChecked = true
                    "${nickName}은(는)\n사용 가능한 닉네임입니다"
                }
            val dialog = MessageDialog(message)
            dialog.show(supportFragmentManager, "MessageDialog")
        }
    }

    private fun signInEvent(member: SignInRequest) {
        lifecycleScope.launch {
            val msg = async { controller.signIn(member) }.await()
            if(msg == "로그인 성공"){
                Utils.showToast(this@SignUpActivity,"가입 성공. 환영합니다!")
                val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                startActivity(intent)
            }else{
                MessageDialog(msg).show(supportFragmentManager, "MessageDialog")
            }
        }
    }

    private fun signUpEvent(member: SignUpRequest) {
        var msg : String
        lifecycleScope.launch {
            msg = async {  controller.signUp(member) }.await()
            Log.d("signUp", "signUpResponse")
            if(msg =="성공") {
                signInEvent(SignInRequest(member.email, member.password))
            }else{
                MessageDialog(msg).show(supportFragmentManager, "MessageDialog")
            }
        }
    }


}