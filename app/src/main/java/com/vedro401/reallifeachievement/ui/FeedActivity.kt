package com.vedro401.reallifeachievement.ui

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.ViewGroup
import com.google.firebase.database.*
import com.vedro401.reallifeachievement.App
import com.vedro401.reallifeachievement.FirebaseUtils.increment
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.adapters.RxRvAdapter
import com.vedro401.reallifeachievement.adapters.holders.ShortAchievementHolder
import com.vedro401.reallifeachievement.model.Achievement
import com.vedro401.reallifeachievement.ui.interfaces.FakeBottomNavigationOwner
import com.vedro401.reallifeachievement.utils.plusAssign
import kotlinx.android.synthetic.main.activity_feed.*
import kotlinx.android.synthetic.main.layout_rv_container.*
import org.jetbrains.anko.onClick

class FeedActivity : BaseActivity(),FakeBottomNavigationOwner {
    override var menuNum = 0

    val adapter = object : RxRvAdapter<Achievement, ShortAchievementHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShortAchievementHolder =
                ShortAchievementHolder(this@FeedActivity, parent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_feed)
        super.onCreate(savedInstanceState)
        initBottomNavigation(bottom_navigation,this)

        App.getComponent().inject(this)
        container_rv.layoutManager = LinearLayoutManager(this)
        container_rv.itemAnimator = DefaultItemAnimator()
        adapter.spinner = container_spinner
        adapter.emptinessIndicator = container_emptiness_indicator_block
        adapter.emptinessIndicatorTextView = container_emptiness_indicator_text
        adapter.warningView = container_warning_block
        adapter.warningTextView = container_warning_text
        container_rv.adapter = adapter
        subscriptions += dbm.getAchievements().subscribe(adapter)

    }
}
