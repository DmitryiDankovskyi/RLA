package com.vedro401.reallifeachievement.adapters

import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup

class NoFragmentPagerAdapter(private var views: Array<View>, private var titles: Array<String>) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup?, position: Int): Any {

        container!!.addView(views[position])
        return views[position]
        }

    override fun getPageTitle(position: Int): CharSequence {
        return if( titles.size > position) titles[position] else "Untitled"
    }

    override fun isViewFromObject(view: View?, `object`: Any?): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return views.size
    }

    override fun destroyItem(container: ViewGroup?, position: Int, view: Any?) {
        container!!.removeView(view as View)
    }
}