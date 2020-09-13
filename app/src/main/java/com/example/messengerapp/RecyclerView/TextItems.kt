package com.example.messengerapp.RecyclerView

import android.content.Context
import android.text.format.DateFormat
import com.example.messengerapp.R
import com.example.messengerapp.TextMessage
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.recycler_view_item_2.*

class TextItems(val textMessage: TextMessage , val messageID : String , val context: Context,val key:String) : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView5.text = textMessage.text
        viewHolder.textView6.text = DateFormat.format("hh:mm a",textMessage.date).toString()
    }

    override fun getLayout(): Int {
        if (key =="1") {
            return R.layout.recycler_view_item_2
        }
        else{
            return R.layout.recycler_view_item_3
        }
    }
}