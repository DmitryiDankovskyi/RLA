package com.vedro401.reallifeachievement.FirebaseUtils

import android.util.Log
import com.google.firebase.database.*
import com.vedro401.reallifeachievement.transferProtocols.RxRvTransferProtocol
import com.vedro401.reallifeachievement.utils.RXRVTAG
import rx.subjects.ReplaySubject
import java.util.*

class RxIndexedChildListener<T>(private var itemType: Class<T>) {
    var keysRef: Query? = null
    var dataRef: DatabaseReference? = null
    var subj = ReplaySubject.create<RxRvTransferProtocol<T>>()
    val listeningKeys = HashSet<String>()
    private val itemListener = object : ValueEventListener {
        val addedIdList = LinkedList<String>()
        override fun onCancelled(p0: DatabaseError?) {
            Log.d(RXRVTAG, "RxIndexedChildListener.itemListener: cancelled. ${p0?.message}")
        }

        override fun onDataChange(p0: DataSnapshot) {
            if (p0.value == null) {
                Log.e(RXRVTAG, "RxIndexedChildListener.itemListener: empty value. key: ${p0.key}")
                return
            }
            val dataItem = p0.getValue(itemType)
            if (dataItem == null) {
                Log.e(RXRVTAG, "RxIndexedChildListener.itemListener: " +
                        "Wrong data type. key: ${p0.key}. value: \"${p0.value}\"")
                return
                /**TODO it can be case when removed data, you should remove key
                 * from pinned list and, maybe, tell about it to user
                 */
            }
            val event = if(!addedIdList.contains(p0.key)) {
                addedIdList.add(p0.key)
                RxRvTransferProtocol.ITEM_ADDED
            }
            else RxRvTransferProtocol.ITEM_CHANGED
            subj.onNext(RxRvTransferProtocol(event, dataItem, p0.key))
        }
    }

    private val keyListener = object : ChildEventListener {
        override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
            val key = p0!!.key
            Log.d(RXRVTAG, "RxIndexedChildListener.keyListener: item added. key \'$key\'")
            listeningKeys.add(key)
            dataRef!!.child(key).addValueEventListener(itemListener)
        }

        override fun onCancelled(p0: DatabaseError?) {
            Log.d(RXRVTAG, "RxIndexedChildListener.keyListener: cancelled. ${p0?.message}")
        }

        override fun onChildMoved(p0: DataSnapshot?, p1: String?) {}
        override fun onChildChanged(p0: DataSnapshot?, p1: String?) {}

        override fun onChildRemoved(p0: DataSnapshot?) {
            val key = p0!!.key
            listeningKeys.remove(key)
            dataRef!!.child(key).removeEventListener(itemListener)
            subj.onNext(RxRvTransferProtocol(key))
        }

    }

    private val isEmptyListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError?) {
            Log.d(RXRVTAG, "RxIndexedChildListener.isEmptyListener: cancelled. ${p0?.message}")
        }

        override fun onDataChange(p0: DataSnapshot?) {
            if (p0?.value == null) {
                subj.onNext(RxRvTransferProtocol(RxRvTransferProtocol.EMPTY_DATA))
            }
        }
    }

    constructor(keysRef: Query, dataRef: DatabaseReference, itemType: Class<T>)
            : this(itemType) {
        setRefs(keysRef, dataRef)
    }

    fun setRefs(newKeysRef: Query, newDataRef: DatabaseReference) {
        keysRef?.removeEventListener(keyListener)
        listeningKeys.forEach { key ->
            dataRef!!.child(key).removeEventListener(itemListener)
        }
        subj.onNext(RxRvTransferProtocol(RxRvTransferProtocol.RESET))
        subj.onCompleted()
        subj = ReplaySubject.create<RxRvTransferProtocol<T>>()!!
        keysRef = newKeysRef
        dataRef = newDataRef
        keysRef!!.addListenerForSingleValueEvent(isEmptyListener)
        keysRef!!.addChildEventListener(keyListener)
    }

    fun clean(){
        keysRef?.removeEventListener(keyListener)
        listeningKeys.forEach { key ->
            dataRef!!.child(key).removeEventListener(itemListener)
        }
        subj.onCompleted()
    }

}