package com.vedro401.reallifeachievement.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import rx.Observable
import rx.Subscription
import rx.subjects.BehaviorSubject
import kotlin.collections.ArrayList

@IgnoreExtraProperties
class Achievement() : DataModel(), Parcelable {
    constructor(title: String,
                shortDescription: String,
                imageUrl: String? = null,
                authorId: String? = null) : this() {
        this.title = title
        this.description = shortDescription
        this.imageUrl = imageUrl
        this.authorId = authorId
    }

    var id: String? = null

    var title: String? = null

    var description: String? = null

    var imageUrl: String? = null

    var authorId: String? = null

    var authorAvatarUrl: String? = null

    var authorName: String? = null

    var likes: Int = 0

    var comments: Int = 0

    var unlocked: Int = 0

    var difficulty: Int = 0


    @get:Exclude
    private var likeSubscription: Subscription? = null

    @get:Exclude
    private var inListSubscription: Subscription? = null

    @get:Exclude
    val isLikedByCurrentUserObs = BehaviorSubject.create<Boolean>(false)!!
        get() {
            if (likeSubscription == null
                    || likeSubscription!!.isUnsubscribed) {
                likeSubscription = databaseManager.isAchievementLiked(this)
                        .subscribe(field)
            }
            return field
        }

    @get:Exclude
    val isInListObs = BehaviorSubject.create<Boolean>(false)!!
        get() {
            if (inListSubscription == null
                    || inListSubscription!!.isUnsubscribed) {
                inListSubscription = databaseManager.isAchievementInList(this)
                        .subscribe(field)
            }
            return field
        }

    @get:Exclude
    var tags = ArrayList<String>()

    fun initTags(): Observable<Boolean> {
        return if(id != null) databaseManager.initAchievementTags(this)
        else Observable.error(Exception("Achievement have no id."))
    }

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
            "$title $description $likes $unlocked $difficulty $id"

    override fun clear() {
        likeSubscription?.unsubscribe()
        inListSubscription?.unsubscribe()
    }

    constructor(source: Parcel) : this(){
        id = source.readString()
        title = source.readString()
        description = source.readString()
        imageUrl = source.readString()
        authorId = source.readString()
        authorName = source.readString()
        authorAvatarUrl = source.readString()
        likes = source.readInt()
        comments = source.readInt()
        unlocked = source.readInt()
        difficulty = source.readInt()
    }
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(title)
        writeString(description)
        writeString(imageUrl)
        writeString(authorId)
        writeString(authorName)
        writeString(authorAvatarUrl)
        writeInt(likes)
        writeInt(comments)
        writeInt(unlocked)
        writeInt(difficulty)
    }

    override fun describeContents() = 0

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Achievement> = object : Parcelable.Creator<Achievement> {
            override fun createFromParcel(source: Parcel): Achievement = Achievement(source)
            override fun newArray(size: Int): Array<Achievement?> = arrayOfNulls(size)
        }
    }
}
