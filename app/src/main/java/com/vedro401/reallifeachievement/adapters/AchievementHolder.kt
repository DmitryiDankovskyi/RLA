package com.vedro401.reallifeachievement.adapters

import android.content.Context
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import com.vedro401.reallifeachievement.App
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.model.Achievement
import com.vedro401.reallifeachievement.utils.coolBigNumbers
import kotlinx.android.synthetic.main.layout_achievement_item.view.*
import org.jetbrains.anko.onClick
import javax.inject.Inject

class AchievementHolder(itemView: View) : BindableViewHolder<Achievement>(itemView)  {

    @Inject
    lateinit var context: Context
    private var flag = false

    init {
        App.getComponent().inject(this)
    }

    override fun bind(achievement: Achievement) {
        itemView.story_achievement_title.text = achievement.title
        itemView.achievement_description_short.text = achievement.shortDescription

        itemView.likes.text = coolBigNumbers(achievement.likes.toString())
        itemView.unlocked.text = achievement.unlocked.toString()
        itemView.difficulty.text = achievement.difficulty.toString()


        Log.d("BIND_D", achievement.toString())
        if (achievement.fullDescription == null || achievement.fullDescription == "") {
            itemView.txtbtn_see_more.text = ""
        } else {
            itemView.txtbtn_see_more.text = context.resources.getString(R.string.see_more)
            itemView.txtbtn_see_more.onClick {
                if (!flag) {
                    itemView.achievement_description_short.text = achievement.fullDescription
                    itemView.txtbtn_see_more.text = context.resources.getString(R.string.hide)
                    flag = true
                } else {
                    itemView.achievement_description_short.text = achievement.shortDescription
                    itemView.txtbtn_see_more.text = context.resources.getString(R.string.see_more)
                    flag = false
                }
            }
        }

        itemView.btn_like.onClick {
            achievement.like()
            val anim = AnimationUtils.loadAnimation(context, R.anim.like)
            itemView.btn_like.startAnimation(anim)

        }

        itemView.btn_pin.onClick {
            achievement.createStore()
            val anim = AnimationUtils.loadAnimation(context, R.anim.like)
            itemView.btn_pin.startAnimation(anim)
//            itemView.btn_pin.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary))
        }

        itemView.btn_unlock.onClick {
            achievement.unlock()
            val anim = AnimationUtils.loadAnimation(context, R.anim.like)
            itemView.btn_unlock.startAnimation(anim)
//            itemView.btn_unlock.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary))
        }
    }
}