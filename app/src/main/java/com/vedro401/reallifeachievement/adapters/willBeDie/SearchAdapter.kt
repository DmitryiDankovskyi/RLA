package com.vedro401.reallifeachievement.adapters.willBeDie

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.adapters.AchievementHolder
import com.vedro401.reallifeachievement.model.Achievement
import org.jetbrains.anko.layoutInflater

class SearchAdapter: RecyclerView.Adapter<AchievementHolder>() {

    private val list = listOf(Achievement("Achievement 1","Achievement 1 shortDescription","shortDescription 1", ""),
                      Achievement("Achievement 2","Achievement 2 shortDescription","shortDescription 2",""),
                      Achievement("Rely long title for achievement","Achievement 3 shortDescription","shortDescription 3",""),
                      Achievement("Achievement 4","Achievement 4 shortDescription","shortDescription 4",""),
                      Achievement("Achievement 5","Achievement 5 shortDescription","shortDescription 5",""))

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: AchievementHolder, position: Int) = holder.bind(list[position])

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): AchievementHolder
            = AchievementHolder(parent!!.context.layoutInflater.
            inflate(R.layout.layout_achievement_item, parent, false))
    



}