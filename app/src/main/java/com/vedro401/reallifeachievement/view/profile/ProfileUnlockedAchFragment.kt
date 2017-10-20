package com.vedro401.reallifeachievement.view.profile

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.adapters.willBeDie.AchFirebaseAdapterIndexed
import kotlinx.android.synthetic.main.layout_rv_container.*

class ProfileUnlockedAchFragment : Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
            = inflater.inflate(R.layout.layout_rv_container, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    fun initRV(){
        val adapter = AchFirebaseAdapterIndexed()
        adapter.spinner = container_spinner
        adapter.voidContentIndicator = container_emptiness_indicator_block
        container_rv.adapter = adapter
        container_rv.layoutManager = LinearLayoutManager(context)
        container_rv.itemAnimator = DefaultItemAnimator()
    }

}