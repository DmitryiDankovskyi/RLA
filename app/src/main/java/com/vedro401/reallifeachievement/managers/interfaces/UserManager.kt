package com.vedro401.reallifeachievement.managers.interfaces

import android.net.Uri
import rx.Observable


interface UserManager {
    val isAuthorisedObs : Observable<Boolean>
    var isAuthorised : Boolean
    var uid : String?
    var lastUid: String?
    var name: String?
    var avatarUrl: Uri?

    fun signIn(email: String, pass: String): Observable<String>
    fun signUp(name: String, email: String, pass: String): Observable<String>
    fun signOut()



}