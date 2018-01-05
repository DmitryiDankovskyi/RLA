package com.vedro401.reallifeachievement.ui.create

import android.content.Context
import android.support.v4.app.Fragment

/**
 * Created by someone on 04.10.17.
 */
abstract class CreateFragment : Fragment() {
    protected lateinit var achCreator: AchievementCreator
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            achCreator = activity as AchievementCreator
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
    }
    abstract fun getData() : Boolean
    abstract fun cleanUp()
}