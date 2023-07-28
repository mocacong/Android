package com.example.mocacong.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mocacong.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {
    private lateinit var binding : ActivityWebViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setWebView()
    }

    private fun setWebView() {
        val webView = binding.webView
        var uri =intent.getStringExtra("urlString")

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        if (uri != null) {
            webView.loadUrl(uri)
        }
    }
}