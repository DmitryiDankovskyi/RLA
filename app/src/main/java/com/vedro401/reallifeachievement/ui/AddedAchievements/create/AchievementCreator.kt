package com.vedro401.reallifeachievement.ui.AddedAchievements.create

import android.net.Uri
import kotlin.collections.ArrayList

/**
 * Created by someone on 04.10.17.
 */
interface AchievementCreator {
    fun setMainData(title : String,
                    shortDescription : String)
    fun createAchievement()
    fun setTagList(tagList: ArrayList<String>)
}