package com.example.messengerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_in.button
import kotlinx.android.synthetic.main.activity_sign_in.editTextTextEmailAddress
import kotlinx.android.synthetic.main.activity_sign_in.editTextTextPassword
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignIn : AppCompatActivity() , TextWatcher {

    var mAuth: FirebaseAuth? = null
    var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        mAuth = FirebaseAuth.getInstance()

        editTextTextEmailAddress.addTextChangedListener(this)
        editTextTextPassword.addTextChangedListener(this)
    }
    override fun afterTextChanged(s: Editable?) {}
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        button.isEnabled = editTextTextEmailAddress.text.trim().isNotEmpty()
                && editTextTextPassword.text.trim().isNotEmpty()
    }

    override fun onStart() {
        super.onStart()
        val current_user = mAuth!!.currentUser
        if (current_user != null)
        {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }

    fun go_to_sign_up(view: View) {
        val intent=Intent(this, SignUp::class.java)
        startActivity(intent)
    }

    fun SignIn(view: View) {
        val email = editTextTextEmailAddress.text.toString().trim()
        val password = editTextTextPassword.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextTextEmailAddress.error = "Please enter a valid email"
            editTextTextEmailAddress.requestFocus()
            return
        }
        if (password.length < 6) {
            editTextTextPassword.error = "6 characters required please"
            editTextTextPassword.requestFocus()
            return
        }

        progressBar2.visibility = View.VISIBLE
        mAuth!!.signInWithEmailAndPassword(email,password).addOnCompleteListener {
            if (it.isSuccessful) {
                FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener{ it2 ->
                    val token = it2.result!!.token
                    db.collection("users").document(mAuth!!.currentUser!!.uid).update(mapOf("token" to token))
                }
                Toast.makeText(applicationContext, "done sign in", Toast.LENGTH_LONG).show()
                progressBar2.visibility = View.GONE

                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)     //3shan mat5allenesh a3mel back
                startActivity(intent)

            } else {
                Toast.makeText(applicationContext, it.exception!!.message, Toast.LENGTH_LONG).show()
                progressBar2.visibility = View.GONE
            }
        }
    }
}