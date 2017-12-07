package com.vedro401.reallifeachievement.model

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.google.firebase.database.Exclude
import com.vedro401.reallifeachievement.adapters.RxRvAdapter.RxRvTransferProtocol
import com.vedro401.reallifeachievement.utils.STORY
import rx.Observable

class Story() : DataModel(), Parcelable {

//    var id: String? = null

    var id: String? = null
    var achievementTitle: String? = null
    var achievementImageUrl: String? = null

    var authorId: String? = null
    var authorName: String? = null
    var authorAvatarUrl: String? = null

    var status = STARTED
    var lastPost: StoryPost? = null

    constructor(ach: Achievement) : this() {
        id = ach.id
        achievementTitle = ach.title
        achievementImageUrl = ach.imageUrl
    }

    init {
        authorId = userManager.id()
        authorName = userManager.name()
        authorAvatarUrl = userManager.avatarUrl()
    }

    @Exclude
    fun getPosts(): Observable<RxRvTransferProtocol<StoryPost>> {
        return databaseManager.getStoryPosts(id!!)
    }

    fun finish(difficulty : Int){
        status = FINISHED
        databaseManager.finishStory(this, id!!,difficulty)
    }

    fun addPost(post: StoryPost) {
        savePost(post)
        updateLastPost(post)
        if (status == STARTED) {
            updateStatus(IN_PROGRESS)
            status = IN_PROGRESS
        }
    }

    fun updatePost(post: StoryPost) {
        Log.d(STORY, "Story.updatePost: post.id ${post.id} lastPost.id ${lastPost?.id} ")
        if (post.id != null) {
            savePost(post)
            if (post.id == lastPost?.id) {
                updateLastPost(post)
            }
        } else throw NullPointerException("updating nonexistent post")
    }

    private fun updateStatus(status: Int) {
        databaseManager.updateStoryStatus(id!!, status)
    }

    private fun savePost(post: StoryPost) {
        databaseManager.save(post, id!!)
    }

    private fun updateLastPost(post: StoryPost) {
        databaseManager.updateLastPost(this, post).subscribe { isSuccessful ->
//            Log.d(STORY, "Story.updateLastPost is successful $isSuccessful ")
            if (isSuccessful) {
                lastPost = post
            }
        }
    }

    fun deletePost(postId: String) {
//        Log.d(STORY, "Story.deletePost lastPost.id \"${lastPost?.id}\" deletedPost.id \"$postId\"")
        databaseManager.deletePost(id!!, postId).subscribe { isSuccessful ->
            if (isSuccessful) {
                if (lastPost?.id == postId) {
//                    Log.d(STORY, "Story.deletePost ${lastPost?.id} == $postId")
                    updateLastPost()
                }
            }
        }
    }

    private fun updateLastPost() {
        databaseManager.updateLastPost(this).subscribe { lastPost ->
//            Log.d(STORY, "Story.updateLastPost lastPost $lastPost")
            if (lastPost == null) {
//                Log.d(STORY, "Story.updateLastPost update status")
                databaseManager.updateStoryStatus(id!!, STARTED)
                status = STARTED
            }
            this.lastPost = lastPost
        }
    }

    fun save() {
        databaseManager.save(this)
    }

    private constructor(source: Parcel) : this() {
        id = source.readString()
        id = source.readString()
        achievementTitle = source.readString()
        achievementImageUrl = source.readString()
        authorId = source.readString()
        authorName = source.readString()
        authorAvatarUrl = source.readString()
        status = source.readInt()
        lastPost = source.readParcelable(StoryPost::class.java.classLoader)
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(id)
        writeString(achievementTitle)
        writeString(achievementImageUrl)
        writeString(authorId)
        writeString(authorName)
        writeString(authorAvatarUrl)
        writeInt(status)
        writeParcelable(lastPost, Parcelable.PARCELABLE_WRITE_RETURN_VALUE)
    }

    companion object {
        @get:Exclude
        const val STARTED = 0

        @get:Exclude
        const val IN_PROGRESS = 1

        @get:Exclude
        const val FINISHED = 2

        @JvmField
        val CREATOR: Parcelable.Creator<Story> = object : Parcelable.Creator<Story> {
            override fun createFromParcel(source: Parcel): Story = Story(source)
            override fun newArray(size: Int): Array<Story?> = arrayOfNulls(size)
        }
    }
}