package com.vedro401.reallifeachievement.model


class UserData() : DataModel() {
    constructor(
    name : String? = null,
    id: String? = null,
    email: String? = null,
    avatarUrl: String? = null
    ) : this() {
        this.name  = name
        this.id = id
        this.email = email
        this.avatarUrl = avatarUrl
    }

    var name : String? = null
    var id: String? = null
    var email: String? = null
    var avatarUrl: String? = null

    fun save(){
        databaseManager.save(this)
    }

    override fun toString(): String {
        return "name $name id $id email $email"
    }
}