package com.vedro401.reallifeachievement.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.ViewGroup
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.vedro401.reallifeachievement.App
import com.vedro401.reallifeachievement.FirebaseUtils.ACHIEVEMENTS_MAIN_DATA
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.adapters.holders.AchievementHolder
import com.vedro401.reallifeachievement.adapters.RxRvAdapter
import com.vedro401.reallifeachievement.managers.interfaces.DatabaseManager
import com.vedro401.reallifeachievement.model.Achievement
import com.vedro401.reallifeachievement.managers.interfaces.UserManager
import com.vedro401.reallifeachievement.utils.inflate
import com.vedro401.reallifeachievement.ui.interfaces.FakeBottomNavigationOwner
import kotlinx.android.synthetic.main.activity_feed.*
import kotlinx.android.synthetic.main.layout_rv_container.*
import org.jetbrains.anko.onClick
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
            return AchievementHolder(parent.inflate(R.layout.layout_item_achievement))
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
        adapter.spinner = container_spinner
        adapter.emptinessIndicator = container_emptiness_indicator_block
        adapter.emptinessIndicatorTextView = container_emptiness_indicator_text
        adapter.warningView = container_warning_block
        adapter.warningTextView = container_warning_text
        container_rv.adapter = adapter

        um.isAuthorisedObs.subscribe{
                adapterSubscription = dbm.getAchievements().subscribe(adapter)
        }
    }
}
