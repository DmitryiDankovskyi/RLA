package com.vedro401.reallifeachievement.database

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import com.vedro401.reallifeachievement.adapters.RxRvAdapter.RxChildListener
import com.vedro401.reallifeachievement.adapters.RxRvAdapter.RxRvTransferProtocol
import com.vedro401.reallifeachievement.model.*
import com.vedro401.reallifeachievement.utils.LOGTAG
import com.vedro401.reallifeachievement.utils.STORY
import com.vedro401.reallifeachievement.utils.UserManager
import com.vedro401.reallifeachievement.utils.transferProtocols.*
import rx.Observable
import rx.Subscriber
import rx.subjects.AsyncSubject
import rx.subjects.BehaviorSubject


class FirebaseManager : DatabaseManager {
    private val TAG = "FMan"

    private val MAIN_DATA = "mainData"
    private val POSTS = "posts"

    private val ACHIEVEMENTS = "achievements"
    private val ACHIEVEMENTS_MAIN_DATA = "achievements/mainData"
    private val ACHIEVEMENTS_LIKE_LIST = "achievements/likesLists"
    private val ACHIEVEMENTS_TAGS = "achievements/tags"
    private val USERS_MAIN_DATA = "users/mainData"
    private val ACH_LIKES = "likes"

    override lateinit var userManager: UserManager

    private val database = FirebaseDatabase.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()


    private val userProvider = BehaviorSubject.create<TransferProtocol<UserData>>()
    private var achievementsListener = RxChildListener(Achievement::class.java)
    private val storiesListener = RxChildListener(Story::class.java)
    private val storyPostsListener = RxChildListener(StoryPost::class.java)


    init {
        auth.addAuthStateListener({ firebaseAuth ->
            if (firebaseAuth.currentUser != null) {
                val user = firebaseAuth.currentUser!!
                if (user.displayName == null)
                    Log.d(LOGTAG, "FirebaseManager: user signed in")
                else
                    Log.d(LOGTAG, "FirebaseManager: user signed up")
                val userData = UserData()
                userData.name = if (user.displayName == null) "None" else user.displayName!!
                userData.email = if (user.email == null) "None" else user.email!!
                userData.id = user.uid
                userProvider.onNext(TransferProtocol(SIGN_IN, userData))
            } else {
                Log.d(LOGTAG, "FirebaseManager: user logged out")
                userProvider.onNext(TransferProtocol(SIGN_OUT))
            }
        })
    }


    //TODO remove
    fun likeAch(ach: Achievement) {
        Log.d(TAG, "Ach liked. Key: ${ach.id} Title: ${ach.title}")
        if (auth.currentUser == null) {
            Log.d(TAG, "Ach liked by unauth")
            return
            //TODO call autt alert
        }
        if (ach.id != null) {
            val likeListRef = database.getReference(ACHIEVEMENTS_LIKE_LIST)
            Log.d(TAG, ach.likes.toString())
            likeListRef.child(ach.id).child(auth.currentUser!!.uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError?) {
                            Log.d(TAG, "like call canceled")
                        }

                        override fun onDataChange(p0: DataSnapshot?) {
                            Log.d(TAG, "like call data snapshot: $p0")
                            var likedBefore: Boolean

                            database.getReference(ACHIEVEMENTS_MAIN_DATA).child(ach.id).child(ACH_LIKES)
                                    .runTransaction(object : Transaction.Handler {
                                        override fun doTransaction(mutableData: MutableData?): Transaction.Result {
                                            likedBefore = p0!!.value == null
                                            val v: Boolean? = if (likedBefore) true else null
                                            likeListRef.child(ach.id).child(auth.currentUser!!.uid)
                                                    .setValue(v).addOnCompleteListener {
                                                var likesCount = mutableData!!.getValue(Int::class.java)!!
                                                if (likedBefore) {
                                                    likesCount--
                                                } else {
                                                    likesCount++
                                                }
                                                mutableData.value = likesCount
                                            }
                                            return Transaction.success(mutableData)
                                        }

                                        override fun onComplete(databaseError: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                                            Log.d(TAG, "postTransaction:onComplete:" + databaseError)
                                        }

                                    })
                        }
                    })
        } else {
            Log.e(TAG, "Offline achievement liked name: ${ach.title} short description ${ach.shortDescription}")
        }
    }

    override fun save(ach: Achievement) {
        val ref = database.getReference(ACHIEVEMENTS_MAIN_DATA)
        if(ach.id == null) ach.id = ref.push().key
        ref.child(ach.id).setValue(ach)
        for (tag: String in ach.tags) {
            Log.d(TAG, "tag $tag saved")
            database.getReference(ACHIEVEMENTS_TAGS).child(ach.id).child(tag).setValue(true)
        }

    }

    override fun likeAchievement(ach: Achievement) {

        Log.i(TAG, "FirebaseManager: Ach liked. Title \"${ach.title}\"")

        if (ach.id == "") {
            Log.e(TAG, "Liked achievement with no id. Title \"${ach.title}\"")
            return
        }

        if (auth.currentUser == null) {
            Log.w(TAG, "Unauthorized user liked achievement \"${ach.title}\"")
            //TODO make alert
            return
        }

        database.getReference(ACHIEVEMENTS).child("likeLists").child(ach.id).runTransaction(
                object : Transaction.Handler {
                    override fun doTransaction(likeList: MutableData?): Transaction.Result {
                        var uid = auth.currentUser!!.uid
                        var liked = false
                        if (likeList == null) {
                            Log.d(TAG, "likeList is null")
                            return Transaction.success(likeList)
                        }
                        likeList!!.children
                                .filter { it.key == uid }
                                .forEach {
                                    it.value = null
                                    liked = true
                                    return@forEach
                                }
                        if (!liked) {
                            likeList.child(uid).value = true
                        }
                        Log.d(TAG, "Is liked before $liked")

                        database.getReference(ACHIEVEMENTS_MAIN_DATA).child(ach.id).child(ACH_LIKES)
                                .runTransaction(object : Transaction.Handler {
                                    override fun onComplete(databaseError: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                                        if (databaseError != null)
                                            Log.d(TAG, "postTransaction:onComplete error:" + databaseError)
                                        else
                                            Log.d(TAG, "Second transaction finished")
                                    }

                                    override fun doTransaction(likesCount: MutableData?): Transaction.Result {
                                        if (likesCount == null) {
                                            return Transaction.success(likesCount)
                                        }
                                        var lc: Int = likesCount.getValue(Int::class.java)!!
                                        if (liked) {
                                            lc--
                                        } else {
                                            lc++
                                        }
                                        likesCount.value = lc
                                        return Transaction.success(likesCount)
                                    }
                                })
                        return Transaction.success(likeList)
                    }

                    override fun onComplete(databaseError: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                        if (databaseError != null)
                            Log.d(TAG, "postTransaction:onComplete error:" + databaseError)
                        else
                            Log.d(TAG, "First transaction finished")
                    }
                }
        )

    }

    override fun save(story: Story) {
        if(!userManager.isAuthorised){
            Log.d(TAG,"FirebaseManage: unauthorized user tried to create story")
            return
        }
        val ref = database.getReference("users/stories")
                .child(auth.currentUser!!.uid).child(MAIN_DATA)
        if(story.id == null){
            story.id = ref.push().key
        }
        ref.child(story.id).setValue(story)
    }

    override fun unlock(ach: Achievement) {
        if (ach.id != null)
            database.getReference("users/unlocked").child(auth.currentUser!!.uid).child(ach.id).setValue(true)
        else
            Log.d(TAG, "ach without id pinned")
    }

    override fun save(userData: UserData) {
        database.getReference(USERS_MAIN_DATA).child(userData.id).setValue(userData)
    }


    override fun signUp(name: String, email: String, pass: String): Observable<String> {
        val subj = AsyncSubject.create<String>()

        val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
        auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser!!
                        user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                Log.d(LOGTAG, "Fman: Update profile failed. ${task.exception.toString()}")
                            } else {
                                Log.d(LOGTAG, "Fman: Profile info updated.")
                                UserData(name, user.uid, email).save()
                                userProvider.onNext(TransferProtocol(CHANGE_NAME,UserData(name = name)))
                            }
                        }
                        subj.onNext("Ok")
                        subj.onCompleted()
                        Log.d(TAG, "signUp check call")
                    } else {
                        Log.d(TAG, task.exception!!.message)
                        subj.onNext("Sign up: " + task.exception!!.message)
                        subj.onCompleted()
                    }
                }
        return subj

    }

    override fun signIn(email: String, pass: String): Observable<String> {
        val subj = AsyncSubject.create<String>()
        auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        subj.onNext("Ok")
                        subj.onCompleted()
                        Log.d(TAG, "signIn check call")
                    } else {
                        Log.d(TAG, task.exception!!.message)
                        subj.onNext("Sign in: " + task.exception!!.message)
                        subj.onCompleted()
                    }
                }
        return subj
    }

    override fun signOut() {
        auth.signOut()
    }


    override fun getCurrentUser(): Observable<TransferProtocol<UserData>> {
        return userProvider
    }

    fun searchAch(request: String): Query {
        return database.getReference(ACHIEVEMENTS_MAIN_DATA).orderByChild("title")
                .startAt(request).endAt(request + "~")
    }

    override fun getAchievements(): Observable<RxRvTransferProtocol<Achievement>> {
        achievementsListener.setQuery(database.getReference(ACHIEVEMENTS_MAIN_DATA))
        return achievementsListener.replaySubject
    }

    override fun getStories(): Observable<RxRvTransferProtocol<Story>> {
        Log.d(STORY, "users/stories/${auth.currentUser!!.uid}/$MAIN_DATA")
        storiesListener.setQuery(
                database.getReference("users/stories/${auth.currentUser!!.uid}/$MAIN_DATA"))
        return storiesListener.replaySubject
    }

    override fun getStoryPosts(storyId: String): Observable<RxRvTransferProtocol<StoryPost>> {
        storyPostsListener.setQuery(database.getReference("users/stories/${userManager.id()}/posts/$storyId"))
        return storyPostsListener.replaySubject
    }

    override fun save(post: StoryPost, storyId: String){
        val ref = database.getReference("users/stories/${userManager.id()}/posts/$storyId")
        if(post.id == null) post.id = ref.push().key
        ref.child(post.id).setValue(post)
    }

    override fun updateLastPost(storyId: String, post: StoryPost): Observable<Boolean> {
        val async = AsyncSubject.create<Boolean>()

        database.getReference("users/stories/${userManager.id()}/$MAIN_DATA/$storyId/lastPost")
                .setValue(post).addOnCompleteListener {
            task ->
            async.onNext(task.isSuccessful)
            async.onCompleted()
            if(!task.isSuccessful){
                Log.d(STORY,"FirebaseManager.updateLastPost: ${task.exception?.message}")
            }
        }
        return async
    }

    override fun updateLastPost(storyId: String): Observable<StoryPost?> {
        val async = AsyncSubject.create<StoryPost?>()
        database.getReference("users/stories/${userManager.id()}/posts/$storyId").limitToLast(1)
                .addValueEventListener(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError?) {
                        Log.d(STORY, "FirebaseManager.updateLastPost: Cancelled ${p0?.message}")
                    }
                    override fun onDataChange(p0: DataSnapshot?) {
                        Log.d("FUCK","p0.value ${p0!!.value} ")
                        if(p0!!.value == null) {
                            async.onNext(null)
                            async.onCompleted()
                            return
                        }
                        val post = p0.children.first().getValue(StoryPost::class.java)!!
                        updateLastPost(storyId, post).map { isSuccessfully ->
                            if(isSuccessfully)
                                post
                             else {
                                null
                            }
                            }.subscribe(async)
                    }
                })
        return  async
    }

    override fun updateStatus(storyId: String, status: Int) {
        database.getReference("users/stories/${userManager.id()}/$MAIN_DATA/$storyId/status")
                .setValue(status)
    }

    override fun deletePost(storyId: String, postId: String): Observable<Boolean> {
        return Observable.create<Boolean> { sub ->
            database.getReference("users/stories/${userManager.id()}/posts/$storyId/$postId")
                    .setValue(null).addOnCompleteListener { task ->
                sub.onNext(task.isSuccessful)
                sub.onCompleted()
            }
        }

    }
}