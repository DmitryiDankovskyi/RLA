package com.vedro401.reallifeachievement.ui.AddedAchievements.achievementEditor

import android.content.Context
import android.support.v4.app.Fragment


abstract class AchievementEditorFragment : Fragment() {
    protected lateinit var achEditorActivity: AchievementEditor
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            achEditorActivity = activity as AchievementEditor
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement AchievementEditor");
        }
    }
    abstract fun saveData() : Boolean
    abstract fun cleanUp()
}