package com.vedro401.reallifeachievement.FirebaseUtils

import android.util.Log
import com.google.firebase.database.*
import com.vedro401.reallifeachievement.utils.FIRETAG

fun DatabaseReference.increment(step: Int = 1){
    this.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData?): Transaction.Result {
                mutableData!!.value = (mutableData.getValue(Int::class.java) ?: 0) + step

                return Transaction.success(mutableData)
            }
            override fun onComplete(databaseError: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                if(databaseError != null)
                    Log.d(FIRETAG, "IncrementTH:onComplete:" + databaseError)
            }
    })
}

fun DatabaseReference.decrement(step: Int = 1){
    this.runTransaction(object : Transaction.Handler {
        override fun doTransaction(mutableData: MutableData?): Transaction.Result {
            if(mutableData!!.value == null) {
                Log.w(FIRETAG, "DecrementTH:doTransaction: decremented value is null")
                return Transaction.abort()
            }
            mutableData.value = mutableData.getValue(Int::class.java)!! - step
            return Transaction.success(mutableData)
        }
        override fun onComplete(databaseError: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
            if(databaseError != null)
                Log.d(FIRETAG, "DecrementTH:onComplete:" + databaseError)
        }
    })
}



