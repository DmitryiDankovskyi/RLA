package com.vedro401.reallifeachievement.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.vedro401.reallifeachievement.App
import com.vedro401.reallifeachievement.managers.interfaces.DatabaseManager
import com.vedro401.reallifeachievement.managers.interfaces.UserManager
import javax.inject.Inject


open class BaseActivity : AppCompatActivity(){
    @Inject
    lateinit var dbm : DatabaseManager

    @Inject
    lateinit var um : UserManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.getComponent().inject(this)
    }

}