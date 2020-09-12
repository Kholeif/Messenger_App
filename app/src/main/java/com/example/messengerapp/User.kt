package com.example.messengerapp

data class User(val name:String,val profileImage:String) {
    constructor():this("","")
}