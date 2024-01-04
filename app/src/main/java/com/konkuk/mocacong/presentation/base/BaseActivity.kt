package com.konkuk.mocacong.presentation.base

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActivity<T : ViewDataBinding> : AppCompatActivity() {

    abstract val TAG: String // 액티비티 태그
    lateinit var binding: T //데이터 바인딩
    abstract val layoutRes: Int // 바인딩에 필요한 layout
    private var toast: Toast? = null //토스트 보관 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "OnCreate")
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutRes)
        binding.lifecycleOwner = this
        afterViewCreated()
    }

    //  모든 백스택 지우고 다음 액티비티로 넘어감
    fun startNextActivity(activity: Class<*>?) {
        val intent = Intent(this, activity)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    abstract fun afterViewCreated()

    //  토스트 생성
    fun showToast(msg: String) {
        toast?.cancel()
        toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        toast?.show()
    }

}