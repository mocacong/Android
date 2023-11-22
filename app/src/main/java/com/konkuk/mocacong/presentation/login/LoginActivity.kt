package com.konkuk.mocacong.presentation.login

import com.konkuk.mocacong.R
import com.konkuk.mocacong.databinding.ActivityLoginBinding
import com.konkuk.mocacong.presentation.base.BaseActivity
import com.konkuk.mocacong.remote.repositories.LoginRepository

class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    override val TAG: String= "LoginActivity"
    override val layoutRes: Int = R.layout.activity_login
    lateinit var viewModel: LoginViewModel

    override fun initViewModel() {
        viewModel = createViewModel(LoginRepository())
    }

    override fun afterViewCreated() {

    }
}