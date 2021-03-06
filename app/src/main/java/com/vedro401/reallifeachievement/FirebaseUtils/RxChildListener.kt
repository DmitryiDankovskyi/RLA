package com.vedro401.reallifeachievement.FirebaseUtils


import android.util.Log
import com.google.firebase.database.*
import com.vedro401.reallifeachievement.transferProtocols.TransferProtocol
import com.vedro401.reallifeachievement.utils.RXRVTAG
import rx.subjects.ReplaySubject


class RxChildListener<T>(private var itemType: Class<T>) {

    constructor(query: Query, itemType: Class<T>) : this(itemType){
        this.query = query
        setUpListener()
    }

    private var query: Query? = null

    var source = ReplaySubject.create<TransferProtocol<T>>()!!
        private set

    private val childListener: ChildEventListener =
    object : ChildEventListener {
        override fun onCancelled(error: DatabaseError?) {
            Log.d(RXRVTAG, "RxChildListener: Cancelled: ${error?.message}")
            when(error?.code){
            DatabaseError.PERMISSION_DENIED ->
                source.onNext(TransferProtocol(TransferProtocol.PERMISSION_DENIED))
            else -> {
                source.onNext(TransferProtocol(TransferProtocol.UNKNOWN_ERROR))
                Log.w(RXRVTAG,"RxChildListener: Unknown error. Code ${error?.code}")
            }
            }
        }

        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            try {
                source.onNext(TransferProtocol(
                        TransferProtocol.ITEM_ADDED,
                        p0.getValue(itemType)!!, p0.key))
                Log.d(RXRVTAG, "RxChildListener: Item added. key = \"${p0.key}\"")
            }
            catch (npe: NullPointerException){
                Log.w(RXRVTAG, "RxChildListener: ${npe.message}" )
            }
            catch (dr: DatabaseException){
                Log.w(RXRVTAG, "RxChildListener: ${dr.message}" )
            }
        }

        override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
            try{
            source.onNext(TransferProtocol(
                    TransferProtocol.ITEM_CHANGED,
                    p0!!.getValue(itemType)!!, p0.key))
                Log.d(RXRVTAG, "RxChildListener: Item changed. p1 = \"$p1\"")
            }
            catch (npe: NullPointerException){
                Log.w(RXRVTAG, "RxChildListener: ${npe.message}" )
            }
            catch (dr: DatabaseException){
                Log.w(RXRVTAG, "RxChildListener: ${dr.message}" )
            }
        }

        override fun onChildRemoved(p0: DataSnapshot?) =
                source.onNext(TransferProtocol(p0!!.key))

        override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
//                        source.onNext(TransferProtocol(
//                                TransferProtocol.ITEM_MOVED,
//                                p0!!.getValue(itemType)!!))
            //TODO not implemented
        }

    }

    private fun setUpListener(){

        if(query == null){
            Log.e(RXRVTAG,"RxChildListener: query is null")
            return
        }

        query!!.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                        Log.e(RXRVTAG, "RxChildListener: ValueEventListener cancelled ")
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        if (p0?.value == null) {
                            source.onNext(TransferProtocol(TransferProtocol.EMPTY_DATA))
                            Log.d(RXRVTAG, "RxChildListener: Empty signal sent")
                        }
                    }
                }
        )
        query!!.addChildEventListener(childListener)
    }

    fun setQuery(newQuery: Query) {
        query?.removeEventListener(childListener)
        source.onNext(TransferProtocol(TransferProtocol.RESET))
        source.onCompleted()
        source = ReplaySubject.create<TransferProtocol<T>>()!!
        query = newQuery
        setUpListener()
    }

    fun clean(){
        query?.removeEventListener(childListener)
        source.onNext(TransferProtocol(TransferProtocol.EMPTY_DATA))
        source.onCompleted()
        source = ReplaySubject.create<TransferProtocol<T>>()!!
    }
}