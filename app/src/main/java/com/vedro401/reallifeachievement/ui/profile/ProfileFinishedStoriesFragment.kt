package com.vedro401.reallifeachievement.ui.profile

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.adapters.RxRvAdapter
import com.vedro401.reallifeachievement.adapters.holders.StoryHolder
import com.vedro401.reallifeachievement.model.Story
import com.vedro401.reallifeachievement.ui.BaseFragment
import com.vedro401.reallifeachievement.utils.inflate
import com.vedro401.reallifeachievement.utils.plusAssign
import kotlinx.android.synthetic.main.layout_rv_container.*

class ProfileFinishedStoriesFragment : BaseFragment() {

    val adapter = object : RxRvAdapter<Story, StoryHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryHolder =
                StoryHolder(this@ProfileFinishedStoriesFragment.context!!, parent.inflate(R.layout.layout_story_item))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.layout_rv_container, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        subscriptions += dbm.getFinishedStories().subscribe(adapter)
    }

}