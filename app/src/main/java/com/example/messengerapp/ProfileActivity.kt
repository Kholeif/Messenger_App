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
import com.example.messengerapp.glide.GlideApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.ByteArrayOutputStream
import java.util.*


class ProfileActivity : AppCompatActivity() {

    var name: String? = null
    var path: String? = null
    private var mAuth: FirebaseAuth? = null
    val my_uid = FirebaseAuth.getInstance().currentUser!!.uid
    var db = FirebaseFirestore.getInstance()
    private var mStorageRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        mAuth = FirebaseAuth.getInstance()
        mStorageRef =
            FirebaseStorage.getInstance().getReference().child(mAuth!!.currentUser!!.uid.toString())

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR   // 3shan yezher elkalam beleswed badal elabyad

        setSupportActionBar(toolbar_profile)
        supportActionBar!!.title = "Me"
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        getuserinfo()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        }
        return false
    }

    fun choose_photo(view: View) {
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
            imageView3.setImageURI(data.data)
            upload_image_to_firebase(data)
        }
    }

    fun upload_image_to_firebase(data: Intent) {
        progressBar3.visibility = View.VISIBLE
        val selected_image_path = data.data
        val selected_image_bitmap =
            MediaStore.Images.Media.getBitmap(this.contentResolver, selected_image_path)
        val output_stream = ByteArrayOutputStream()
        selected_image_bitmap.compress(Bitmap.CompressFormat.JPEG, 20, output_stream)
        val selected_image_bytes = output_stream.toByteArray()
        val ref = mStorageRef!!.child("Profile_Pictures")
            .child(UUID.nameUUIDFromBytes(selected_image_bytes).toString())
        ref.putBytes(selected_image_bytes).addOnCompleteListener {
            if (it.isSuccessful) {
                ta3del_7aga_felfirestore(ref.path)
                progressBar3.visibility = View.INVISIBLE
//                Toast.makeText(this, "Done uploading to storage firebase", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(
                    this,
                    "Error uploading to storage firebase : " + it.exception!!.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun getuserinfo() {
        progressBar3.visibility = View.VISIBLE
        val uid = mAuth!!.currentUser!!.uid
        db.document("users/$uid").get().addOnSuccessListener {
            val user = it.toObject(User::class.java)!!
            name = user.name
            textView4.text = name
            path = user.profileImage
            if (path!!.isNotEmpty()) {
                GlideApp.with(this)
                    .load(FirebaseStorage.getInstance().getReference(path!!))
                    .placeholder(R.drawable.acount_image)
                    .into(imageView3)
            }
            progressBar3.visibility = View.INVISIBLE
        }
    }

    private fun ta3del_7aga_felfirestore(it: String) {
        val uid = mAuth!!.currentUser!!.uid
        val userfieldmap = mutableMapOf<String, Any>()
        userfieldmap["name"] = name!!
        userfieldmap["profileImage"] = it
        db.document("users/$uid").update(userfieldmap)


        //update omken yetmese7 mn awel hena
        db.collection("users").addSnapshotListener { value, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            value!!.documents.forEach { it2 ->
                if (it2.id != my_uid) {
                    db.collection("users").document(it2.id).collection("chat_channel")
                        .document(my_uid).update(mapOf("profileImage" to it))
                }
            }
        }
    }

    fun sign_out(view: View) {
        mAuth!!.signOut()
        val intent = Intent(this, SignIn::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)     //3shan mat5allenesh a3mel back
        startActivity(intent)
    }

}