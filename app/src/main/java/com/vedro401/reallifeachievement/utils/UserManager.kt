package com.vedro401.reallifeachievement.utils

import android.util.Log
import com.vedro401.reallifeachievement.database.DatabaseManager
import com.vedro401.reallifeachievement.model.UserData
import com.vedro401.reallifeachievement.utils.transferProtocols.UserTransferProtocol
import rx.subjects.BehaviorSubject


class UserManager(var dbm: DatabaseManager){
    init {
        dbm.userManager = this
    }
    private  var userData: UserData? = null
    val userStatus = BehaviorSubject.create<Int>(UserTransferProtocol.SIGN_OUT)!!
    var isAuthorised = false

    init {
        dbm.getCurrentUser().subscribe ({
            message ->
            when(message.event){
                UserTransferProtocol.SIGN_IN -> {
                    userData = message.data!!
                    Log.d(LOGTAG,"UserManager: User signed in")
                    Log.d(LOGTAG,"UserManager: $userData")
                    isAuthorised = true

                }
                UserTransferProtocol.SIGN_OUT -> {
                    Log.d(LOGTAG,"UserManager: User signed out")
                    isAuthorised = false
                }
                UserTransferProtocol.CHANGE_NAME -> {
                    Log.d(LOGTAG,"UserManager: User signed out")
                    userData!!.name = message.data!!.name
                }
            }
            userStatus.onNext(message.event)
        },
        { t ->
            Log.e(LOGTAG,"UserManager: ${t.message}")
        })
    }

    fun id() = userData?.id
    fun name() = if(isAuthorised) userData!!.name else "none"
    fun avatarUrl() = userData?.avatarUrl
}