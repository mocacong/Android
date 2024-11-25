package com.konkuk.mocacong.presentation.base

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.konkuk.mocacong.R
import com.konkuk.mocacong.remote.models.response.ErrorResponse
import com.konkuk.mocacong.util.ApiState

abstract class BaseFragment<T : ViewDataBinding> : Fragment() {

    private var _binding: T? = null
    protected val binding get() = _binding!!

    abstract val TAG: String // fragment 태그
    abstract val layoutRes: Int // 바인딩에 필요한 layout
    private var toast: Toast? = null //토스트 보관 변수

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        afterViewCreated()
    }

    protected abstract fun afterViewCreated()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun startNextActivity(activity: Class<*>?) {
        val intent = Intent(requireActivity(), activity)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    fun showToast(msg: String) {
        toast?.cancel()
        toast = Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT)
        toast?.show()
    }


    fun <D, R> LiveData<ApiState<D>>.observeLiveData(
        onSuccess: (D) -> (R?),
        onFailure: (ErrorResponse) -> (Unit) = { Log.e(TAG, "Network error: $it")},
        onLoading: () -> (Unit) = {}
    ) {
        this.observe(this@BaseFragment) { state ->
            state.byState(
                onSuccess,
                onFailure,
                onLoading
            )
        }
    }

    fun logging(msg: String){
        Log.d(TAG, msg)
    }
}