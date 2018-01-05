package com.vedro401.reallifeachievement.transferProtocols

import com.vedro401.reallifeachievement.model.UserData

/**
 * Created by someone on 11.10.17.
 */
class UserTransferProtocol(var event : Int, var data: UserData?){
    companion object {
        const val SIGN_IN = 1
        const val SIGN_OUT = 2
        const val CHANGE_NAME = 3
    }
}