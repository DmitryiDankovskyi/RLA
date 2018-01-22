package com.vedro401.reallifeachievement.config

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager


class AppPreference(val context: Context) {
    private val prefs: SharedPreferences
            = PreferenceManager.getDefaultSharedPreferences(context)

    private val AVATAR_LAST_UPDATE = "avatarLastUpdate"
    var avatarLastUpdate : String
        get() = prefs.getString(AVATAR_LAST_UPDATE, "")
        set(value) = prefs.edit().putString(AVATAR_LAST_UPDATE, value).apply()

}