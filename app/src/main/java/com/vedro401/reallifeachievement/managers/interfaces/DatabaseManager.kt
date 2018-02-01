package com.vedro401.reallifeachievement.managers.interfaces

import com.vedro401.reallifeachievement.transferProtocols.TransferProtocol
import com.vedro401.reallifeachievement.transferProtocols.SeparatedFieldsTP
import com.vedro401.reallifeachievement.model.*
import rx.Observable

/**
 * Created by someone on 17.09.17.
 */
interface DatabaseManager {
    val userManager: UserManager
    // User
    fun save(userData: UserData)

    fun getUserStatisticData(): Observable<SeparatedFieldsTP>
    fun getFinishedStories(): Observable<TransferProtocol<Story>>
    fun getNotFinishedStories(): Observable<TransferProtocol<Story>>
    fun getUserFavTags(): Observable<Pair<String, Int>>

    //Achievement
    fun setId(ach : Achievement)
    fun save(ach : Achievement)
    fun clear(ach: Achievement)
    fun likeAchievement(ach : Achievement)
    fun getAchievements(): Observable<TransferProtocol<Achievement>>
    fun getMyAchievements(): Observable<TransferProtocol<Achievement>>
    fun initAchievementTags(ach : Achievement):Observable<Boolean>
    fun isAchievementLiked(ach : Achievement) : Observable<Boolean>
    fun isAchievementInList(ach : Achievement) : Observable<Boolean>
    fun addNewTags(newTags: ArrayList<String>)
    fun removeTags(oldTags: ArrayList<String>)
//    fun clear(ach : Achievement)
//    fun isAchievementInList(achId: String, uid: String) : Observable<Boolean>
    fun searchTagsTips(request: String): Observable<TransferProtocol<String>>

    //Story
    fun save(story: Story)
    fun delete(story: Story) //!!!
    fun updateStoryStatus(story: Story, newStatus: Int)
    fun updateLastPost(story: Story, post: StoryPost?): Observable<Boolean>
    fun updateLastPost(story: Story): Observable<StoryPost?>
    fun finishStory(story: Story, difficulty: Int)


    //Story post
    fun save(post: StoryPost, storyId: String) // : Observable<*>
    fun getStoryPosts(storyId: String): Observable<TransferProtocol<StoryPost>>
    fun deletePost(storyId : String, postId: String): Observable<Boolean>
}