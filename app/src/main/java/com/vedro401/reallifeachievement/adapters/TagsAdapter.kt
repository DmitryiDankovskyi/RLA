package com.vedro401.reallifeachievement.adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.adapters.holders.TagHolder
import com.vedro401.reallifeachievement.utils.inflate
import kotlinx.android.synthetic.main.layout_item_tag.view.*
import org.jetbrains.anko.onClick

class TagsAdapter(val btnIconId: Int? = null) : RecyclerView.Adapter<TagHolder>() {
    var tags  = ArrayList<String>()
    private set

    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    var buttonOnClick: ((position: Int, tag: String) -> Unit)? = null
    var onCLick: ((position: Int, tag: String) -> Unit)? = null

    override fun onBindViewHolder(holder: TagHolder, position: Int) {
        holder.bind(tags[position],
                buttonOnClick,
                onCLick)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TagHolder {
        val th =  TagHolder(parent!!.inflate(R.layout.layout_item_tag))
        if(btnIconId != null) {
            th.itemView.tag_btn.setImageResource(btnIconId)
            th.itemView.tag_btn.visibility = View.VISIBLE
        }
        return  th
    }

    override fun getItemCount() = tags.size

    fun add(tag: String){
        if(tags.contains(tag)){

            return
        }
        tags.add(tag)
        notifyItemInserted(tags.size-1)
    }

    fun isEmpty() = tags.isEmpty()


}

