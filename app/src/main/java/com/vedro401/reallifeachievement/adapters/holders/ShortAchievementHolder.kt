package com.vedro401.reallifeachievement.adapters.holders

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.ViewGroup
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.config.GlideApp
import com.vedro401.reallifeachievement.model.Achievement
import com.vedro401.reallifeachievement.ui.AchievementActivity
import com.vedro401.reallifeachievement.utils.inflate
import kotlinx.android.synthetic.main.item_achievement_short.view.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.onClick

class ShortAchievementHolder(var context: Context, parent: ViewGroup)
    : BindableViewHolder<Achievement>(parent.inflate(R.layout.item_achievement_short)) {
    override fun bind(data: Achievement) {
        if (data.imageUrl != null) {
            GlideApp.with(context)
                    .load(data.imageUrl)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.ic_image_75dp)
                    .error(R.drawable.nothing)
                    .into(itemView.ias_achievement_pick)

        } else {
            itemView.ias_achievement_pick.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.achievement))
        }
        itemView.ias_title.text = data.title
        itemView.ias_description.text = data.description
        itemView.onClick {
            context.startActivity(
                    context.intentFor<AchievementActivity>("achievement" to data)
                            .newTask())
        }
    }
}