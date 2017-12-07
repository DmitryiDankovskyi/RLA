package com.vedro401.reallifeachievement.adapters

import android.content.Context
import android.content.Intent
import android.view.View
import com.vedro401.reallifeachievement.App
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.model.Story
import com.vedro401.reallifeachievement.view.StoryEditorActivity
import kotlinx.android.synthetic.main.layout_story_item.view.*
import kotlinx.android.synthetic.main.layout_story_post.view.*
import org.jetbrains.anko.onClick
import javax.inject.Inject


class StoryHolder(itemVIew: View) : BindableViewHolder<Story>(itemVIew){

    @Inject
    lateinit var context : Context

    init {
        App.getComponent().inject(this)
    }

    override fun bind(data: Story) {
        itemView.story_author_name.text = data.authorName
        // TODO Author avatar
        itemView.story_achievement_title.text = data.achievementTitle
        // TODO Achievement image
        val status = when(data.status){
            Story.STARTED ->  context.getString(R.string.story_status_started)
            Story.IN_PROGRESS -> context.getString(R.string.story_status_in_progress)
            Story.FINISHED -> context.getString(R.string.story_status_finished)
            else -> "Broken"
        }
        if(data.lastPost != null){
            itemView.story_last_post.visibility = View.VISIBLE
            itemView.story_border3.visibility = View.VISIBLE
            itemView.story_post_content.text = data.lastPost!!.content
            itemView.story_post_title.text = data.lastPost!!.title
            itemView.story_post_time_stamped.text = data.lastPost!!.timeStamped
        } else {
            itemView.story_last_post.visibility = View.GONE
            itemView.story_border3.visibility = View.GONE
        }
        itemView.story_status.text = status
        itemView.story_change_btn.onClick {
            val intent = Intent(context, StoryEditorActivity::class.java )
            intent.putExtra("story", data)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }


    }


}