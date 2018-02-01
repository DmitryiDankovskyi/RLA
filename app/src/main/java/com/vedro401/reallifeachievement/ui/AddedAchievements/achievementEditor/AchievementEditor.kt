package com.vedro401.reallifeachievement.ui.AddedAchievements.achievementEditor

import android.net.Uri
import com.vedro401.reallifeachievement.model.Achievement
import kotlin.collections.ArrayList

/**
 * Created by someone on 04.10.17.
 */
interface AchievementEditor {
    var achievement: Achievement
    var pickPath: Uri?
    var oldTags: ArrayList<String>
    var newTags: ArrayList<String>
    fun saveAchievement()
}