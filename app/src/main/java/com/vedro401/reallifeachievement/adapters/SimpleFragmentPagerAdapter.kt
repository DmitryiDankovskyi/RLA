package com.vedro401.reallifeachievement.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import java.util.*
import kotlin.collections.ArrayList


class SimpleFragmentPagerAdapter(var fragments: ArrayList<Fragment>,
                                 fm : FragmentManager,
                                 var titles : ArrayList<String>? = null): FragmentPagerAdapter(fm) {
    override fun getItem(position: Int) = fragments[position]
    override fun getCount() = fragments.size
    override fun getPageTitle(position: Int): CharSequence {
        return if(titles != null && titles!!.size > position)  titles!![position]
               else "No title"
    }
}