package com.konkuk.mocacong.data.objects

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import java.io.Serializable

object Utils {
    private var toast: Toast? = null

    fun showConfirmDialog(
        context: Context,
        msg: String,
        confirmAction: () -> Unit,
        cancelAction: () -> Unit
    ) {
        AlertDialog.Builder(context).apply {
            setCancelable(false)
            setMessage(msg)
            setPositiveButton("확인") { _: DialogInterface, _: Int ->
                confirmAction()

            }
            setNegativeButton("취소") { _: DialogInterface, _: Int ->
                cancelAction()
            }
            create()
        }.show()
    }

    fun EditText.handleEnterKey() {
        val inputMethodManager =
            ContextCompat.getSystemService(context, InputMethodManager::class.java)
        this.setOnKeyListener { _, code, keyEvent ->
            if ((keyEvent.action == KeyEvent.ACTION_DOWN) && (code == KeyEvent.KEYCODE_ENTER)) {
                (inputMethodManager)?.hideSoftInputFromWindow(
                    this.windowToken,
                    0
                )
                true
            }
            false
        }
    }

    fun EditText.showKeyboard() {
        isFocusableInTouchMode = true
        requestFocus()
        postDelayed({
            val inputMethodManager =
                ContextCompat.getSystemService(context, InputMethodManager::class.java)
            inputMethodManager?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
        }, 30)
    }


    fun showToast(context: Context, msg: String) {
        toast?.cancel()
        toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT)
        toast?.show()
    }

    fun <T : Serializable> Intent.intentSerializable(key: String, clazz: Class<T>): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.getSerializableExtra(key, clazz)
        } else {
            this.getSerializableExtra(key) as T?
        }
    }

    fun <T : Serializable> Bundle.bundleSerializable(key: String, clazz: Class<T>): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.getSerializable(key, clazz)
        } else {
            this.getSerializable(key) as T?
        }
    }


}