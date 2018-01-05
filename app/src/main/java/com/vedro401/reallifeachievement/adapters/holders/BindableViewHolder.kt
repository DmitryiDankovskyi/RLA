package com.vedro401.reallifeachievement.adapters.holders

import android.support.v7.widget.RecyclerView
import android.view.View

abstract class BindableViewHolder<in DT> (view : View): RecyclerView.ViewHolder(view) {

    abstract fun bind(data : DT)

}