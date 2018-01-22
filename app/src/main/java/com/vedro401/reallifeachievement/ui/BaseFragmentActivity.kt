package com.vedro401.reallifeachievement.ui

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.vedro401.reallifeachievement.App
import com.vedro401.reallifeachievement.managers.StorageManager
import com.vedro401.reallifeachievement.managers.interfaces.DatabaseManager
import com.vedro401.reallifeachievement.managers.interfaces.UserManager
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

/**
 * Created by someone on 22.01.18.
 */
open class BaseFragmentActivity : FragmentActivity(){

    @Inject
    lateinit var dbm : DatabaseManager

    @Inject
    lateinit var um : UserManager

    @Inject
    lateinit var sm: StorageManager

    val subscriptions = CompositeSubscription()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.getComponent().inject(this)
    }

    override fun onDestroy() {
        subscriptions.clear()
        super.onDestroy()
    }

}