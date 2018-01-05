package com.vedro401.reallifeachievement.managers

import android.net.Uri
import android.util.Log
import com.vedro401.reallifeachievement.model.UserData
import com.vedro401.reallifeachievement.transferProtocols.UserTransferProtocol
import com.vedro401.reallifeachievement.managers.interfaces.DatabaseManager
import com.vedro401.reallifeachievement.managers.interfaces.UserManager
import com.vedro401.reallifeachievement.utils.LOGTAG
import rx.Observable
import rx.subjects.BehaviorSubject


class FireUserManager(var dbm: DatabaseManager) : UserManager{
    var userData: UserData? = null
    private set

    val userStatus = BehaviorSubject.create<Int>(UserTransferProtocol.SIGN_OUT)!!
    override val isAuthorisedObs = BehaviorSubject.create<Boolean>(false)!!
    override var isAuthorised = false
    override var uid = userData!!.id!!
    override var name = "none"
    override var avatarUrl = Uri.parse(userData!!.avatarUrl)

    init {
        dbm.userManager = this
        dbm.getCurrentUserData().subscribe ({
            message ->
            when(message.event){
                UserTransferProtocol.SIGN_IN -> {
                    userData = message.data
                    Log.d(LOGTAG,"FireUserManager: User signed in")
                    Log.d(LOGTAG,"FireUserManager: $userData")
                    isAuthorised = true

                }
                UserTransferProtocol.SIGN_OUT -> {
                    Log.d(LOGTAG,"FireUserManager: User signed out")
                    isAuthorised = false
                }
                UserTransferProtocol.CHANGE_NAME -> {
                    Log.d(LOGTAG,"FireUserManager: User changed name")
                    userData!!.name = message.data.name
                }
            }
            userStatus.onNext(message.event)
        },
        { t ->
            Log.e(LOGTAG,"FireUserManager: ${t.message}")
        })
    }


}