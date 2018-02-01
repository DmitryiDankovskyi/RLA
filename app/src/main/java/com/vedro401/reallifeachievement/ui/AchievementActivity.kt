package com.vedro401.reallifeachievement.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.config.GlideApp
import com.vedro401.reallifeachievement.model.Achievement
import com.vedro401.reallifeachievement.utils.coolBigNumbers
import com.vedro401.reallifeachievement.utils.wordDifficulty
import kotlinx.android.synthetic.main.activity_achievement.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast
import rx.Subscription

class AchievementActivity : AppCompatActivity() {
    private var flag = false

    private var subscriptionLike: Subscription? = null
    private var subscriptionList: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievement)

        val achievement : Achievement = intent.getParcelableExtra("achievement")
        if (achievement.imageUrl != null) {
            GlideApp.with(this)
                    .load(achievement.imageUrl)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.ic_image_75dp)
                    .error(R.drawable.nothing)
                    .into(aa_achievement_pick)

        } else {
            aa_achievement_pick.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.achievement))
        }
        aa_achievement_title.text = achievement.title
        aa_description.text = achievement.description
        aa_likes_indicator.text = coolBigNumbers(achievement.likes)
        aa_unlocked_indicator.text = achievement.unlocked.toString()
        if(achievement.unlocked==0){
            aa_difficulty_text.text = "???"
        } else {
            val difficulty = achievement.difficulty / achievement.unlocked
            aa_difficulty_text.text = wordDifficulty(difficulty)
            aa_difficulty_ratingbar.rating = difficulty/20f
        }
        aa_author_name.text = achievement.authorName
        aa_btn_like.isEnabled = false
        aa_btn_pin.isEnabled = false
        subscriptionLike?.unsubscribe()
        subscriptionLike = achievement.isLikedByCurrentUserObs.subscribe { isLiked ->
            setColor(aa_btn_like, isLiked)
            aa_btn_like.isEnabled = true
        }
        subscriptionList?.unsubscribe()
        subscriptionList = achievement.isInListObs.subscribe { isInList ->
            Log.d("BIND_D", achievement.title + " " + isInList)
            setColor(aa_btn_pin, isInList)
            aa_btn_pin.isEnabled = true
        }

        aa_description.onClick {
            if (!flag) {
                aa_description.maxLines = Int.MAX_VALUE
                flag = true
            } else {
                aa_description.maxLines =
                        resources.getInteger(R.integer.shortTextMaxLines)
                flag = false
            }
        }

        aa_btn_like.onClick {
            achievement.isLikedByCurrentUserObs.onNext(!achievement.isLikedByCurrentUserObs.value)
//            setColor(itemView.btn_like, !achievement.isLikedByCurrentUserObs.value)
            achievement.like()
            val anim = AnimationUtils.loadAnimation(this, R.anim.like)
            aa_btn_like.startAnimation(anim)

        }

        aa_btn_pin.onClick {
            if (achievement.isInListObs.value) {
                toast(getString(R.string.already_in_list_alert))
                return@onClick
            }
            achievement.createStore()
            val anim = AnimationUtils.loadAnimation(this, R.anim.like)
            aa_btn_pin.startAnimation(anim)
//            itemView.btn_pin.setColorFilter(thisCompat.getColor(this, R.color.colorPrimary))
        }

        aa_btn_unlock.onClick {
            toast("Do nothing yet")
            val anim = AnimationUtils.loadAnimation(this, R.anim.like)
            aa_btn_unlock.startAnimation(anim)
//            itemView.btn_unlock.setColorFilter(thisCompat.getColor(this, R.color.colorPrimary))
        }
    }

    private fun setColor(view: ImageView, b: Boolean) {
        if (b) {
            view.setColorFilter(ContextCompat.getColor(this, R.color.drawablePrimaryColor),
                    android.graphics.PorterDuff.Mode.SRC_IN)
        } else {
            view.setColorFilter(ContextCompat.getColor(this, R.color.drawableSecondaryColor),
                    android.graphics.PorterDuff.Mode.SRC_IN)
        }
    }
}
