package com.vedro401.reallifeachievement.view.interfaces

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.view.FeedActivity
import com.vedro401.reallifeachievement.view.SearchActivity
import com.vedro401.reallifeachievement.view.create.CreateActivity
import com.vedro401.reallifeachievement.view.profile.ProfileActivity


interface BottomNavigationOwner {
    var menuNum : Int
    fun initBottomNavigations(bottomNavigation: BottomNavigationViewEx, context: Context) {
        bottomNavigation.enableItemShiftingMode(false)
        bottomNavigation.enableShiftingMode(false)
        bottomNavigation.setTextVisibility(false)
//        bottomNavigation.menu.getItem(menuNum).isChecked = true
        val option = ActivityOptions.makeCustomAnimation(context, R.anim.fade_in, R.anim.fade_out)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            var mn : Int
            var activity = when (item.itemId) {
                R.id.feed -> {
                    mn = 0
                    FeedActivity::class.java
                }
                R.id.search -> {
                    mn = 1
                    SearchActivity::class.java }
                R.id.create -> {
                    mn = 2
                    CreateActivity::class.java
                }
                R.id.profile -> {
                    mn = 3
                    ProfileActivity::class.java
                }
                else -> throw IllegalArgumentException()
            }
            bottomNavigation.menu.getItem(mn).isChecked = true
            context.startActivity(Intent(context, activity)
                    .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT), option.toBundle())
            true
        }
    }
}
