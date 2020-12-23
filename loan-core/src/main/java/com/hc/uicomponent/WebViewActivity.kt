package com.hc.uicomponent

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.common_title_bar.*
import kotlinx.android.synthetic.main.common_webview.*

class WebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.common_webview)
        initData()
    }

    private fun initData() {
        common_back?.visibility = View.VISIBLE
        val title = intent.getStringExtra("title")
        val link = intent.getStringExtra("link")
        if(link.isNullOrEmpty()){
            finish()
        }
        common_title?.text = title
        mWebView?.apply{
            loadUrl(link)
            webViewClient = MyWebViewClient()
            isHorizontalScrollBarEnabled = false
            isVerticalScrollBarEnabled = false
        }
        common_back?.setOnClickListener{
            finish()
        }
    }

    internal inner class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView,
            url: String?
        ): Boolean {
            url?.let {
                view.loadUrl(url)
            }
            return true
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
            mWebView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}