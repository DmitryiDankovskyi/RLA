package com.vedro401.reallifeachievement.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import com.vedro401.reallifeachievement.App
import javax.inject.Inject

abstract class BindableViewHolder<in DT> (view : View): RecyclerView.ViewHolder(view) {

    abstract fun bind(data : DT)

}