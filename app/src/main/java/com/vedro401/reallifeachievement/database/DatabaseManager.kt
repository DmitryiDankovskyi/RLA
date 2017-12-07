package com.vedro401.reallifeachievement.database

import com.vedro401.reallifeachievement.adapters.RxRvAdapter.RxRvTransferProtocol
import com.vedro401.reallifeachievement.model.*
import com.vedro401.reallifeachievement.utils.UserManager
import com.vedro401.reallifeachievement.utils.transferProtocols.TransferProtocol
import com.vedro401.reallifeachievement.utils.transferProtocols.UserTransferProtocol
import rx.Observable

/**
 * Created by someone on 17.09.17.
 */
interface DatabaseManager {
    var userManager: UserManager
    fun getCurrentUserData(): Observable<TransferProtocol<UserData>>


    // User
    fun save(userData: UserData)
    fun signIn(email: String, pass: String): Observable<String>
    fun signUp(name: String, email: String, pass: String): Observable<String>
    fun signOut()

    fun getNotFinishedStories(): Observable<RxRvTransferProtocol<Story>>
    fun getFinishedStories(): Observable<RxRvTransferProtocol<Story>>

    //Achievement
    fun save(ach : Achievement)
    fun likeAchievement(ach : Achievement)
    fun unlock(ach : Achievement)
    fun getAchievements(): Observable<RxRvTransferProtocol<Achievement>>
    fun isAchievementLiked(ach : Achievement) : Observable<Boolean>
    fun isAchievementInList(ach : Achievement) : Observable<Boolean>
    fun clear(ach : Achievement)
//    fun isAchievementInList(achId: String, uid: String) : Observable<Boolean>


    //Story
    fun save(story: Story)
    fun delete(storyId: String) //!!!
    fun updateStoryStatus(storyId: String, status: Int)
    fun updateLastPost(story: Story, post: StoryPost?): Observable<Boolean>
    fun updateLastPost(story: Story): Observable<StoryPost?>
    fun finishStory(story: Story, achievementId: String, difficulty: Int)


    //Story post
    fun save(post: StoryPost, storyId: String) // : Observable<*>
    fun getStoryPosts(storyId: String): Observable<RxRvTransferProtocol<StoryPost>>
    fun deletePost(storyId : String, postId: String): Observable<Boolean>
}