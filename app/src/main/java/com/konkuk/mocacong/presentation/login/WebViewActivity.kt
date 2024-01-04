package com.konkuk.mocacong.presentation.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.konkuk.mocacong.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setWebView()
    }

    private fun setWebView() {
        val webView = binding.webView
        val uri = intent.getStringExtra("urlString")

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        if (uri != null) {
            webView.loadUrl(uri)
        }
    }
}