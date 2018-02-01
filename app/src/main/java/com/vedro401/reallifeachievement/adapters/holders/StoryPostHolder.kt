package com.vedro401.reallifeachievement.adapters.holders

import android.view.View
import com.vedro401.reallifeachievement.model.StoryPost
import com.vedro401.reallifeachievement.ui.interfaces.StoryEditor
import kotlinx.android.synthetic.main.layout_story_post.view.*
import kotlinx.android.synthetic.main.layout_story_post_item.view.*
import org.jetbrains.anko.onClick


class StoryPostHolder(itemView: View,
                      private var editor: StoryEditor)
    : BindableViewHolder<StoryPost>(itemView) {


    override fun bind(data: StoryPost) {
        itemView.story_post_content.text = data.content
        itemView.story_post_title.text = data.title
        itemView.story_post_time_stamped.text = data.timeStamped
        itemView.story_post_item_change.onClick {
            editor.updatePost(data)
        }
        itemView.story_post_item_delete.onClick {
            editor.deletePost(data)
        }
    }

}