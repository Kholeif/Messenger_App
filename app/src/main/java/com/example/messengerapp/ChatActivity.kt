package com.example.messengerapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.messengerapp.RecyclerView.ImageItems
import com.example.messengerapp.RecyclerView.TextItems
import com.example.messengerapp.glide.GlideApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat.*
import java.io.ByteArrayOutputStream
import java.util.*

class ChatActivity : AppCompatActivity() {
    var a7aaaaaaaaa = ""
    val my_uid = FirebaseAuth.getInstance().currentUser!!.uid
    var db = FirebaseFirestore.getInstance()
    private var mStorageRef: StorageReference? = null

    lateinit var other_uid: String
    private lateinit var chanel_id: String
    val message_adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR   // 3shan yezher elkalam beleswed badal elabyad

        mStorageRef = FirebaseStorage.getInstance().getReference()

        setSupportActionBar(toolbar2)
        supportActionBar!!.title = ""
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        other_uid = intent.getStringExtra("uid")
        val name = intent.getStringExtra("name")
        val path = intent.getStringExtra("path")
        our_title.text = name
        profile_image
        if (path!!.isNotEmpty()) {
            GlideApp.with(this)
                .load(FirebaseStorage.getInstance().getReference(path))
                .into(profile_image)
        }
        create_chat_channel {
            getMesseges(it)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        }
        return false
    }

    fun create_chat_channel(on_complete: (chanel_id: String) -> Unit) {
        db.collection("users").document(my_uid).collection("chat_channel").document(other_uid).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    chanel_id = it["channel_ID"] as String
                    on_complete(chanel_id)
                    return@addOnSuccessListener
                }
                val new_chat_channel = db.collection("users").document()
                chanel_id = new_chat_channel.id
                db.collection("users").document(other_uid).collection("chat_channel").document(my_uid).set(mapOf("channel_ID" to chanel_id))
                db.collection("users").document(my_uid).collection("chat_channel").document(other_uid).set(mapOf("channel_ID" to chanel_id))
                on_complete(chanel_id)
            }
    }

    fun send_message(view: View) {
        val text = editTextTextPersonName4.text.toString()
        if (text.isNotEmpty()) {
            val message = TextMessage(text, my_uid, other_uid, Calendar.getInstance().time ,"TEXT")
            db.collection("chat_channels").document(chanel_id).collection("messages").add(message)
            editTextTextPersonName4.setText("")
        }
    }

    fun getMesseges(chanel_id: String) {
        db.collection("chat_channels").document(chanel_id).collection("messages")
            .orderBy("date", Query.Direction.DESCENDING).addSnapshotListener { value, error ->
                message_adapter.clear()
                if (error != null) {
                    return@addSnapshotListener
                }
                value!!.documents.forEach {
                    if(it["type"]=="TEXT"){
                        val message = it.toObject(TextMessage::class.java)
                        if (message!!.senderID == my_uid) {
                            message_adapter.add(TextItems(message, it.id, this, "1"))
                        } else {
                            message_adapter.add(TextItems(message, it.id, this, "2"))
                        }
                    }
                    else{
                        val message = it.toObject(TextMessage::class.java)
                        if (message!!.senderID == my_uid) {
                            message_adapter.add(ImageItems(message, it.id, this, "1"))
                        } else {
                            message_adapter.add(ImageItems(message, it.id, this, "2"))
                        }
                    }
                }
            }
        init_recycler_view()
    }

    fun init_recycler_view() {
        chat_recyclerView.apply {
            adapter = message_adapter
        }
    }

    fun send_image(view: View) {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("images/jpeg", "image/png"))
        }
        startActivityForResult(Intent.createChooser(intent, "Select Image"), 2)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            upload_image_to_firebase(data)
        }
    }

    fun upload_image_to_firebase(data: Intent) {
        val selected_image_path = data.data
        val selected_image_bitmap =
            MediaStore.Images.Media.getBitmap(this.contentResolver, selected_image_path)
        val output_stream = ByteArrayOutputStream()
        selected_image_bitmap.compress(Bitmap.CompressFormat.JPEG, 25, output_stream)
        val selected_image_bytes = output_stream.toByteArray()
        val ref = mStorageRef!!.child(my_uid).child("images").child(UUID.nameUUIDFromBytes(selected_image_bytes).toString())
        ref.putBytes(selected_image_bytes).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Done uploading to storage firebase", Toast.LENGTH_LONG).show()
                val image_message = TextMessage(ref.path,my_uid,other_uid,Calendar.getInstance().time,"IMAGE")
                db.collection("chat_channels").document(chanel_id).collection("messages").add(image_message)
            } else {
                Toast.makeText(this, "Error uploading to storage firebase : " + it.exception!!.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}

