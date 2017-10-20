package com.vedro401.reallifeachievement.adapters.RxRvAdapter


class RxRvTransferProtocol<T>(var event : Int){
    lateinit var dataSet : ArrayList<T>

    var data : T? = null
    var id = ""
    constructor(event : Int, data : T, id : String): this(event){
        this.event = event
        this.data = data
        this.id = id
    }

    //when item removed
    constructor(idOfRemovedItem : String): this(ITEM_REMOVED){
        this.id = idOfRemovedItem
    }

    //when you wont to set all list
    constructor(dataSet : ArrayList<T>) : this(FULL_DATA_SET) {
        this.dataSet = dataSet
    }
    companion object {
        const val FULL_DATA_SET = 0
        const val ITEM_ADDED    = 1
        const val ITEM_CHANGED  = 2
        const val ITEM_REMOVED  = 3
        const val ITEM_MOVED    = 4
        const val EMPTY_DATA    = 5
        const val RESET         = 6
        const val PERMISSION_DENIED = -1
        const val UNKNOWN_ERROR = -666

    }
}