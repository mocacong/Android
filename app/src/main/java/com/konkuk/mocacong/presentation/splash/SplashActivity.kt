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
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    val TAG = "Splash"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkToken()
    }

    private val repository = TokenRepository()

    private fun checkToken() = lifecycleScope.launch {
        val startTime = System.currentTimeMillis().toInt()

        Log.d(TAG, "checkToken 들어옴")
        val refreshToken = withContext(Dispatchers.Default) {
            TokenManager.getRefreshToken().first()
        }
        if (refreshToken.isNullOrBlank()) {
            Log.d(TAG, "refresh token is NULL")
            gotoActivity(LoginActivity::class.java, startTime)
            return@launch
        }

        val response = withContext(Dispatchers.IO) {
            postRefresh(refreshToken)
        }
        response.byState(
            onSuccess = {
                CoroutineScope(Dispatchers.Default).launch {
                    TokenManager.saveAccessToken(it.accessToken)
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

    private fun gotoActivity(activity: Class<*>?, startTime: Int) = lifecycleScope.launch {
        val endTime = System.currentTimeMillis()  // 종료 시간 기록
        val elapsedTime = endTime - startTime  // 경과 시간 계산

        if (elapsedTime < 2000) {
            delay(2000 - elapsedTime)  // 2초 동안 기다리기
        }

        val intent = Intent(this@SplashActivity, activity)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }


}