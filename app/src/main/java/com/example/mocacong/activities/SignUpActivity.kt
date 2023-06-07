package com.example.mocacong.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.mocacong.controllers.SignUpController
import com.example.mocacong.data.request.SignUpRequest
import com.example.mocacong.databinding.ActivitySignUpBinding
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
    private var isPhoneChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        controller = SignUpController()
        initLayout()
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

        binding.phoneText.addTextChangedListener {
            binding.phoneConfirmText.visibility =
                if (controller.phoneRegex(binding.phoneText.text.toString())) {
                    isPhoneChecked = true
                    View.INVISIBLE
                } else {
                    isPhoneChecked = false
                    View.VISIBLE
                }
        }

        //가입하기 버튼 클릭 이벤트
        binding.registerBtn.setOnClickListener {
            val msg = checkInputText()
            if (msg == "") {
                val member = SignUpRequest(
                    binding.emailText.text.toString(),
                    binding.phoneText.text.toString(),
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
        if (!isPhoneChecked) return "전화번호를 확인해주세요"
        return ""
    }


    private fun checkEmail(email: String) {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            val isDuplicated = async { controller.emailConfirm(email) }.await()
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

    private fun signUpEvent(member: SignUpRequest) {
        var code = 0
        lifecycleScope.launch {
            code = controller.signUp(member)
            Log.d("signUp", "signUpResponseCode : $code")
        }

        when (code) {
            200 -> {
                val intent = Intent(this, SignInActivity::class.java)
                intent.putExtra("id", binding.emailText.text.toString())
                intent.putExtra("pw", binding.emailText.text.toString())
                startActivity(intent)
            }
        }

    }


}