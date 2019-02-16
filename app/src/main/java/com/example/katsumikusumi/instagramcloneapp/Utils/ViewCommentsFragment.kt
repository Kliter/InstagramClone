package com.example.katsumikusumi.instagramcloneapp.Utils

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.katsumikusumi.instagramcloneapp.R

class ViewCommentsFragment : Fragment() {

    private val TAG : String = "ViewCommentsFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var view : View = inflater.inflate(R.layout.fragment_view_comments, container, false)
        return view
    }
}