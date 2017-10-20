package com.vedro401.reallifeachievement.model

import android.util.Log
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlin.collections.ArrayList

/**
 * Created by someone on 29.08.17.
 */
@IgnoreExtraProperties
class Achievement() : DataModel() {

    constructor( title: String,
                 shortDescription: String,
                 fullDescription: String?,
                 imageUrl: String? = null,
                 author: String? = null) : this(){
        this.title = title
        this.shortDescription = shortDescription
        this.fullDescription = fullDescription
        this.imageUrl = imageUrl
        this.author = author
    }

    var title: String? = null
    var shortDescription : String? = null
    var fullDescription: String? = null
    var imageUrl: String? = null
    var author: String? = null

    var likes: Int = 0
    var unlocked: Int = 0
    var difficulty: Int = 0
    var id: String? = null

    @get:Exclude
    var tags = ArrayList<String>()

    init {
        author = userManager.id()
    }

    fun save() {
        databaseManager!!.save(this)
    }

    fun like() {
        databaseManager.likeAchievement(this)
    }

    fun createStore(){
        val story = Story(this)
        story.save()
    }

    fun unlock(){
        databaseManager.unlock(this)
    }

    override fun toString(): String {
        return "$title $shortDescription $fullDescription $likes $unlocked $difficulty $id"
    }
}
