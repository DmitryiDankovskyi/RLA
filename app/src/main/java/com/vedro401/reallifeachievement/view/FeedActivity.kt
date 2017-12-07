package com.vedro401.reallifeachievement.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.ViewGroup
import com.vedro401.reallifeachievement.App
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.adapters.AchievementHolder
import com.vedro401.reallifeachievement.adapters.RxRvAdapter.RxRvAdapter
import com.vedro401.reallifeachievement.database.DatabaseManager
import com.vedro401.reallifeachievement.model.Achievement
import com.vedro401.reallifeachievement.utils.transferProtocols.UserTransferProtocol
import com.vedro401.reallifeachievement.utils.UserManager
import com.vedro401.reallifeachievement.utils.inflate
import com.vedro401.reallifeachievement.view.interfaces.FakeBottomNavigationOwner
import kotlinx.android.synthetic.main.activity_feed.*
import kotlinx.android.synthetic.main.layout_rv_container.*
import rx.Subscription
import javax.inject.Inject

class FeedActivity : AppCompatActivity(),FakeBottomNavigationOwner {
    override var menuNum = 0

    @Inject
    lateinit var dbm : DatabaseManager

    @Inject
    lateinit var um : UserManager

    val adapter = object : RxRvAdapter<Achievement, AchievementHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementHolder {
            return AchievementHolder(parent.inflate(R.layout.layout_achievement_item))
        }
    }


    var adapterSubscription : Subscription? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_feed)
        super.onCreate(savedInstanceState)
        initBottomNavigation(bottom_navigation,this)

        App.getComponent().inject(this)
        container_rv.layoutManager = LinearLayoutManager(this)
        container_rv.itemAnimator = DefaultItemAnimator()
//        adapterSubscription = dbm.getAchievements().subscribe(adapter)
        adapter.spinner = container_spinner
        adapter.emptinessIndicator = container_emptiness_indicator_block
        adapter.emptinessIndicatorTextView = container_emptiness_indicator_text
        adapter.warningView = container_warning_block
        adapter.warningTextView = container_warning_text
        container_rv.adapter = adapter

        um.userStatus.subscribe{
            status ->
            if(status == UserTransferProtocol.SIGN_IN || status == UserTransferProtocol.SIGN_OUT){
                adapterSubscription = dbm.getAchievements().subscribe(adapter)
            }
        }
    }


}
