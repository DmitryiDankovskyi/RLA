package com.vedro401.reallifeachievement.adapters.holders

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.View
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.config.GlideApp
import com.vedro401.reallifeachievement.model.Achievement
import com.vedro401.reallifeachievement.ui.AddedAchievements.achievementEditor.AchievementEditorActivity
import com.vedro401.reallifeachievement.utils.coolBigNumbers
import kotlinx.android.synthetic.main.layout_my_achievement_item.view.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.onClick

class MyAchievementHolder(itemView: View, var context: Context) : BindableViewHolder<Achievement>(itemView){


    override fun bind(data: Achievement) {
        if(data.imageUrl != null) {
            GlideApp.with(context)
                    .load(data.imageUrl)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.ic_image_75dp)
                    .error(R.drawable.nothing)
                    .into(itemView.mai_pick)

        } else {
            itemView.mai_pick.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.achievement))
        }
        itemView.mai_title.text = data.title
        itemView.mai_likes.text = coolBigNumbers(data.likes)
        itemView.mai_unlocks.text = data.unlocked.toString()
        itemView.mai_difficulty.text = if (data.unlocked == 0) "???"
            else (data.difficulty / data.unlocked).toString()
        itemView.mai_change.onClick {
            context.startActivity(
                    context.intentFor<AchievementEditorActivity>("achievement" to data)
                            .newTask())
        }
    }
}