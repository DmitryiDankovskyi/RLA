package com.vedro401.reallifeachievement.ui.AddedAchievements

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.ViewGroup
import com.vedro401.reallifeachievement.App
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.adapters.RxRvAdapter
import com.vedro401.reallifeachievement.adapters.holders.MyAchievementHolder
import com.vedro401.reallifeachievement.managers.interfaces.DatabaseManager
import com.vedro401.reallifeachievement.managers.interfaces.UserManager
import com.vedro401.reallifeachievement.model.Achievement
import com.vedro401.reallifeachievement.ui.AddedAchievements.create.CreateActivity
import com.vedro401.reallifeachievement.ui.BaseActivity
import com.vedro401.reallifeachievement.ui.interfaces.FakeBottomNavigationOwner
import com.vedro401.reallifeachievement.utils.inflate
import kotlinx.android.synthetic.main.activity_added_achievements.*
import kotlinx.android.synthetic.main.layout_rv_container.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.startActivity
import rx.Subscription

class AddedAchievementsActivity : BaseActivity(), FakeBottomNavigationOwner {
    override var menuNum = 3

    var adapterSubscription : Subscription? = null
    val adapter = object : RxRvAdapter<Achievement, MyAchievementHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAchievementHolder =
                MyAchievementHolder(parent.inflate(R.layout.layout_my_achievement_item))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_added_achievements)
        initBottomNavigation(aa_bottom_navigation, this)

        aa_create_new.onClick {
            startActivity<CreateActivity>()
        }

        container_rv.layoutManager = LinearLayoutManager(this)
        container_rv.itemAnimator = DefaultItemAnimator()
        adapter.spinner = container_spinner
        adapter.emptinessIndicator = container_emptiness_indicator_block
        adapter.emptinessIndicatorTextView = container_emptiness_indicator_text
        adapter.warningView = container_warning_block
        adapter.warningTextView = container_warning_text
        container_rv.adapter = adapter

        um.isAuthorisedObs.subscribe{
            adapterSubscription = dbm.getMyAchievements().subscribe(adapter)
        }
    }
}
