package com.example.messengerapp.Service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.messengerapp.ChatActivity
import com.example.messengerapp.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        const val CHANNEL_ID = "CHANNEL_ID"
    }

    override fun onNewToken(p0: String) {   //hatetnafez mogarad eny afta7 el app 3l ghaz awel mara aslan
        Log.d("token", p0)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        if(remoteMessage.notification !=null ){
//            Log.d("notify",remoteMessage.data.toString())
//        }
        val title = remoteMessage.data["title"]
        val body = remoteMessage.data["body"]
        val sender = remoteMessage.data["sender"]
        val sender_name = remoteMessage.data["USER_NAME"]
        val image_path = remoteMessage.data["image_path"]
        create_notification(title, body,sender,sender_name,image_path)
    }

    private fun create_notification(
        title: String?,
        body: String?,
        sender: String?,
        sender_name: String?,
        imagePath: String?
    ) {

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.chat_channel_name)
            val descriptionText = getString(R.string.chat_channel_descriptionText)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
        builder.setSmallIcon(R.drawable.messenger)
        builder.setContentTitle(title)
        builder.setContentText(body)

        val intent = Intent(this,ChatActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        intent.putExtra("uid", sender.toString())
        intent.putExtra("name", sender_name.toString())
        intent.putExtra("path", imagePath.toString())
//        val pendingIntent = PendingIntent.getActivity(this,0,intent,0)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pendingIntent)
        builder.setAutoCancel(true)

        notificationManager.notify(0, builder.build())
    }
}