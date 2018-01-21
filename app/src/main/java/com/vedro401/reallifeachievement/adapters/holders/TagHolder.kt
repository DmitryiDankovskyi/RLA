package com.vedro401.reallifeachievement.adapters.holders

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.layout_item_tag.view.*
import org.jetbrains.anko.onClick

class TagHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(tag : String, buttonOnClick: ((position: Int, tag: String) -> Unit)? = null,
             onCLick: ((position: Int, tag: String) -> Unit)? = null){
        itemView.tag_text.text = tag
        if(buttonOnClick != null) {
            itemView.tag_btn.onClick {
                buttonOnClick.invoke(adapterPosition,tag)
            }
        }
        if(onCLick != null){
            itemView.onClick {
                onCLick.invoke(adapterPosition,tag)
            }
        }
    }
}