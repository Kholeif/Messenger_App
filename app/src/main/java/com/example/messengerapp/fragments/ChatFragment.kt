package com.example.messengerapp.fragments

import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messengerapp.*
import com.example.messengerapp.RecyclerView.ChatItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.OnItemClickListener
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_chat.view.*

class ChatFragment : Fragment() {

    val my_uid = FirebaseAuth.getInstance().currentUser!!.uid
    var db = FirebaseFirestore.getInstance()

    private lateinit var chatSection: Section

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =inflater.inflate(R.layout.fragment_chat, container, false)

        view.imageView.setOnClickListener{
            val intent=Intent(activity,SearchActivity::class.java)
            startActivity(intent)
        }

        // Inflate the layout for this fragment
        val ourtitle = activity!!.findViewById<TextView>(R.id.our_title)
        ourtitle.text = "Chats"

        //listening of chats
        addchatlistener()
        return view
    }

    override fun onResume() {
        super.onResume()
        addchatlistener()
    }

//    private fun addchatlistener(): ListenerRegistration {
//        return db.collection("users").addSnapshotListener { value, error ->
//            if (error != null) {
//                return@addSnapshotListener
//            }
//            val items = mutableListOf<Item>()
//            value!!.documents.forEach {
//                val user = it.toObject(User::class.java)!!
//                if (it.id != my_uid) {
//                    db.collection("users").document(my_uid).collection("chat_channel").document(it.id).get().addOnSuccessListener { it2 ->
//                        if (it2.exists()) {
//                            val textMessage = it2.toObject(TextMessage::class.java)
//                            var text:String?=null
//                            if (textMessage!!.senderID == my_uid){
//                                text = "You : "+textMessage.text
//                            }
//                            else{
//                                text = "Him : "+textMessage.text
//                            }
//                            val date = DateFormat.format("hh:mm a",textMessage.date).toString()
//                            items.add(ChatItems(it.id, user, text, date, activity!!))
//                        }
//                        init_recycler_view(items)
//                    }
////                    items.add(ChatItems( it.id , user ,activity!!))
//                }
//            }
////            init_recycler_view(items)
//        }
//    }

    // 3shan ye3red elnas elly fe chats mabeny w mabenhom bssss
    private fun addchatlistener(): ListenerRegistration {
        return db.collection("users").document(my_uid).collection("chat_channel")
            .orderBy("date", Query.Direction.DESCENDING).addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                val items = mutableListOf<Item>()

                value!!.documents.forEach {
                    if (it.exists()) {
                        val textMessage = it.toObject(TextMessage::class.java)
                        var text: String? = null
                        if (textMessage!!.senderID == my_uid) {
                            text = "You : " + textMessage.text
                        } else {
                            text = "Him : " + textMessage.text
                        }
                        val date = DateFormat.format("hh:mm a", textMessage.date).toString()
                            db.collection("users").document(it.id).get().addOnSuccessListener {it2 ->
                                val user = it2.toObject(User::class.java)!!
                                items.add(ChatItems(it2.id, user, text, date, activity!!))
                                init_recycler_view(items)
                            }
                    }
                }
            }
    }

    private fun init_recycler_view(items: List<Item>) {
        chat_recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = GroupAdapter<ViewHolder>().apply {
                chatSection = Section(items)
                add(chatSection)
                setOnItemClickListener(onItemClick)
            }
        }
    }

    val onItemClick = OnItemClickListener { item, view ->
        if (item is ChatItems) {
            val intent = Intent(activity, ChatActivity::class.java)
            intent.putExtra("uid", item.uid)
            intent.putExtra("name", item.user.name)
            intent.putExtra("path", item.user.profileImage)
            startActivity(intent)
        }
    }
}