package com.vedro401.reallifeachievement.FirebaseUtils

import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.vedro401.reallifeachievement.transferProtocols.SeparatedFieldsTP
import com.vedro401.reallifeachievement.utils.FIRETAG
import rx.subjects.ReplaySubject


class SeparatedFieldsListener : ChildEventListener{

    var source = ReplaySubject.create<SeparatedFieldsTP>()!!
        private set

    override fun onCancelled(p0: DatabaseError?) {
        Log.d(FIRETAG, "SeparatedFieldsListener:onCancelled:" + p0)
    }

    override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
        Log.w(FIRETAG, "SeparatedFieldsListener:onChildMoved: moved but not implemented")
    }

    override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
        source.onNext(SeparatedFieldsTP(p0!!.key, p0.value))
    }

    override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
        Log.d(FIRETAG, "SeparatedFieldsListener:onChildAdded:" + p0)
        source.onNext(SeparatedFieldsTP(p0!!.key, p0.value))
    }

    override fun onChildRemoved(p0: DataSnapshot?) {
        source.onNext(SeparatedFieldsTP(p0!!.key, null))
    }

}