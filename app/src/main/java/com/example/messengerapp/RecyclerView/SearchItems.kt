package com.example.messengerapp.RecyclerView

import android.content.Context
import com.example.messengerapp.R
import com.example.messengerapp.User
import com.example.messengerapp.glide.GlideApp
import com.google.firebase.storage.FirebaseStorage
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.recycler_view_item_6.*

class SearchItems(val uid:String , val user: User, val context: Context) : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.textView.text = user.name
        if (user.profileImage != "") {
            GlideApp.with(context)
                .load(FirebaseStorage.getInstance().getReference(user.profileImage))
                .into(viewHolder.imageView2)
        } else {
            viewHolder.imageView2.setImageResource(R.drawable.acount_image)
        }

    }

    override fun isSameAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if(other !is SearchItems){
            return false
        }
        if (this.user != other.user){
            return false
        }
        return true
    }

    override fun hashCode(): Int {
        var result = user.hashCode()
        result = 31 * result + context.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        return isSameAs(other as SearchItems)
    }

    override fun getLayout(): Int {
        return R.layout.recycler_view_item_6
    }
}