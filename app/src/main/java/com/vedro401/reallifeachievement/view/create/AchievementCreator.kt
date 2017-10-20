package com.vedro401.reallifeachievement.view.create

import kotlin.collections.ArrayList

/**
 * Created by someone on 04.10.17.
 */
interface AchievementCreator {
    fun setMainData(title : String, shortDescription : String, fullDescription : String?)
    fun createAchievement()
    fun setTagList(tagList: ArrayList<String>)
}