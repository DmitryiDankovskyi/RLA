package com.vedro401.reallifeachievement.adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.vedro401.reallifeachievement.App
import com.vedro401.reallifeachievement.R
import com.vedro401.reallifeachievement.model.Achievement
import com.vedro401.reallifeachievement.utils.coolBigNumbers
import kotlinx.android.synthetic.main.layout_achievement_item.view.*
import org.jetbrains.anko.onClick
import org.jetbrains.anko.toast
import rx.Subscription
import javax.inject.Inject

class AchievementHolder(itemView: View) : BindableViewHolder<Achievement>(itemView) {

    @Inject
    lateinit var context: Context
    private var flag = false

    private var subscriptionLike: Subscription? = null
    private var subscriptionList: Subscription? = null

    init {
        App.getComponent().inject(this)
    }

    override fun bind(data: Achievement) {
        itemView.story_achievement_title.text = data.title
        itemView.achievement_description_short.text = data.shortDescription
        itemView.likes.text = coolBigNumbers(data.likes.toString())
        itemView.unlocked.text = data.unlocked.toString()
        itemView.difficulty.text = if (data.unlocked == 0) "???" else (data.difficulty / data.unlocked).toString()

        itemView.btn_like.isEnabled = false
        itemView.btn_pin.isEnabled = false
        subscriptionLike?.unsubscribe()
        subscriptionLike = data.isLikedByCurrentUserObs.subscribe { isLiked ->
            setColor(itemView.btn_like, isLiked)
            itemView.btn_like.isEnabled = true
        }
        subscriptionList?.unsubscribe()
        subscriptionList = data.isInListObs.subscribe{
            isInList ->
            Log.d("BIND_D", data.title + " " + isInList)
            setColor(itemView.btn_pin, isInList)
            itemView.btn_pin.isEnabled = true
        }

        Log.d("BIND_D", data.toString())
        if (data.fullDescription == null || data.fullDescription == "") {
            itemView.txtbtn_see_more.text = ""
        } else {
            itemView.txtbtn_see_more.text = context.resources.getString(R.string.see_more)
            itemView.txtbtn_see_more.onClick {
                if (!flag) {
                    itemView.achievement_description_short.text = data.fullDescription
                    itemView.txtbtn_see_more.text = context.resources.getString(R.string.hide)
                    flag = true
                } else {
                    itemView.achievement_description_short.text = data.shortDescription
                    itemView.txtbtn_see_more.text = context.resources.getString(R.string.see_more)
                    flag = false
                }
            }
        }

        itemView.btn_like.onClick {
            data.isLikedByCurrentUserObs.onNext(!data.isLikedByCurrentUserObs.value)
//            setColor(itemView.btn_like, !data.isLikedByCurrentUserObs.value)
            data.like()
            val anim = AnimationUtils.loadAnimation(context, R.anim.like)
            itemView.btn_like.startAnimation(anim)

        }

        itemView.btn_pin.onClick {
            if(data.isInListObs.value){
                context.toast(context.getString(R.string.already_in_list_alert))
                return@onClick
            }
            data.createStore()
            val anim = AnimationUtils.loadAnimation(context, R.anim.like)
            itemView.btn_pin.startAnimation(anim)
//            itemView.btn_pin.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary))
        }

        itemView.btn_unlock.onClick {
            data.unlock()
            val anim = AnimationUtils.loadAnimation(context, R.anim.like)
            itemView.btn_unlock.startAnimation(anim)
//            itemView.btn_unlock.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary))
        }
    }

    fun setColor(view: ImageView, b: Boolean) {
        Log.d("setcolor", "set color $b")
        if (b) {
            view.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary),
                    android.graphics.PorterDuff.Mode.SRC_IN)
        } else {
            view.setColorFilter(ContextCompat.getColor(context, R.color.lightGreen),
                    android.graphics.PorterDuff.Mode.SRC_IN)
        }
    }
}