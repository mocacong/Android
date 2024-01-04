package com.konkuk.mocacong.presentation.login

import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.konkuk.mocacong.R
import com.konkuk.mocacong.databinding.ActivityLoginBinding
import com.konkuk.mocacong.presentation.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    override val TAG: String = "LoginActivity"
    override val layoutRes: Int = R.layout.activity_login
    private val viewModel: LoginViewModel by viewModels()


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