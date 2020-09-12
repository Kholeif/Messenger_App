package com.example.messengerapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.messengerapp.R

class MoreFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val ourtitle = activity!!.findViewById<TextView>(R.id.our_title)
        ourtitle.text="Discover"
        return inflater.inflate(R.layout.fragment_more, container, false)
    }
}