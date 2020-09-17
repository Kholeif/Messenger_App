package com.example.messengerapp

import java.util.*

data class TextMessage(
    val text: String,
    val senderID: String,
    val recieptientID: String,
    val senderName: String,
    val recieptientName: String,
    val date: Date,
    val sender_image_path: String,
    val type: String
) {
    constructor() : this("", "", "", "", "", Date(), "","")
}