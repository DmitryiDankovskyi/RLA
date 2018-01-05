package com.vedro401.reallifeachievement.model

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import com.google.firebase.database.Exclude
import com.vedro401.reallifeachievement.transferProtocols.RxRvTransferProtocol
import com.vedro401.reallifeachievement.utils.STORY
import rx.Observable

class Story() : DataModel(), Parcelable {

//    var uid: String? = null

    var id: String? = null
    var achievementTitle: String? = null
    var achievementImageUrl: String? = null

    var authorId: String? = null
    var authorName: String? = null
    var authorAvatarUrl: String? = null

    var status = STARTED
    var difficulty : Int? = null
    var postsCount = 0
    set(value) {
        field = value
        Log.d("FFFUCK","value - $value uid = $id" )
    }
    var lastPost: StoryPost? = null

    constructor(ach: Achievement) : this() {
        id = ach.id
        achievementTitle = ach.title
        achievementImageUrl = ach.imageUrl
    }

    init {
        authorId = userManager.uid
        authorName = userManager.name
        authorAvatarUrl = userManager.avatarUrl.toString()
    }

    @Exclude
    fun getPosts(): Observable<RxRvTransferProtocol<StoryPost>> =
            databaseManager.getStoryPosts(id!!)

    fun finish(difficulty : Int){
        status = FINISHED
        this.difficulty = difficulty
        databaseManager.finishStory(this,difficulty)
    }

    fun addPost(post: StoryPost) {
        savePost(post)
        updateLastPost(post)
        if (status == STARTED) {
            updateStatus(IN_PROGRESS)
            status = IN_PROGRESS
        }
    }

    private fun savePost(post: StoryPost) {
        databaseManager.save(post, id!!)
    }

    fun updatePost(post: StoryPost) {
        Log.d(STORY, "Story.updatePost: post.uid ${post.id} lastPost.uid ${lastPost?.id} ")
        if (post.id != null) {
            savePost(post)
            if (post.id == lastPost?.id) {
                updateLastPost(post)
            }
        } else throw NullPointerException("updating nonexistent post")
    }

    private fun updateStatus(status: Int) {
        databaseManager.updateStoryStatus(this, status)
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
//        Log.d(STORY, "Story.deletePost lastPost.uid \"${lastPost?.uid}\" deletedPost.uid \"$postId\"")
        databaseManager.deletePost(id!!, postId).subscribe { isSuccessful ->
            if (isSuccessful) {
                if (lastPost?.id == postId) {
//                    Log.d(STORY, "Story.deletePost ${lastPost?.uid} == $postId")
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
                databaseManager.updateStoryStatus(this, STARTED)
                status = STARTED
            }
            this.lastPost = lastPost
        }
    }

    fun save() {
        databaseManager.save(this)
        databaseManager.save(this)
    }

    fun delete(){
        databaseManager.delete(this)
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
        postsCount = source.readInt()
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
        writeInt(postsCount)
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