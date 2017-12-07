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
import rx.subjects.AsyncSubject
import rx.subjects.BehaviorSubject


class FirebaseManager : DatabaseManager {
    private val TAG = "FMan"

    private val MAIN_DATA = "mainData"

    private val ACHIEVEMENTS = "achievements"
    private val ACHIEVEMENTS_MAIN_DATA = "achievements/mainData"
    private val ACHIEVEMENTS_LIKE_LIST = "achievements/likeLists"
    private val ACHIEVEMENTS_TAGS = "achievements/tags"
    private val USERS_MAIN_DATA = "users/mainData"
    private val ACH_LIKES = "likes"
    private val FINISHED_STORIES = "users/finishedStories"
    private val NOT_FINISHED_STORIES = "users/notFinishedStories"
    private val STORY_POSTS = "storyPosts"

    private val ACH_LIKE_PREFIX = "achLike"
    private val ACH_IN_FINISHED_LIST_PREFIX = "achInFinished"
    private val ACH_IN_NOT_FINISHED_LIST_PREFIX = "achInNotFinished"

    override lateinit var userManager: UserManager

    private val database = FirebaseDatabase.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val valueEventListenersMap = HashMap<String, ValueEventListener>()


    private val userProvider = BehaviorSubject.create<TransferProtocol<UserData>>()
    private var achievementsListener = RxChildListener(Achievement::class.java)
    private val notFinishedStoriesListener = RxChildListener(Story::class.java)
    private val finishedStoriesListener = RxChildListener(Story::class.java)
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

    override fun getCurrentUserData(): Observable<TransferProtocol<UserData>> = userProvider

    // User

    //TODO move to user manager?

    override fun save(userData: UserData) {
        database.getReference(USERS_MAIN_DATA).child(userData.id).setValue(userData)
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
                                Log.d(LOGTAG, "name $name uid ${user.uid} email $email.")
                                val ud = UserData(name = name, id =  user.uid, email = email)
                                ud.save()
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



    override fun getNotFinishedStories(): Observable<RxRvTransferProtocol<Story>> {
        notFinishedStoriesListener.setQuery(
                database.getReference("$NOT_FINISHED_STORIES/${auth.currentUser!!.uid}"))
        return notFinishedStoriesListener.replaySubject
    }

    override fun getFinishedStories(): Observable<RxRvTransferProtocol<Story>> {
        finishedStoriesListener.setQuery(
                database.getReference("$FINISHED_STORIES/${auth.currentUser!!.uid}"))
        return finishedStoriesListener.replaySubject    }


    //Achievements


    override fun save(ach: Achievement) {
        val ref = database.getReference(ACHIEVEMENTS_MAIN_DATA)
        if(ach.id == null) ach.id = ref.push().key
        ref.child(ach.id).setValue(ach)
        for (tag: String in ach.tags) {
            Log.d(TAG, "tag $tag saved")
            database.getReference(ACHIEVEMENTS_TAGS).child(ach.id).child(tag).setValue(true)
        }
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

        database.getReference(ACHIEVEMENTS_LIKE_LIST).child(ach.id).runTransaction(
                object : Transaction.Handler {
                    override fun doTransaction(likeList: MutableData?): Transaction.Result {
                        val uid = auth.currentUser!!.uid
                        var liked = false
                        if (likeList == null) {
                            Log.d(TAG, "likeList is null")
                            return Transaction.success(likeList)
                        }
                        likeList.children
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

    override fun unlock(ach: Achievement) {
        if (ach.id != null)
            database.getReference("users/unlocked").child(auth.currentUser!!.uid).child(ach.id).setValue(true)
        else
            Log.d(TAG, "ach without id pinned")
    }

    override fun getAchievements(): Observable<RxRvTransferProtocol<Achievement>> {
        achievementsListener.setQuery(database.getReference(ACHIEVEMENTS_MAIN_DATA))
        return achievementsListener.replaySubject
    }

    override fun isAchievementLiked(ach : Achievement) : Observable<Boolean>{
        val response = BehaviorSubject.create<Boolean>()
        val listener = object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                Log.w(TAG, "isAchievementLiked.onCancelled ${p0!!.message}")
            }
            override fun onDataChange(p0: DataSnapshot?) {
                response.onNext(p0!!.value != null)
            }
        }
        database.getReference("$ACHIEVEMENTS_LIKE_LIST/${ach.id}/${userManager.id()!!}")
                .addValueEventListener(listener)
        valueEventListenersMap.put("ach_${ach.id}", listener)
        return response
    }

    override fun isAchievementInList(ach: Achievement): Observable<Boolean> {
        val response = BehaviorSubject.create<Boolean>()
        val inFinishedlistener = object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                Log.w(TAG, "isAchievementInFinishedList.onCancelled ${p0!!.message}")
            }
            override fun onDataChange(p0: DataSnapshot?) {
                if(p0!!.value != null) response.onNext(true)
                Log.w(TAG, "inFinishedlistener: ${ach.title} $p0")
            }
        }
        database.getReference("$FINISHED_STORIES/${userManager.id()!!}/${ach.id}")
                .addValueEventListener(inFinishedlistener)

        val inNotFinishedlistener = object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                Log.w(TAG, "isAchievementInNotFinishedList.onCancelled ${p0!!.message}")
            }
            override fun onDataChange(p0: DataSnapshot?) {
                if(p0!!.value != null) response.onNext(true)
                Log.w(TAG, "inNotFinishedlistener ${ach.title} $p0")
            }
        }
        database.getReference("$NOT_FINISHED_STORIES/${userManager.id()!!}/${ach.id}")
                .addValueEventListener(inNotFinishedlistener)

        valueEventListenersMap.put("$ACH_IN_FINISHED_LIST_PREFIX${ach.id}", inFinishedlistener)
        valueEventListenersMap.put("$ACH_IN_NOT_FINISHED_LIST_PREFIX${ach.id}", inNotFinishedlistener)
        return response
    }

    override fun clear(ach: Achievement) {
        database.getReference("$ACHIEVEMENTS_LIKE_LIST/${ach.id}/${userManager.id()}")
                .removeEventListener(valueEventListenersMap["$ACH_LIKE_PREFIX${ach.id}"])

    }


    //Story

    override fun save(story: Story) {
        if(!userManager.isAuthorised){
            Log.d(TAG,"FirebaseManage: unauthorized user tried to create story")
            return
        }
        val ref = database.getReference(NOT_FINISHED_STORIES)
                .child(auth.currentUser!!.uid)
//        if(story.id == null){
//            story.id = ref.push().key
//        }
        ref.child(story.id).setValue(story)
    }

    override fun delete(storyId: String) {
        if(!userManager.isAuthorised){
            Log.d(TAG,"FirebaseManage: unauthorized user tried to create story")
            return
        }
        database.getReference(NOT_FINISHED_STORIES).child(auth.currentUser!!.uid).child(storyId).setValue(null)
    }

    override fun updateStoryStatus(storyId: String, status: Int) {
        database.getReference("NOT_FINISHED_STORIES/${userManager.id()}/$storyId/status")
                .setValue(status)
    }

    override fun updateLastPost(story: Story, post: StoryPost?): Observable<Boolean> {
        val async = AsyncSubject.create<Boolean>()
        val ifFinished = if(story.status == Story.FINISHED) FINISHED_STORIES else NOT_FINISHED_STORIES
        database.getReference("$ifFinished/${userManager.id()}/${story.id}/lastPost")
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

    override fun updateLastPost(story: Story): Observable<StoryPost?> {
        val async = AsyncSubject.create<StoryPost?>()
        database.getReference("users/$STORY_POSTS/${userManager.id()}/${story.id!!}").limitToLast(1)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError?) {
                        Log.d(STORY, "FirebaseManager.updateLastPost: Cancelled ${p0?.message}")
                    }
                    override fun onDataChange(p0: DataSnapshot?) {
                        var post : StoryPost? = null
                        if(p0!!.value != null) {
                            post = p0.children.first().getValue(StoryPost::class.java)
                        }
                        updateLastPost(story, post).map { isSuccessfully ->
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

    override fun finishStory(story: Story, achievementId: String, difficulty: Int) {
        delete(story.id!!)
        database.getReference(FINISHED_STORIES).child(auth.currentUser!!.uid)
                .child(story.id).setValue(story)
        database.getReference("$ACHIEVEMENTS/$MAIN_DATA/$achievementId").runTransaction(
                object : Transaction.Handler{
                    override fun onComplete(databaseError: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                        if (databaseError != null)
                            Log.d(TAG, "finishStory:onComplete error:" + databaseError)
                        else
                            Log.i(TAG, "Finish story completed")
                    }
                    override fun doTransaction(p0: MutableData?): Transaction.Result {
                        val ach = p0!!.getValue(Achievement::class.java)!!
                        ach.unlocked++
                        ach.difficulty += difficulty
                        p0.value = ach
                        return Transaction.success(p0)
                    }

                }
        )
    }


    //StoryPost

    override fun save(post: StoryPost, storyId: String){
        val ref = database.getReference("users/$STORY_POSTS/${userManager.id()}/$storyId")
        if(post.id == null) post.id = ref.push().key
        ref.child(post.id).setValue(post)
    }

    override fun getStoryPosts(storyId: String): Observable<RxRvTransferProtocol<StoryPost>> {
        storyPostsListener.setQuery(database.getReference("users/$STORY_POSTS/${userManager.id()}/$storyId"))
        return storyPostsListener.replaySubject
    }

    override fun deletePost(storyId: String, postId: String): Observable<Boolean> {
        return Observable.create<Boolean> { sub ->
            database.getReference("users/$STORY_POSTS/${userManager.id()}/$storyId/$postId")
                    .setValue(null).addOnCompleteListener { task ->
                sub.onNext(task.isSuccessful)
                sub.onCompleted()
            }
        }
    }

    fun searchAch(request: String): Query {
        return database.getReference(ACHIEVEMENTS_MAIN_DATA).orderByChild("title")
                .startAt(request).endAt(request + "~")
    }


}