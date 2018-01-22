package com.vedro401.reallifeachievement.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.vedro401.reallifeachievement.App
import com.vedro401.reallifeachievement.config.AppPreference
import com.vedro401.reallifeachievement.managers.StorageManager
import com.vedro401.reallifeachievement.managers.interfaces.DatabaseManager
import com.vedro401.reallifeachievement.managers.interfaces.UserManager
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

open class BaseFragment : Fragment() {
    @Inject
    lateinit var dbm : DatabaseManager

    @Inject
    lateinit var um : UserManager

    @Inject
    lateinit var sm: StorageManager

    @Inject
    lateinit var pref: AppPreference

    val subscriptions = CompositeSubscription()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.getComponent().inject(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriptions.clear()
    }
}