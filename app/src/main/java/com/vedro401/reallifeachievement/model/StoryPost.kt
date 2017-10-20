package com.vedro401.reallifeachievement.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by someone on 11.10.17.
 */
class StoryPost() : Parcelable {
    constructor(title : String, timestamp : String, content: String) : this(){
        this.title = title
        this.timeStamped = timeStamped
        this.content = content
    }

    var id : String? = null
    var title: String? = null
    var timeStamped: String? = null
    var content: String? = null

    constructor(source: Parcel) : this(){
        id = source.readString()
        title = source.readString()
        timeStamped = source.readString()
        content = source.readString()
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(title)
        writeString(timeStamped)
        writeString(content)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<StoryPost> = object : Parcelable.Creator<StoryPost> {
            override fun createFromParcel(source: Parcel): StoryPost = StoryPost(source)
            override fun newArray(size: Int): Array<StoryPost?> = arrayOfNulls(size)
        }
    }

    override fun toString(): String {
        return "title \"$title\" content \"$content\" id \"$id\""
    }
}