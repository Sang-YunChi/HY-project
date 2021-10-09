package com.example.knurling_dietinbody

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebSettings
import kotlinx.android.synthetic.main.activity_privacy.*

class PrivacyActivity : AppCompatActivity() {
    lateinit var webSetting : WebSettings
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy)
        back_btn.setOnClickListener {
            finish()
        }
        web_view.webViewClient
        webSetting = web_view.settings
        webSetting.javaScriptEnabled
        web_view.loadUrl("file:///android_asset/MyCafeDiary_Privacy.html")

    }
}