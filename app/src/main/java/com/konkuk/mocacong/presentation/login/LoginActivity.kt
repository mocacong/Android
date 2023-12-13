package com.konkuk.mocacong.presentation.login

import androidx.lifecycle.ViewModelProvider
import com.konkuk.mocacong.R
import com.konkuk.mocacong.databinding.ActivityLoginBinding
import com.konkuk.mocacong.presentation.base.BaseActivity
import com.konkuk.mocacong.remote.repositories.LoginRepository
import com.konkuk.mocacong.util.ViewModelFactory

class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    override val TAG: String= "LoginActivity"
    override val layoutRes: Int = R.layout.activity_login
    lateinit var viewModel: LoginViewModel

    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory(LoginRepository()))[LoginViewModel::class.java]
    }

    override fun afterViewCreated() {

    }
}