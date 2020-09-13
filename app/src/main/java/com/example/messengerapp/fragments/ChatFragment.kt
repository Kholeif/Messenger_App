package com.example.messengerapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messengerapp.ChatActivity
import com.example.messengerapp.MainActivity
import com.example.messengerapp.R
import com.example.messengerapp.RecyclerView.ChatItems
import com.example.messengerapp.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.storage.StorageReference
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.fragment_chat.*

class ChatFragment : Fragment() {

    private var mAuth: FirebaseAuth? = null
    var db = FirebaseFirestore.getInstance()

    private lateinit var chatSection : Section

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        mAuth = FirebaseAuth.getInstance()

        // Inflate the layout for this fragment
        val ourtitle = activity!!.findViewById<TextView>(R.id.our_title)
        ourtitle.text="Chats"

        //listening of chats
        addchatlistener()
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    private fun addchatlistener() : ListenerRegistration {
        return db.collection("users").addSnapshotListener { value, error ->
            if (error!=null){
                return@addSnapshotListener
            }
            val items = mutableListOf<Item>()
            value!!.documents.forEach{
                items.add(ChatItems(it.id , it.toObject(User::class.java)!!,activity!!))
            }
            init_recycler_view(items)
        }
    }

    private fun init_recycler_view( items:List<Item> ){
        chat_recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = GroupAdapter<ViewHolder>().apply {
                chatSection = Section(items)
                add(chatSection)
                setOnItemClickListener(onItemClick)
            }
        }
    }
    val onItemClick = OnItemClickListener{item, view ->
        if (item is ChatItems){
            val intent = Intent(activity, ChatActivity::class.java)
            intent.putExtra("uid" ,item.uid)
            intent.putExtra("name",item.user.name)
            intent.putExtra("path",item.user.profileImage)
            startActivity(intent)
        }
    }
}