package com.konkuk.mocacong.presentation.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.konkuk.mocacong.presentation.login.LoginActivity
import com.konkuk.mocacong.presentation.main.MainActivity
import com.konkuk.mocacong.remote.models.request.ReIssueRequest
import com.konkuk.mocacong.remote.repositories.TokenRepository
import com.konkuk.mocacong.util.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    val TAG = "Splash"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkToken()
    }

    @Inject lateinit var repository: TokenRepository

    private fun checkToken() = lifecycleScope.launch {
        val startTime = System.currentTimeMillis().toInt()

        val refreshToken = withContext(Dispatchers.IO) {
            TokenManager.getRefreshToken().first()
        }
        if (refreshToken.isNullOrBlank()) {
            gotoActivity(LoginActivity::class.java, startTime)
            return@launch
        }

        val response = withContext(Dispatchers.IO) {
            postRefresh(refreshToken)
        }

        response.byState(
            onSuccess = {
                Log.d(TAG, "성공")
                CoroutineScope(Dispatchers.Default).launch {
                    withContext(Dispatchers.IO) {
                        TokenManager.saveAccessToken(it.accessToken)
                    }
                    gotoActivity(MainActivity::class.java, startTime)
                }
            },
            onFailure = {
                when (it.code) {
                    1021 -> {
                        gotoActivity(LoginActivity::class.java, startTime)
                    }
                    1022 -> {
                        gotoActivity(MainActivity::class.java, startTime)
                    }
                    else -> {
                        gotoActivity(LoginActivity::class.java, startTime)
                    }
                }
            }
        )
    }

    private suspend fun postRefresh(token: String) = repository.refresh(ReIssueRequest(token))

    private fun gotoActivity(activity: Class<*>, startTime: Int) =
        CoroutineScope(Dispatchers.Main).launch {
            val endTime = System.currentTimeMillis()
            val elapsedTime = endTime - startTime
            if (elapsedTime < 2000) {
                delay(2000 - elapsedTime)
            }

            val intent = Intent(this@SplashActivity, activity)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }


}