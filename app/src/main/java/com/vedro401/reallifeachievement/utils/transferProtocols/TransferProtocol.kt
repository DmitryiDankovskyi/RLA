package com.vedro401.reallifeachievement.utils.transferProtocols


class TransferProtocol<T : Any>(var event: Int){
    constructor(event: Int, data: T): this(event){
        this.data = data
    }
    lateinit var data: T

}