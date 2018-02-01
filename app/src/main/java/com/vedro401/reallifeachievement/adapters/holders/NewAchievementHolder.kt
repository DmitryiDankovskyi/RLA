package com.vedro401.reallifeachievement.adapters.holders

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.config.GlideApp
import com.vedro401.reallifeachievement.model.Achievement
import com.vedro401.reallifeachievement.utils.coolBigNumbers
import com.vedro401.reallifeachievement.utils.wordDifficulty
import kotlinx.android.synthetic.main.item_achievement.view.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast
import rx.Subscription


class NewAchievementHolder(var context: Context, itemView: View) : BindableViewHolder<Achievement>(itemView) {

    private var flag = false

    private var subscriptionLike: Subscription? = null
    private var subscriptionList: Subscription? = null

    override fun bind(data: Achievement) {
        if (data.imageUrl != null) {
            GlideApp.with(context)
                    .load(data.imageUrl)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.ic_image_75dp)
                    .error(R.drawable.nothing)
                    .into(itemView.ia_achievement_pick)

        } else {
             itemView.ia_achievement_pick.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.achievement))
        }
        itemView.ia_achievement_title.text = data.title
        itemView.ia_description.text = data.description
        itemView.ia_likes_indicator.text = coolBigNumbers(data.likes)
        itemView.ia_unlocked_indicator.text = data.unlocked.toString()
        if(data.unlocked==0){
            itemView.ia_difficulty_text.text = "???"
        } else {
            val difficulty = data.difficulty / data.unlocked
            itemView.ia_difficulty_text.text = wordDifficulty(difficulty)
            itemView.ia_difficulty_ratingbar.rating = difficulty/20f
        }
        itemView.ia_author_name.text = data.authorName
        itemView.ia_btn_like.isEnabled = false
        itemView.ia_btn_pin.isEnabled = false
        subscriptionLike?.unsubscribe()
        subscriptionLike = data.isLikedByCurrentUserObs.subscribe { isLiked ->
            setColor(itemView.ia_btn_like, isLiked)
            itemView.ia_btn_like.isEnabled = true
        }
        subscriptionList?.unsubscribe()
        subscriptionList = data.isInListObs.subscribe { isInList ->
            Log.d("BIND_D", data.title + " " + isInList)
            setColor(itemView.ia_btn_pin, isInList)
            itemView.ia_btn_pin.isEnabled = true
        }

        itemView.ia_description.onClick {
            if (!flag) {
                itemView.ia_description.maxLines = Int.MAX_VALUE
                flag = true
            } else {
                itemView.ia_description.maxLines =
                        context.resources.getInteger(R.integer.shortTextMaxLines)
                flag = false
            }
        }

        itemView.ia_btn_like.onClick {
            data.isLikedByCurrentUserObs.onNext(!data.isLikedByCurrentUserObs.value)
//            setColor(itemView.btn_like, !data.isLikedByCurrentUserObs.value)
            data.like()
            val anim = AnimationUtils.loadAnimation(context, R.anim.like)
            itemView.ia_btn_like.startAnimation(anim)

        }

        itemView.ia_btn_pin.onClick {
            if (data.isInListObs.value) {
                context.toast(context.getString(R.string.already_in_list_alert))
                return@onClick
            }
            data.createStore()
            val anim = AnimationUtils.loadAnimation(context, R.anim.like)
            itemView.ia_btn_pin.startAnimation(anim)
//            itemView.btn_pin.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary))
        }

        itemView.ia_btn_unlock.onClick {
            context.toast("Do nothing yet")
            val anim = AnimationUtils.loadAnimation(context, R.anim.like)
            itemView.ia_btn_unlock.startAnimation(anim)
//            itemView.btn_unlock.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary))
        }
    }

    private fun setColor(view: ImageView, b: Boolean) {
        if (b) {
            view.setColorFilter(ContextCompat.getColor(context, R.color.drawablePrimaryColor),
                    android.graphics.PorterDuff.Mode.SRC_IN)
        } else {
            view.setColorFilter(ContextCompat.getColor(context, R.color.drawableSecondaryColor),
                    android.graphics.PorterDuff.Mode.SRC_IN)
        }
    }
}
