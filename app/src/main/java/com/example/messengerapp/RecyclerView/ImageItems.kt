package com.example.messengerapp.RecyclerView

import android.content.Context
import android.text.format.DateFormat
import com.example.messengerapp.R
import com.example.messengerapp.TextMessage
import com.example.messengerapp.glide.GlideApp
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.recycler_view_item_4.*

class ImageItems(val textMessage: TextMessage , val messageID : String , val context: Context , val key:String) : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        val path = textMessage.text
        viewHolder.textView7.text = DateFormat.format("hh:mm a",textMessage.date).toString()
        if(path.isNotEmpty()){
            GlideApp.with(context)
                .load(FirebaseStorage.getInstance().getReference(path))
                .placeholder(R.drawable.insert_photo)
                .into(viewHolder.imageView4)
        }
    }

    override fun getLayout(): Int {
        if (key =="1") {
            return R.layout.recycler_view_item_4
        }
        else{
            return R.layout.recycler_view_item_5
        }
    }
}