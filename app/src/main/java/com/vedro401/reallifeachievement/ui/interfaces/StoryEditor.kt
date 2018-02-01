package com.vedro401.reallifeachievement.ui.interfaces

import com.vedro401.reallifeachievement.model.StoryPost

/**
 * Created by someone on 01.02.18.
 */
interface StoryEditor {
    var post: StoryPost
    fun updatePost(post: StoryPost)
    fun deletePost(post: StoryPost)
}