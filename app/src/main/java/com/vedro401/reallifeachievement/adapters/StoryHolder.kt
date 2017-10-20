package com.vedro401.reallifeachievement.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import com.vedro401.reallifeachievement.App
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.model.Story
import com.vedro401.reallifeachievement.utils.STORY
import com.vedro401.reallifeachievement.view.StoryWritingActivity
import kotlinx.android.synthetic.main.layout_story_item.view.*
import kotlinx.android.synthetic.main.layout_story_post.view.*
import org.jetbrains.anko.onClick
import javax.inject.Inject

/**
 * Created by someone on 15.10.17.
 */
class StoryHolder(itemVIew: View) : BindableViewHolder<Story>(itemVIew){

    @Inject
    lateinit var context : Context

    init {
        App.getComponent().inject(this)
    }

    override fun bind(story: Story) {
        itemView.story_author_name.text = story.authorName
        // TODO Author avatar
        itemView.story_achievement_title.text = story.achievementTitle
        // TODO Achievement image
        val status = when(story.status){
            Story.STARTED ->  context.getString(R.string.story_status_started)
            Story.IN_PROGRESS -> context.getString(R.string.story_status_in_progress)
            Story.FINISHED -> context.getString(R.string.story_status_finished)
            else -> "Broken"
        }
        if(story.status == Story.STARTED){
            itemView.story_last_post.visibility = View.GONE
            itemView.story_border3.visibility = View.GONE
        } else {
            itemView.story_last_post.visibility = View.VISIBLE
            itemView.story_border3.visibility = View.VISIBLE
            itemView.story_post_content.text = story.lastPost!!.content
        }
        itemView.story_status.text = status
        itemView.story_change_btn.onClick {
            val intent = Intent(context, StoryWritingActivity::class.java )
            intent.putExtra("story", story)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }


    }


}