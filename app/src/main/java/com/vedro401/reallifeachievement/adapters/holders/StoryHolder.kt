package com.vedro401.reallifeachievement.adapters.holders

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.view.View
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.config.GlideApp
import com.vedro401.reallifeachievement.model.Story
import com.vedro401.reallifeachievement.ui.StoryEditorActivity
import kotlinx.android.synthetic.main.layout_item_achievement.view.*
import kotlinx.android.synthetic.main.layout_story_item.view.*
import kotlinx.android.synthetic.main.layout_story_post.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.onClick


class StoryHolder(val context: Context, itemVIew: View) : BindableViewHolder<Story>(itemVIew){


    override fun bind(data: Story) {
        itemView.story_author_name.text = data.authorName
        // TODO Author avatar
        itemView.story_achievement_title.text = data.achievementTitle
        if(data.achievementImageUrl != null){
            GlideApp.with(context)
                    .load(data.achievementImageUrl)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.ic_image_75dp)
                    .error(R.drawable.nothing)
                    .into(itemView.story_achievement_image)
        } else {
            itemView.story_achievement_image.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.achievement))
        }
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

        itemView.story_delete_btn.onClick {

//            val dialog = AlertDialog.Builder(context)
//            dialog.setPositiveButton("Delete") { _, _ -> data.delete() }
//            dialog.setNegativeButton("No,wait..."){_,_ ->}
//            dialog.setMessage("Delete this story?")
//            dialog.show()

            context.alert {
                title("Delete this story?")
                positiveButton("Delete"){
                    data.delete()
                }
                negativeButton("No, wait...")
            }.show()
        }


    }


}