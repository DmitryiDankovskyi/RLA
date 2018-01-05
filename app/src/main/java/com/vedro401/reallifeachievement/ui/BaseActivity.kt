package com.vedro401.reallifeachievement.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.vedro401.reallifeachievement.App


open class BaseActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.getComponent().inject(this)
    }

}