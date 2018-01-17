package com.vedro401.reallifeachievement.managers


import android.util.Log
import com.google.firebase.database.*
import com.vedro401.reallifeachievement.FirebaseUtils.*
import com.vedro401.reallifeachievement.managers.interfaces.DatabaseManager
import com.vedro401.reallifeachievement.managers.interfaces.UserManager
import com.vedro401.reallifeachievement.model.Achievement
import com.vedro401.reallifeachievement.model.Story
import com.vedro401.reallifeachievement.model.StoryPost
import com.vedro401.reallifeachievement.model.UserData
import com.vedro401.reallifeachievement.transferProtocols.RxRvTransferProtocol
import com.vedro401.reallifeachievement.transferProtocols.SeparatedFieldsTP
import com.vedro401.reallifeachievement.utils.FIRETAG
import com.vedro401.reallifeachievement.utils.STORY
import rx.Observable
import rx.subjects.AsyncSubject
import rx.subjects.BehaviorSubject


class FirebaseManager(override val userManager: UserManager) : DatabaseManager {
    private val database = FirebaseDatabase.getInstance()
    private val userStatisticListener : SeparatedFieldsListener by lazy {
        SeparatedFieldsListener()
    }
    private val MAIN_DATA = "mainData"
    private val valueEventListenersMap = HashMap<String, ValueEventListener>()

//    private val userProvider = BehaviorSubject.create<TransferProtocol<UserData>>()
    private var achievementsListener = RxChildListener(Achievement::class.java)
    private var myAchievementsListener = RxChildListener(Achievement::class.java)
    private val finishedStoriesListener = RxChildListener(Story::class.java)
    private val notFinishedStoriesListener = RxChildListener(Story::class.java)
    private val storyPostsListener = RxChildListener(StoryPost::class.java)

    init {
        database.setPersistenceEnabled(true)

        userManager.isAuthorisedObs.subscribe{
            isAuthorised ->
            if(!isAuthorised){
                database.getReference("$USERS_STATISTIC_DATA/${userManager.uid}")
                        .removeEventListener(userStatisticListener)
            }
        }
    }


//    private val userStatisticListener = SeparatedFieldsListener()

//    REMOVE ALL THIS SHIT
//    init {
//        auth.addAuthStateListener({ firebaseAuth ->
//            if (firebaseAuth.currentUser != null) {
//                val user = firebaseAuth.currentUser!!
//                if (user.displayName == null)
//                    Log.d(AUTHTAG, "FirebaseManager: user signed in")
//                else
//                    Log.d(AUTHTAG, "FirebaseManager: user signed up")
//                val userData = UserData()
//                userData.name = if (user.displayName == null) "None" else user.displayName!!
//                userData.email = if (user.email == null) "None" else user.email!!
//                userData.id = user.uid
//                userProvider.onNext(TransferProtocol(SIGN_IN, userData))
//            } else {
//                Log.d(AUTHTAG, "FirebaseManager: user logged out")
//                userProvider.onNext(TransferProtocol(SIGN_OUT))
//                database.getReference("$USERS_STATISTIC_DATA/${um.uid}")
//                        .removeEventListener(userStatisticListener)
//            }
//        })
//    }

//    override fun getCurrentUserData(): Observable<TransferProtocol<UserData>> = userProvider
//    REMOVE ALL THIS SHIT

    // User

    override fun save(userData: UserData) {
        database.getReference(USERS_MAIN_DATA).child(userData.id).setValue(userData)
    }
    // #Statistic

    override fun getUserStatisticData(): Observable<SeparatedFieldsTP> {
        database.getReference("$USERS_STATISTIC_DATA/${userManager.uid}")
                .addChildEventListener(userStatisticListener)
        return userStatisticListener.source
    }

    override fun getFinishedStories(): Observable<RxRvTransferProtocol<Story>> {
        finishedStoriesListener.setQuery(
                database.getReference("$STORIES/${userManager.uid!!}")
                        .orderByChild("status").equalTo(Story.FINISHED.toDouble()))
        return finishedStoriesListener.source
    }

    override fun getNotFinishedStories(): Observable<RxRvTransferProtocol<Story>> {
        notFinishedStoriesListener.setQuery(
                database.getReference("$STORIES/${userManager.uid!!}")
                        .orderByChild("status").endAt(Story.IN_PROGRESS.toDouble()))
//        finishedStoriesListener.source
//                .filter { it.event == RxRvTransferProtocol.ITEM_ADDED }
//                .subscribe { notFinishedStoriesListener.source.onNext(
//                        RxRvTransferProtocol(it.data!!.uid!!))}
        return notFinishedStoriesListener.source
    }

    //Achievements

    override fun setId(ach: Achievement) {
        val ref = database.getReference(ACHIEVEMENTS_MAIN_DATA)
        if (ach.id == null) ach.id = ref.push().key
    }

    override fun save(ach: Achievement) {
        val ref = database.getReference(ACHIEVEMENTS_MAIN_DATA)
        setId(ach)
        ref.child(ach.id).setValue(ach)
        for (tag: String in ach.tags) {
            Log.d(FIRETAG, "tag $tag saved")
            database.getReference(ACHIEVEMENTS_TAGS).child(ach.id).child(tag).setValue(true)
        }
    }

    //TODO remove
    fun likeAch(ach: Achievement) {
        Log.d(FIRETAG, "Ach liked. Key: ${ach.id} Title: ${ach.title}")
        if (userManager.isAuthorised) {
            Log.d(FIRETAG, "Ach liked by unauth")
            return
            //TODO call autt alert
        }
        if (ach.id != null) {
            val likeListRef = database.getReference(ACHIEVEMENTS_LIKE_LIST)
            Log.d(FIRETAG, ach.likes.toString())
            likeListRef.child(ach.id).child(userManager.uid!!)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError?) {
                            Log.d(FIRETAG, "like call canceled")
                        }

                        override fun onDataChange(p0: DataSnapshot?) {
                            Log.d(FIRETAG, "like call data snapshot: $p0")
                            var likedBefore: Boolean

                            database.getReference(ACHIEVEMENTS_MAIN_DATA).child(ach.id).child(ACH_LIKES)
                                    .runTransaction(object : Transaction.Handler {
                                        override fun doTransaction(mutableData: MutableData?): Transaction.Result {
                                            likedBefore = p0!!.value == null
                                            val v: Boolean? = if (likedBefore) true else null
                                            likeListRef.child(ach.id).child(userManager.uid!!)
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
                                            Log.d(FIRETAG, "postTransaction:onComplete:" + databaseError)
                                        }

                                    })
                        }
                    })
        } else {
            Log.e(FIRETAG, "Offline achievement liked name: ${ach.title} short description ${ach.shortDescription}")
        }
    }

    override fun likeAchievement(ach: Achievement) {

        Log.i(FIRETAG, "FirebaseManager: Ach liked. Title \"${ach.title}\"")

        if (ach.id == "") {
            Log.e(FIRETAG, "Liked achievement with no uid. Title \"${ach.title}\"")
            return
        }

        if (!userManager.isAuthorised) {
            Log.w(FIRETAG, "Unauthorized user liked achievement \"${ach.title}\"")
            //TODO make alert
            return
        }

        database.getReference(ACHIEVEMENTS_LIKE_LIST).child(ach.id).runTransaction(
                object : Transaction.Handler {
                    override fun doTransaction(likeList: MutableData?): Transaction.Result {
                        val uid = userManager.uid!!
                        var liked = false
                        if (likeList == null) {
                            Log.d(FIRETAG, "likeList is null")
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
                        Log.d(FIRETAG, "Is liked before $liked")

                        database.getReference(ACHIEVEMENTS_MAIN_DATA).child(ach.id).child(ACH_LIKES)
                                .runTransaction(object : Transaction.Handler {
                                    override fun onComplete(databaseError: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                                        if (databaseError != null)
                                            Log.d(FIRETAG, "postTransaction:onComplete error:" + databaseError)
                                        else
                                            Log.d(FIRETAG, "Second transaction finished")
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
                            Log.d(FIRETAG, "postTransaction:onComplete error:" + databaseError)
                        else
                            Log.d(FIRETAG, "First transaction finished")
                    }
                }
        )

    }

    override fun getAchievements(): Observable<RxRvTransferProtocol<Achievement>> {
        achievementsListener.setQuery(database.getReference(ACHIEVEMENTS_MAIN_DATA))
        return achievementsListener.source
    }

    override fun getMyAchievements(): Observable<RxRvTransferProtocol<Achievement>> {
        myAchievementsListener.setQuery(database.getReference(ACHIEVEMENTS_MAIN_DATA)
                .orderByChild("author").equalTo(userManager.uid))
        return myAchievementsListener.source
    }

    override fun isAchievementLiked(ach: Achievement): Observable<Boolean> {
        val response = BehaviorSubject.create<Boolean>()
        val listener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                Log.w(FIRETAG, "isAchievementLiked.onCancelled ${p0!!.message}")
            }

            override fun onDataChange(p0: DataSnapshot?) {
                response.onNext(p0!!.value != null)
            }
        }
        database.getReference("$ACHIEVEMENTS_LIKE_LIST/${ach.id}/${userManager.uid!!}")
                .addValueEventListener(listener)
        valueEventListenersMap.put("ach_${ach.id}", listener)
        return response
    }

    override fun isAchievementInList(ach: Achievement): Observable<Boolean> {
        val response = BehaviorSubject.create<Boolean>()
        val inListListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                Log.w(FIRETAG, "isAchievementInFinishedList.onCancelled ${p0!!.message}")
            }

            override fun onDataChange(p0: DataSnapshot?) {
                response.onNext(p0!!.value != null)
                Log.w(FIRETAG, "inFinishedlistener: ${ach.title} $p0")
            }
        }
        database.getReference("$STORIES/${userManager.uid!!}/${ach.id}")
                .addValueEventListener(inListListener)

        valueEventListenersMap.put("$ACH_IN_LIST_PREFIX${ach.id}", inListListener)
        return response
    }

    override fun clear(ach: Achievement) {
        database.getReference("$ACHIEVEMENTS_LIKE_LIST/${ach.id}/${userManager.uid}")
                .removeEventListener(valueEventListenersMap["$ACH_LIKE_PREFIX${ach.id}"])

    }

    // #Story

    override fun save(story: Story) {
        if (!userManager.isAuthorised) {
            Log.d(FIRETAG, "FirebaseManage: unauthorized user tried to create story")
            return
        }
        database.getReference(STORIES)
                .child(userManager.uid!!).child(story.id).setValue(story)
    }

    override fun delete(story: Story) {
        if (!userManager.isAuthorised) {
            Log.d(FIRETAG, "FirebaseManage: unauthorized user tried to create story")
            return
        }

        if(story.status == Story.FINISHED){
            database.getReference("$USERS_STATISTIC_DATA/${userManager.uid}/$FINISHED_STORIES_COUNT")
                    .decrement()
            database.getReference("$ACHIEVEMENTS/$MAIN_DATA/${story.id}").runTransaction(
                    object : Transaction.Handler {
                        override fun onComplete(databaseError: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                            if (databaseError != null)
                                Log.d(FIRETAG, "delete(Story):onComplete error:" + databaseError)
                            else
                                Log.i(FIRETAG, "Delete story completed")
                        }

                        override fun doTransaction(p0: MutableData?): Transaction.Result {
                            val ach = p0!!.getValue(Achievement::class.java)!!
                            ach.unlocked--
                            ach.difficulty -= story.difficulty!!
                            p0.value = ach
                            return Transaction.success(p0)
                        }
                    }
            )
        }

        database.getReference("$USERS_STATISTIC_DATA/${userManager.uid}/$POST_COUNT")
                .decrement(story.postsCount)

        database.getReference(STORIES).child(userManager.uid!!).child(story.id!!)
                .removeValue()

        database.getReference(STORY_POSTS).child(userManager.uid!!).child(story.id!!)
                .removeValue()
    }

    override fun updateStoryStatus(story: Story, newStatus: Int) {
        database.getReference("$STORIES/${userManager.uid}/${story.id}/status")
                .setValue(newStatus)
    }

    override fun updateLastPost(story: Story, post: StoryPost?): Observable<Boolean> {
        val async = AsyncSubject.create<Boolean>()
        database.getReference("$STORIES/${userManager.uid}/${story.id}/lastPost")
                .setValue(post).addOnCompleteListener { task ->
            async.onNext(task.isSuccessful)
            async.onCompleted()
            if (!task.isSuccessful) {
                Log.d(STORY, "FirebaseManager.updateLastPost: ${task.exception?.message}")
            }
        }
        return async
    }

    override fun updateLastPost(story: Story): Observable<StoryPost?> {
        val async = AsyncSubject.create<StoryPost?>()
        database.getReference("$STORY_POSTS/${userManager.uid}/${story.id!!}").limitToLast(1)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                        Log.d(STORY, "FirebaseManager.updateLastPost: Cancelled ${p0?.message}")
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        var post: StoryPost? = null
                        if (p0!!.value != null) {
                            post = p0.children.first().getValue(StoryPost::class.java)
                        }
                        updateLastPost(story, post).map { isSuccessfully ->
                            if (isSuccessfully)
                                post
                            else {
                                null
                            }
                        }.subscribe(async)
                    }
                })
        return async
    }

    override fun finishStory(story: Story, difficulty: Int) {
        save(story)
        database.getReference("$USERS_STATISTIC_DATA/${userManager.uid}/$FINISHED_STORIES_COUNT")
                .increment()
        database.getReference("$ACHIEVEMENTS/$MAIN_DATA/${story.id}").runTransaction(
                object : Transaction.Handler {
                    override fun onComplete(databaseError: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                        if (databaseError != null)
                            Log.d(FIRETAG, "finishStory:onComplete error:" + databaseError)
                        else
                            Log.i(FIRETAG, "Finish story completed")
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


    // #StoryPost

    override fun save(post: StoryPost, storyId: String) {
        val ref = database.getReference("$STORY_POSTS/${userManager.uid}/$storyId")
        if (post.id == null) {
            post.id = ref.push().key
            database.getReference("$USERS_STATISTIC_DATA/${userManager.uid}/$POST_COUNT")
                    .increment()
            database.getReference("$STORIES/${userManager.uid}/$storyId/$POST_COUNT")
                    .increment()
        }
        ref.child(post.id).setValue(post)
    }

    override fun getStoryPosts(storyId: String): Observable<RxRvTransferProtocol<StoryPost>> {
        storyPostsListener.setQuery(database.getReference("$STORY_POSTS/${userManager.uid}/$storyId"))
        return storyPostsListener.source
    }

    override fun deletePost(storyId: String, postId: String): Observable<Boolean> {
        return Observable.create<Boolean> { sub ->
            database.getReference("$STORY_POSTS/${userManager.uid}/$storyId/$postId")
                    .setValue(null).addOnCompleteListener { task ->
                sub.onNext(task.isSuccessful)
                sub.onCompleted()
                database.getReference("$USERS_STATISTIC_DATA/${userManager.uid}/$POST_COUNT")
                        .decrement()
                database.getReference("$STORIES/${userManager.uid}/$storyId/$POST_COUNT")
                        .decrement()
            }
        }
    }

    fun searchAch(request: String): Query {
        return database.getReference(ACHIEVEMENTS_MAIN_DATA).orderByChild("title")
                .startAt(request).endAt(request + "~")
    }

}
