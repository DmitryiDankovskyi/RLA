package com.vedro401.reallifeachievement.managers

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.vedro401.reallifeachievement.managers.interfaces.UserManager
import com.vedro401.reallifeachievement.model.UserData
import com.vedro401.reallifeachievement.utils.AUTHTAG
import com.vedro401.reallifeachievement.utils.FIRETAG
import rx.Observable
import rx.subjects.BehaviorSubject


class FireUserManager : UserManager{
    var userData: UserData? = null
    private set

    private var user: FirebaseUser? = null

    override val isAuthorisedObs = BehaviorSubject.create<Boolean>()!!
    override var isAuthorised = false
        get() = isAuthorisedObs.value
    override var uid: String? = null
        get() = user?.uid
    override var lastUid: String? = null
    override var name : String? = "none"
        get() = if(user == null) "none" else user!!.displayName

    override var avatarUrl : Uri? = null
        get() = if(userData?.avatarUrl == null) null
        else  Uri.parse(userData?.avatarUrl)

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    init {
        auth.addAuthStateListener({ firebaseAuth ->
            lastUid = user?.uid
            user = firebaseAuth.currentUser
            if(firebaseAuth.currentUser != null){
                isAuthorisedObs.onNext(true)
                Log.d(AUTHTAG, "User signed in")
            }
             else {
                isAuthorisedObs.onNext(false)
                Log.d(AUTHTAG, "User logged out")
            }

        })
    }

    override fun signIn(email: String, pass: String): Observable<String> {
       return Observable.create { subscriber ->
           auth.signInWithEmailAndPassword(email, pass)
                   .addOnCompleteListener { task ->
                       if (task.isSuccessful) {
                           subscriber.onNext("Ok")
                           subscriber.onCompleted()
                           Log.d(FIRETAG, "signIn check call")
                       } else {
                           Log.d(FIRETAG, task.exception!!.message)
                           subscriber.onNext("Sign in: " + task.exception!!.message)
                           subscriber.onCompleted()
                       }
                   }
       }
    }

    override fun signUp(name: String, email: String, pass: String): Observable<String> {
        return Observable.create { subscriber ->
            val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
            auth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser!!
                            user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    Log.d(AUTHTAG, "Update profile failed. ${task.exception.toString()}")
                                } else {
                                    Log.d(AUTHTAG, "Profile info updated.")
                                    Log.d(AUTHTAG, "name $name uid ${user.uid} email $email.")
                                    val ud = UserData(name = name, id = user.uid, email = email)
                                    ud.save()
                                }
                            }
                            subscriber.onNext("Ok")
                            subscriber.onCompleted()
                        } else {
                            Log.d(AUTHTAG, task.exception!!.message)
                            subscriber.onNext("Sign up: " + task.exception!!.message)
                            subscriber.onCompleted()
                        }
                    }
        }
    }

    override fun signOut() {
        auth.signOut()
    }
}