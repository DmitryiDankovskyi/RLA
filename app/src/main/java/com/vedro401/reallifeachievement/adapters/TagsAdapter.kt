package com.vedro401.reallifeachievement.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.utils.inflate
import kotlinx.android.synthetic.main.layout_item_tag.view.*
import org.jetbrains.anko.onClick

class TagsAdapter : RecyclerView.Adapter<TagsAdapter.TagHolder>() {
    var tags  = ArrayList<String>()
    private set
    override fun onBindViewHolder(holder: TagHolder, position: Int) {
        holder.bind(tags[position],{
            tags.remove(holder.tag)
            notifyItemRemoved(holder.adapterPosition)})
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int)
            = TagHolder(parent!!.inflate(R.layout.layout_item_tag))

    override fun getItemCount() = tags.size

    class TagHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var tag: String
        fun bind(tag : String, delete: () -> Unit){
            this.tag = tag
            itemView.tv_tag.text = tag
            itemView.img_delete.onClick {
                delete.invoke()
            }

        }
    }

    fun add(tag: String){
        if(tags.contains(tag)){

            return
        }
        tags.add(tag)
        notifyItemInserted(tags.size-1)
    }

    fun isEmpty() = tags.isEmpty()


}

