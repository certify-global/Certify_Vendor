package com.certify.vendor.activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.webkit.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.certify.vendor.R
import com.certify.vendor.common.Utils

class WebViewActivity : AppCompatActivity() {

    private val TAG: String = WebViewActivity::class.java.name
    private var webViewUrl: String? = null
    private var imgBack: ImageView? = null
    private var dialog: Dialog? = null
    private lateinit var mWebview: WebView
    @Suppress("UNUSED_PARAMETER")
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
            val intent: Intent = getIntent()
            webViewUrl = intent.getStringExtra("url")
            initView()
            imgBack!!.setOnClickListener { finish() }
    }

    override fun onStart() {
        super.onStart()
        loadWebView()
    }

    private fun loadWebView() {
        try {
            mWebview.webViewClient = WebViewClient()
            mWebview.loadUrl(webViewUrl!!)
            mWebview.settings.javaScriptEnabled = true
            mWebview.settings.setSupportZoom(true)
        } catch (e: Exception) {
            Log.e(TAG, e.message!!)
        }
    }

    private fun initView() {
        mWebview = findViewById(R.id.webview)
        imgBack = findViewById(R.id.img_back)
    }

}