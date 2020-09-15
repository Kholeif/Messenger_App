package com.example.messengerapp.RecyclerView

import android.content.Context
import com.example.messengerapp.R
import com.example.messengerapp.User
import com.example.messengerapp.glide.GlideApp
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.recycler_view_item.*

class ChatItems(val uid:String , val user: User , val context: Context) : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView.text = user.name
        viewHolder.textView3.text="Time"
        viewHolder.textView2.text="last message"
        if (user.profileImage!="")
        {
            GlideApp.with(context)
                .load(FirebaseStorage.getInstance().getReference(user.profileImage))
                .into(viewHolder.imageView2)
        }else{
            viewHolder.imageView2.setImageResource(R.drawable.acount_image)
        }
    }

    override fun getLayout(): Int {
        return R.layout.recycler_view_item
    }
}