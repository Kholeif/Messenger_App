package com.example.messengerapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.example.messengerapp.glide.GlideApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*

class ChatActivity : AppCompatActivity() {
    val my_uid = FirebaseAuth.getInstance().currentUser!!.uid
    var db = FirebaseFirestore.getInstance()
    var other_uid =""
    var chanel_id =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR   // 3shan yezher elkalam beleswed badal elabyad

        setSupportActionBar(toolbar2)
        supportActionBar!!.title = ""
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        other_uid = intent.getStringExtra("uid")
        val name = intent.getStringExtra("name")
        val path = intent.getStringExtra("path")
        our_title.text=name
        profile_image
        if (path!!.isNotEmpty()) {
            GlideApp.with(this)
                .load(FirebaseStorage.getInstance().getReference(path))
                .into(profile_image)
        }
        create_chat_channel()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        }
        return false
    }

    private fun create_chat_channel(){
        db.collection("users").document(my_uid).collection("chat_channel").document(other_uid).get().addOnSuccessListener{
            if(it.exists()){
                chanel_id = it["channel_ID"] as String
                return@addOnSuccessListener
            }
            val new_chat_channel = db.collection("users").document()
            chanel_id = new_chat_channel.id
            db.collection("users").document(other_uid).collection("chat_channel").document(my_uid).set(mapOf("channel_ID" to chanel_id))
            db.collection("users").document(my_uid).collection("chat_channel").document(other_uid).set(mapOf("channel_ID" to chanel_id))
        }
    }

    fun send_message(view: View) {
        val text = editTextTextPersonName4.text.toString()
        editTextTextPersonName4.setText("")
        val message = TextMessage(text,my_uid ,Calendar.getInstance().time)
        db.collection("chat_channels").document(chanel_id).collection("messages").add(message)
    }

    fun getMessege(channelID:String){

    }
}