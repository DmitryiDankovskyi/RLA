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
import com.vedro401.reallifeachievement.transferProtocols.TransferProtocol
import com.vedro401.reallifeachievement.transferProtocols.SeparatedFieldsTP
import com.vedro401.reallifeachievement.utils.FIRETAG
import com.vedro401.reallifeachievement.utils.STORY
import rx.Observable
import rx.Subscriber
import rx.subjects.AsyncSubject
import rx.subjects.BehaviorSubject


class FirebaseManager(override val userManager: UserManager) : DatabaseManager {
    private val database = FirebaseDatabase.getInstance()
    private val userStatisticListener : SeparatedFieldsListener by lazy {
        SeparatedFieldsListener()
    }
    private val MAIN_DATA = "mainData"
    private val valueEventListenersMap = HashMap<String, HashMap<DatabaseReference, ValueEventListener>>()
    private val childEventListenersMap = HashMap<String, Pair<DatabaseReference, ChildEventListener>>()
    private val listenersCounter = HashMap<String, Int>()


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

                valueEventListenersMap.entries.forEach{

                }
            }
        }
    }
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

    override fun getFinishedStories(): Observable<TransferProtocol<Story>> {
        finishedStoriesListener.setQuery(
                database.getReference("$STORIES/${userManager.uid!!}")
                        .orderByChild("status").equalTo(Story.FINISHED.toDouble()))
        return finishedStoriesListener.source
    }

    override fun getNotFinishedStories(): Observable<TransferProtocol<Story>> {
        notFinishedStoriesListener.setQuery(
                database.getReference("$STORIES/${userManager.uid!!}")
                        .orderByChild("status").endAt(Story.IN_PROGRESS.toDouble()))
//        finishedStoriesListener.source
//                .filter { it.event == TransferProtocol.ITEM_ADDED }
//                .subscribe { notFinishedStoriesListener.source.onNext(
//                        TransferProtocol(it.data!!.uid!!))}
        return notFinishedStoriesListener.source
    }

    override fun getUserFavTags(): Observable<Pair<String, Int>>
            = Observable.create { subscriber ->
        database.getReference("$USER_FAVORITE_TAGS/${userManager.uid}")
                .addListenerForSingleValueEvent(
                        object : ValueEventListener{
                            override fun onDataChange(p0: DataSnapshot) {
                                p0.children.forEach {
                                    subscriber.onNext(Pair(it.key,it.getValue(Int::class.java)!!))
                                }
                                subscriber.onCompleted()
                            }

                            override fun onCancelled(p0: DatabaseError?) {
                                Log.d(FIRETAG,"getUserFavTags onCancelled $p0")
                            }

                        }
                )
    }

    //Achievements

    override fun setId(ach: Achievement) {
        val ref = database.getReference(ACHIEVEMENTS_MAIN_DATA)
        if (ach.id == null) ach.id = ref.push().key
    }

    override fun save(ach: Achievement) {
        var ref = database.getReference(ACHIEVEMENTS_MAIN_DATA)
        setId(ach)
        ref.child(ach.id).setValue(ach)
        ref = database.getReference(ACHIEVEMENTS_TAGS).child(ach.id)
        ref.setValue(null)
        for (tag: String in ach.tags) {
            Log.d(FIRETAG, "tag $tag saved")
            ref.child(tag).setValue(true)
        }
    }

    override fun clear(ach: Achievement) {
        var count = listenersCounter[ach.id]
        if(count == null){
            valueEventListenersMap[ach.id]!!.entries.forEach {
                it.key.removeEventListener(it.value)
            }
        } else {
            count--
            if(count <= 0)
                listenersCounter.remove(ach.id)
            else
                listenersCounter.put(ach.id!!, count)
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
                                            Log.d(FIRETAG, "Second like transaction finished")
                                    }

                                    override fun doTransaction(likesCount: MutableData): Transaction.Result {

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
                        database.getReference("$ACHIEVEMENTS_TAGS/${ach.id}").addListenerForSingleValueEvent(
                                object : ValueEventListener{
                                    override fun onCancelled(p0: DatabaseError?) {
                                        Log.d(FIRETAG, "Like. Getting tags cancelled. $p0")
                                    }

                                    override fun onDataChange(p0: DataSnapshot) {
                                        p0.children.forEach { tag ->
                                            val ref = database.getReference(USER_FAVORITE_TAGS +
                                                    "/${userManager.uid}" +
                                                    "/${tag.key}")
                                            if(liked){
                                                ref.decrement()
                                            } else {
                                                ref.increment()
                                            }
                                        }
                                    }
                                }
                        )


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

    override fun getAchievements(): Observable<TransferProtocol<Achievement>> {
        achievementsListener.setQuery(database.getReference(ACHIEVEMENTS_MAIN_DATA))
        return achievementsListener.source
    }

    override fun getMyAchievements(): Observable<TransferProtocol<Achievement>> {
        myAchievementsListener.setQuery(database.getReference(ACHIEVEMENTS_MAIN_DATA)
                .orderByChild("authorId").equalTo(userManager.uid))
        return myAchievementsListener.source
    }

    override fun initAchievementTags(ach: Achievement): Observable<Boolean> {
        return Observable.create { subscriber: Subscriber<in Boolean> ->
            database.getReference(ACHIEVEMENTS_TAGS).child(ach.id)
                    .addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onCancelled(p0: DatabaseError?) {
                            Log.d(FIRETAG,"initAchievementTags onCancelled $p0" )
                            subscriber.onError(Exception(p0!!.message))
                        }
                        override fun onDataChange(p0: DataSnapshot) {
                            p0.children.forEach {
                                ach.tags.add(it.key)
                            }
                            subscriber.onCompleted()
                        }
                    })

        }
    }

    override fun addNewTags(newTags: ArrayList<String>) {
        val ref = database.getReference(ALL_TAGS)
        newTags.forEach { ref.child(it).increment() }
    }

    override fun removeTags(oldTags: ArrayList<String>) {
        val ref = database.getReference(ALL_TAGS)
        oldTags.forEach { ref.child(it).decrement() }
    }

    override fun isAchievementLiked(ach: Achievement): Observable<Boolean> {
        val response = BehaviorSubject.create<Boolean>()
        val ref = database.getReference("$ACHIEVEMENTS_LIKE_LIST/${ach.id}/${userManager.uid!!}")
        addListener(response,ref,ach.id!!,"isAchievementLiked.onCancelled")
//        valueEventListenersMap.put(path, listener)
        return response
    }

    override fun isAchievementInList(ach: Achievement): Observable<Boolean> {
        val response = BehaviorSubject.create<Boolean>()
        val ref = database.getReference("$STORIES/${userManager.uid!!}/${ach.id}")
        addListener(response,
                ref,
                ach.id!!,
                "isAchievementInFinishedList.onCancelled")
        return response
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

    override fun getStoryPosts(storyId: String): Observable<TransferProtocol<StoryPost>> {
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
                .startAt(request).endAt(request + Character.MAX_VALUE)
    }

    override fun searchTagsTips(request: String) : Observable<TransferProtocol<String>>{
        removeChieldListener("search")
        val ref =  FirebaseDatabase.getInstance().getReference(ALL_TAGS)
        var listener : ChildEventListener
        return  Observable.create<TransferProtocol<String>> { subscriber ->
            listener = object : ChildEventListener{
                override fun onCancelled(p0: DatabaseError?) {}
                override fun onChildMoved(p0: DataSnapshot?, p1: String?) {}
                override fun onChildChanged(p0: DataSnapshot?, p1: String?) {}
                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    val tag : String = p0.key
                   subscriber.onNext(TransferProtocol(TransferProtocol.ITEM_ADDED,
                          tag, p0.key))
                }
                override fun onChildRemoved(p0: DataSnapshot) {
                    subscriber.onNext(TransferProtocol(p0.key))
                }
            }
            ref.orderByKey().startAt(request).endAt(request + Character.MAX_VALUE).addChildEventListener(listener)
            childEventListenersMap["search"] = Pair(ref,listener)
        }

    }

    private fun removeValueListener(key : String){
        valueEventListenersMap[key]?.entries?.forEach {
            it.key.removeEventListener(it.value)
        }
        valueEventListenersMap.remove(key)
    }

    private fun removeChieldListener(key : String){
            childEventListenersMap[key]?.first?.removeEventListener(childEventListenersMap[key]?.second)
        childEventListenersMap.remove(key)
    }

    private fun addListener(response: BehaviorSubject<Boolean>,
                            ref: DatabaseReference,
                            id: String,
                            onCancelledText: String = "addListener.onCancelled"
    ){
        var listener : ValueEventListener? = null
        var needNewListener = true
        if(valueEventListenersMap.containsKey(id)){
            var count = listenersCounter[id]
            if(count == null){
                count = 1
            } else count++
            listenersCounter.put(id, count)
            if(valueEventListenersMap[id]!!.containsKey(ref)) {
                listener = valueEventListenersMap[id]!![ref]!!
                needNewListener = false
            }
        }
        if(needNewListener) {
            listener = object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                    Log.w(FIRETAG, "$onCancelledText ${p0!!.message}")
                }
                override fun onDataChange(p0: DataSnapshot?) {
                    response.onNext(p0!!.value != null)
                }
            }
        }
        ref.addValueEventListener(listener!!)
    }

}
