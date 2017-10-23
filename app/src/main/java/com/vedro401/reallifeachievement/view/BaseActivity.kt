package com.vedro401.reallifeachievement.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.vedro401.reallifeachievement.App
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.view.interfaces.BottomNavigationOwner


open class BaseActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.getComponent().inject(this)
    }

}