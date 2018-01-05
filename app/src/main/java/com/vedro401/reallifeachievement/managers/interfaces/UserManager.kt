package com.vedro401.reallifeachievement.managers.interfaces

import android.net.Uri
import rx.Observable


interface UserManager {
    val isAuthorisedObs : Observable<Boolean>
    var isAuthorised : Boolean
    var uid : String
    var name: String
    var avatarUrl: Uri



}