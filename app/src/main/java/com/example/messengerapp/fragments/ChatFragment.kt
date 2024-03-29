package com.example.messengerapp.fragments

import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messengerapp.ChatActivity
import com.example.messengerapp.R
import com.example.messengerapp.RecyclerView.ChatItems
import com.example.messengerapp.TextMessage
import com.example.messengerapp.User
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

class ChatFragment : Fragment() {

    val my_uid = FirebaseAuth.getInstance().currentUser!!.uid
    var db = FirebaseFirestore.getInstance()

    private lateinit var chatSection: Section

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val ourtitle = activity!!.findViewById<TextView>(R.id.our_title)
        ourtitle.text = "Chats"

        //listening of chats
        addchatlistener()
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }


    // 3shan ye3red elnas elly fe chats mabeny w mabenhom bssss
    private fun addchatlistener(): ListenerRegistration {
        return db.collection("users").document(my_uid).collection("chat_channel").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                val items = mutableListOf<Item>()
                var text = "hello"
                var date = "hello"
                var seen = "hello"

                value!!.documents.forEach {
                    if (it.exists()) {

                        val textMessage = it.toObject(TextMessage::class.java)!!
                        seen = it["seen"].toString()

                        if (textMessage.senderID == my_uid) {
                            if (textMessage.type == "IMAGE") {
                                text = "You sent an image"
                            } else {
                                text = "You : " + textMessage.text
                            }
                        } else {
                            if (textMessage.type == "IMAGE") {
                                text =
                                    textMessage.senderName.substringBefore(" ") + " sent you an image"
                            } else {
                                text =
                                    textMessage.senderName.substringBefore(" ") + " : " + textMessage.text
                            }
                        }

                        date = DateFormat.format("hh:mm a", textMessage.date).toString()

                        items.add(
                            ChatItems(
                                it.id,
                                it.toObject(User::class.java)!!,
                                text,
                                date,
                                seen,
                                activity
                            )
                        )
                    }
                }
                init_recycler_view(items)
            }
    }

    private fun init_recycler_view(items: List<Item>) {
        chat_recycler_view.apply {
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
            activity!!.startActivity(intent)
        }
    }
}