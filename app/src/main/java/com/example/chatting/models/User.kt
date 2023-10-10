package com.example.chatting.models

import android.provider.ContactsContract.CommonDataKinds.Email

class User {
    var name : String? = null
    var email: String? = null
    var uid: String? = null
    var imgUrl: String? = null
    var FCMToken: String? = null

    constructor(){}

    constructor(name : String?, email: String?, uid: String?,imageName: String?){
        this.email = email
        this.name = name
        this.uid = uid
        this.imgUrl = imageName
    }

    constructor(name: String?, email: String?, uid: String?, imgUrl: String?, FCMToken: String?) {
        this.name = name
        this.email = email
        this.uid = uid
        this.imgUrl = imgUrl
        this.FCMToken = FCMToken
    }
//    fun get(): String? {
//        return FCMToken
//    }
//
//    fun set(token: String?){
//        this.FCMToken = token
//    }
}