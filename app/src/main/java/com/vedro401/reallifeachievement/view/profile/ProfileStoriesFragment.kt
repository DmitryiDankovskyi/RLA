package com.vedro401.reallifeachievement.view.profile

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vedro401.reallifeachievement.App
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.adapters.RxRvAdapter.RxRvAdapter
import com.vedro401.reallifeachievement.adapters.RxRvAdapter.RxRvTransferProtocol
import com.vedro401.reallifeachievement.adapters.StoryHolder
import com.vedro401.reallifeachievement.database.DatabaseManager
import com.vedro401.reallifeachievement.model.Story
import com.vedro401.reallifeachievement.utils.transferProtocols.UserTransferProtocol
import com.vedro401.reallifeachievement.utils.LOGTAG
import com.vedro401.reallifeachievement.utils.STORY
import com.vedro401.reallifeachievement.utils.UserManager
import com.vedro401.reallifeachievement.utils.inflate
import kotlinx.android.synthetic.main.layout_rv_container.*
import rx.Subscription
import javax.inject.Inject

class ProfileStoriesFragment : Fragment() {

    @Inject
    lateinit var userManager: UserManager

    @Inject
    lateinit var databaseManager: DatabaseManager

    var adapterSubscription : Subscription? = null

    val adapter = object : RxRvAdapter<Story, StoryHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryHolder {
            return StoryHolder(parent.inflate(R.layout.layout_story_item))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.layout_rv_container, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        App.getComponent().inject(this)
        container_rv.layoutManager = LinearLayoutManager(context)
        container_rv.itemAnimator = DefaultItemAnimator()
        container_rv.adapter = adapter
        initRV()
    }

    private fun initRV() {
        adapter.spinner = container_spinner
        adapter.emptinessIndicator = container_emptiness_indicator_block
        adapter.emptinessIndicatorTextView = container_emptiness_indicator_text
        adapter.warningView = container_warning_block
        adapter.warningTextView = container_warning_text
        userManager.userStatus.subscribe ({
            status ->
            Log.i(STORY,"ProfileStoriesFragment.initRV status $status" )
            when(status) {
                UserTransferProtocol.SIGN_IN -> {
                    Log.i(STORY, "ProfileStoriesFragment.initRV: set stories")
                    adapterSubscription = databaseManager.getStories().subscribe(adapter)
                }

                UserTransferProtocol.SIGN_OUT -> {
                    adapterSubscription?.unsubscribe()
                    adapter.onNext(RxRvTransferProtocol(RxRvTransferProtocol.RESET))
                    adapter.onNext(RxRvTransferProtocol(RxRvTransferProtocol.PERMISSION_DENIED))
                }
            }
        }, { t ->
            Log.e(STORY, "ProfileStoriesFragment: ${t.message}")
        })
    }

}