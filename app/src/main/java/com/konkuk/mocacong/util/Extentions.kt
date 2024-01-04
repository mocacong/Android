package com.konkuk.mocacong.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.konkuk.mocacong.presentation.splash.SplashActivity

fun Context.forceRestart() {
    if (this is Activity) {
        Log.d("Restart","Force Restart")
        this.finish()
        val intent = Intent(this, SplashActivity::class.java)
        this.startActivity(intent)
    }
}


val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "mocacong")


fun EditText.handleEnterKey(onEnter: ()->(Unit) = {}) {
    val inputMethodManager =
        ContextCompat.getSystemService(context, InputMethodManager::class.java)
    this.setOnKeyListener { _, code, keyEvent ->
        if ((keyEvent.action == KeyEvent.ACTION_DOWN) && (code == KeyEvent.KEYCODE_ENTER)) {
            (inputMethodManager)?.hideSoftInputFromWindow(
                this.windowToken,
                0
            )
            onEnter()
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
