package com.konkuk.mocacong.presentaion.base

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.konkuk.mocacong.util.ViewModelFactory

abstract class BaseActivity<T : ViewDataBinding> : AppCompatActivity() {

    abstract val TAG: String // 액티비티 태그
    lateinit var binding: T //데이터 바인딩
    abstract val layoutRes: Int // 바인딩에 필요한 layout
    private var toast: Toast? = null //토스트 보관 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutRes)
        initViewModel()
        afterViewCreated()
    }

    //  모든 백스택 지우고 다음 액티비티로 넘어감
    fun startNextActivity(activity: Class<*>?) {
        val intent = Intent(this, activity)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    //뷰모델 생성 함수
    inline fun <reified VM : ViewModel, R> createViewModel(repository: R): VM {
        val viewModelFactory = ViewModelFactory(repository)
        return ViewModelProvider(this, viewModelFactory)[VM::class.java]
    }

    abstract fun initViewModel()
    abstract fun afterViewCreated()

    //  토스트 생성
    fun showToast(msg: String) {
        toast?.cancel()
        toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        toast?.show()
    }

}