package org.engine.simulogic.android

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import org.engine.simulogic.R

class WebViewActivity: AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        val webView = findViewById<WebView>(R.id.myWebView)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
        intent.data?.toString()?.also { url->
            webView.loadUrl(url)
        }

    }
}
