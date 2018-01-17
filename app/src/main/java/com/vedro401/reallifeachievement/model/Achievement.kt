package com.vedro401.reallifeachievement.model

import android.util.Log
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.vedro401.reallifeachievement.utils.REFUCKTTAG
import rx.Subscription
import rx.subjects.BehaviorSubject
import kotlin.collections.ArrayList

@IgnoreExtraProperties
class Achievement() : DataModel() {

    constructor(title: String,
                shortDescription: String,
                fullDescription: String?,
                imageUrl: String? = null,
                author: String? = null) : this() {
        this.title = title
        this.shortDescription = shortDescription
        this.fullDescription = fullDescription
        this.imageUrl = imageUrl
        this.author = author
    }

    var title: String? = null
    var shortDescription: String? = null
    var fullDescription: String? = null
    var imageUrl: String? = null
    var author: String? = null

    var likes: Int = 0
    var comments: Int = 0
    var unlocked: Int = 0
    var difficulty: Int = 0
    var id: String? = null
        set(value) {
            field = value
            if (value != null) {
                userManager.isAuthorisedObs.subscribe({ authorised ->
                    if (authorised) {
                        likeSubscription?.unsubscribe()
                        try {
                            likeSubscription =
                                    databaseManager.isAchievementLiked(this)
                                    .subscribe(isLikedByCurrentUserObs)
                        } catch (ex: Exception){
                            Log.d(REFUCKTTAG, ex.toString())

                        }
                        inListSubscription?.unsubscribe()
                        inListSubscription = databaseManager.isAchievementInList(this)
                                .subscribe(isInListObs)

                    } else {
                        Log.d(REFUCKTTAG, "Not authorised")
                            isLikedByCurrentUserObs.onNext(false)
                            likeSubscription?.unsubscribe()
                            isInListObs.onNext(false)
                            inListSubscription?.unsubscribe()
                    }

                },
                        { ex ->
                            Log.d(REFUCKTTAG, ex.message)
                        })
            }

        }

    @get:Exclude
    val isLikedByCurrentUserObs = BehaviorSubject.create<Boolean>(false)!!

    @get:Exclude
    var likeSubscription: Subscription? = null

    @get:Exclude
    var inListSubscription: Subscription? = null

    @get:Exclude
    val isInListObs = BehaviorSubject.create<Boolean>(false)!!

    @get:Exclude
    var tags = ArrayList<String>()


    fun setId() {
        databaseManager!!.setId(this)
    }

    fun save() {
        databaseManager!!.save(this)
    }

    fun like() {
        databaseManager.likeAchievement(this)
    }

    fun createStore() {
        val story = Story(this)
        story.save()
    }

    override fun toString(): String =
            "$title $shortDescription $fullDescription $likes $unlocked $difficulty $id"

    override fun clear() {

    }
}
