package com.example.messengerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import com.example.messengerapp.fragments.ChatFragment
import com.example.messengerapp.fragments.MoreFragment
import com.example.messengerapp.fragments.PeopleFragment
import com.example.messengerapp.glide.GlideApp
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener {

    var mAuth: FirebaseAuth? = null
    var db = FirebaseFirestore.getInstance()

    private val mChatFragment = ChatFragment()
    private val mPeopleFragment = PeopleFragment()
    private val mMoreFragment = MoreFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR   // 3shan yezher elkalam beleswed badal elabyad

        mAuth = FirebaseAuth.getInstance()

        setSupportActionBar(toolbar)
        supportActionBar!!.title=""

        bottomNavigationView.setOnNavigationItemSelectedListener(this)
        setFragment(mChatFragment)
        getuserinfo()
    }

    override fun onResume() {
        super.onResume()
        getuserinfo()
    }
    override fun onStart() {
        super.onStart()
        getuserinfo()
    }

    private fun getuserinfo() {
        val uid = mAuth!!.currentUser!!.uid
        db.document("users/$uid").get().addOnSuccessListener {
            val user = it.toObject(User::class.java)!!
            val path = user.profileImage
            if (path.isNotEmpty()) {
                GlideApp.with(this)
                    .load(FirebaseStorage.getInstance().getReference(path))
                    .into(profile_image)
            }
        }
    }

    //menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu , menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if(id==R.id.sign_out){
            mAuth!!.signOut()
            val intent = Intent(this, SignIn::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)     //3shan mat5allenesh a3mel back
            startActivity(intent)
        }
        if (id==R.id.new_message){
            val intent=Intent(this,SearchActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
    //end menu

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.chat -> {
                setFragment(mChatFragment)
                return true
            }
            R.id.people -> {
                setFragment(mPeopleFragment)
                return true
            }
            R.id.more -> {
                setFragment(mMoreFragment)
                return true
            }
            else -> return false
        }
    }
    private fun setFragment(fragment : Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.lallo , fragment)
        transaction.commit()
    }

    fun gotoprofile(view: View) {
        val intent = Intent(this,ProfileActivity::class.java)
        startActivity(intent)
    }

}