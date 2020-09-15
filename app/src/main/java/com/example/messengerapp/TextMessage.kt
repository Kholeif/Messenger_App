package com.example.messengerapp

import java.util.*

data class TextMessage(val text : String,
                       val senderID : String,
                       val recieptientID : String,
                       val date : Date,
                       val type: String) {
    constructor():this("","","" , Date(),"")
}