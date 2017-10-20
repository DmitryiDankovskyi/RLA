package com.vedro401.reallifeachievement.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.vedro401.reallifeachievement.view.FeedActivity

import org.jetbrains.anko.startActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity<FeedActivity>()
        finish()
    }
}
