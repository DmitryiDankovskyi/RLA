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
    fun save(ach : Achievement)
    fun save(userData: UserData)
    fun likeAchievement(ach : Achievement)
    fun signUp(name: String, email: String, pass: String): Observable<String>
    fun signIn(email: String, pass: String): Observable<String>
    fun signOut()
    fun unlock(ach : Achievement)
    fun getCurrentUser(): Observable<TransferProtocol<UserData>>
    fun getAchievements(): Observable<RxRvTransferProtocol<Achievement>>
    fun getStories(): Observable<RxRvTransferProtocol<Story>>
    fun getStoryPosts(storyId: String): Observable<RxRvTransferProtocol<StoryPost>>

    fun save(story: Story)
    fun updateStatus(storyId: String, status: Int)
    fun save(post: StoryPost, storyId: String) // : Observable<*>
    fun updateLastPost(storyId: String, post: StoryPost): Observable<Boolean>
    fun updateLastPost(storyId: String): Observable<StoryPost?>
    fun deletePost(storyId : String, postId: String): Observable<Boolean>
}