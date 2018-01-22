package com.vedro401.reallifeachievement.ui.interfaces

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatImageView
import android.view.MotionEvent
import android.view.View
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.ui.AddedAchievements.AddedAchievementsActivity
import com.vedro401.reallifeachievement.ui.FeedActivity
import com.vedro401.reallifeachievement.ui.SearchActivity
import com.vedro401.reallifeachievement.ui.AddedAchievements.create.CreateActivity
import com.vedro401.reallifeachievement.ui.profile.ProfileActivity
import kotlinx.android.synthetic.main.layout_bottom_navigation.view.*

interface FakeBottomNavigationOwner {
    var menuNum: Int
    fun initBottomNavigation(bottomNavigation: View, context: Context) {
        when (menuNum) {
            0 -> bottomNavigation.bottom_navigation_feed
            1 -> bottomNavigation.bottom_navigation_search
            2 -> bottomNavigation.bottom_navigation_profile
            3 -> bottomNavigation.bottom_navigation_create
            else -> throw IndexOutOfBoundsException("Index must be < 3 and > 0. Your index is $menuNum")
        }.setColorFilter(ContextCompat.getColor(context, R.color.white))
        val option = ActivityOptions.makeCustomAnimation(context, R.anim.fade_in, R.anim.fade_out)
        val onTouchListener = View.OnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    (v as AppCompatImageView).setColorFilter(ContextCompat.getColor(context, R.color.white))
                    return@OnTouchListener true
                }
                MotionEvent.ACTION_UP -> {
                    val activity: Class<*> = when (v.id) {
                        R.id.bottom_navigation_feed -> {
                            if(menuNum == 0) return@OnTouchListener false
                            FeedActivity::class.java
                        }
                        R.id.bottom_navigation_search -> {
                            if(menuNum == 1) return@OnTouchListener false
                            SearchActivity::class.java
                        }
                        R.id.bottom_navigation_profile -> {
                            if(menuNum == 2) return@OnTouchListener false
                            ProfileActivity::class.java
                        }
                        R.id.bottom_navigation_create -> {
                            if(menuNum == 3) return@OnTouchListener false
                            AddedAchievementsActivity::class.java
                        }
                        else -> throw IllegalArgumentException()
                    }
                    context.startActivity(Intent(context, activity)
                            .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT), option.toBundle())
                    (v as AppCompatImageView).setColorFilter(ContextCompat.getColor(context, R.color.drawablePrimaryDarkColor))
                    return@OnTouchListener true
                }
                else -> return@OnTouchListener false
            }
        }

            for (v: View in arrayOf<View>(
                    bottomNavigation.bottom_navigation_feed,
                    bottomNavigation.bottom_navigation_search,
                    bottomNavigation.bottom_navigation_profile,
                    bottomNavigation.bottom_navigation_create)) {
                v.setOnTouchListener(onTouchListener)
            }

        }

    }