package com.konkuk.mocacong.presentation.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.konkuk.mocacong.R
import com.konkuk.mocacong.databinding.ActivityLoginBinding
import com.konkuk.mocacong.presentation.base.BaseActivity
import com.konkuk.mocacong.presentation.main.MainActivity
import com.konkuk.mocacong.remote.repositories.LoginRepository
import com.konkuk.mocacong.util.TokenManager
import com.konkuk.mocacong.util.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    override val TAG: String = "LoginActivity"
    override val layoutRes: Int = R.layout.activity_login
    lateinit var viewModel: LoginViewModel


    private val loginFragment by lazy {
        supportFragmentManager.findFragmentByTag(LoginFragment::class.java.name)
            ?: LoginFragment()
    }

    private val joinFragment by lazy {
        supportFragmentManager.findFragmentByTag(JoinFragment::class.java.name)
            ?: JoinFragment()
    }

    private fun getFragment(page: LoginPage): Fragment {
        return when (page) {
            LoginPage.LOGIN -> loginFragment
            LoginPage.JOIN -> joinFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel =
            ViewModelProvider(this, ViewModelFactory(LoginRepository()))[LoginViewModel::class.java]
        checkToken()
        super.onCreate(savedInstanceState)
    }

    private fun checkToken() {
        lifecycleScope.launch {
            val refreshToken = withContext(Dispatchers.Default) {
                TokenManager.getRefreshToken().first()
            }
            if (refreshToken.isNullOrBlank()) {
                return@launch
            }

            val response = withContext(Dispatchers.IO) {
                viewModel.postRefresh(refreshToken)
            }
            response.byState(
                onSuccess = {
                    CoroutineScope(Dispatchers.Default).launch {
                        TokenManager.saveAccessToken(it.accessToken)
                        startNextActivity(MainActivity::class.java)
                    }
                },
                onFailure = {
                    when(it.code){
                        1021->{
                            setTheme(R.style.Theme_Mocacong)
                            return@byState
                        }
                        1022->{
                            startNextActivity(MainActivity::class.java)
                        }
                    }
                }
            )
        }

    }

    override fun afterViewCreated() {
        collectPage()
    }

    private fun collectPage() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                var prevPage = viewModel.pageFlow.value
                viewModel.pageFlow.collect { page ->
                    val preFragment = getFragment(prevPage)
                    val fragment = getFragment(page)
                    supportFragmentManager.commit {
                        if (preFragment != fragment) hide(preFragment)
                        if (fragment.isAdded) show(fragment)
                        else add(R.id.fragmentContainer, fragment, fragment.javaClass.name)
                    }
                    prevPage = page
                }
            }
        }
    }
}