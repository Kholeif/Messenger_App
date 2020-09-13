package com.example.messengerapp

import java.util.*

data class TextMessage(val text:String , val sederID :String ,val recipientID:String , val date:Date ) {
    constructor():this("","","" , Date())
}