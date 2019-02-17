package com.example.katsumikusumi.instagramcloneapp.Utils

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import com.example.katsumikusumi.instagramcloneapp.Models.Comment
import com.example.katsumikusumi.instagramcloneapp.Models.CommentsListAdapter
import com.example.katsumikusumi.instagramcloneapp.Models.Photo
import com.example.katsumikusumi.instagramcloneapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*



class ViewCommentsFragment() : Fragment() {

    private val TAG : String = "ViewCommentsFragment"

    //vars
    private var mPhoto : Photo? = null
    private var mComments: ArrayList<Comment>? = null

    //widgets
    private var mBackArrow : ImageView? = null
    private var mCheckMark : ImageView? = null
    private var mComment: EditText? = null
    private var mListView: ListView? = null

    //firebase
    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var mFirebaseDatabase: FirebaseDatabase? = null
    private var myRef: DatabaseReference? = null

    init {
        arguments = Bundle()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view : View = inflater.inflate(R.layout.fragment_view_comments, container, false)
        mBackArrow = view.findViewById(R.id.backArrow) as ImageView?
        mCheckMark = view.findViewById(R.id.ivPostCommnent) as ImageView?
        mComment = view.findViewById(R.id.comment) as EditText?
        mComments = ArrayList()
        mListView = view.findViewById(R.id.listView)

        setupFirebaseAuth()

        try {
            mPhoto = getPhotoFromBundle()
        } catch (e: NullPointerException) {
            Log.e(TAG, "onCreateView: NullPointerException: " + e.message)
        }

        val firstComment : Comment = Comment()
        firstComment.comment = mPhoto?.caption
        firstComment.user_id = mPhoto?.user_id
        firstComment.date_created = mPhoto?.date_created

        mComments?.add(firstComment)
        val adapter : CommentsListAdapter = CommentsListAdapter(context, R.layout.layout_comment, mComments)
        mListView?.adapter = adapter

        mCheckMark?.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                if (!mComment?.text.toString().equals("")) {
                    Log.d(TAG, "onClick: attempting to submit new commit.")
                    addNewComment(mComment?.text.toString())
                    mComment?.setText("");
                    closeKeyboard()
                } else {
                    Toast.makeText(activity, "You can't post a blank comment.", Toast.LENGTH_SHORT).show()
                }
            }
        })

        return view
    }

    private fun closeKeyboard() {
        var view : View? = activity?.currentFocus
        if (view != null) {
            var imm : InputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun addNewComment(newComment : String) {
        Log.d(TAG, "addnewComment: adding new comment: " + newComment)

        var commentID : String? = myRef?.push()?.key
        val comment: Comment = Comment()
        comment.comment = newComment
        comment.date_created
        comment.user_id = FirebaseAuth.getInstance().currentUser?.uid

        // insert into photos node
        myRef?.child(getString(R.string.dbname_photos))
                ?.child(mPhoto?.photo_id!!)
                ?.child(getString(R.string.field_comments))
                ?.child(commentID!!)
                ?.setValue(comment)

        // insert into user_photos node
        myRef?.child(getString(R.string.dbname_user_photos))
                ?.child(FirebaseAuth.getInstance().currentUser?.uid!!)
                ?.child(mPhoto?.photo_id!!)
                ?.child(getString(R.string.field_comments))
                ?.child(commentID!!)
                ?.setValue(comment)
    }


    private fun getTimeStamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm;ss'Z'", Locale.JAPAN)
        sdf.timeZone = TimeZone.getTimeZone("Asia/Tokyo")
        return sdf.format(Date())
    }

    /**
     * retrieve the photo from the incoming bundle from profileActivity interface
     * @return
     */
    private fun getCallingActivityFromBundle(): String? {
        Log.d(TAG, "getPhotoFromBundle: arguments: " + arguments!!)

        val bundle = this.arguments
        return bundle?.getString(getString(R.string.string_home))
    }

    /**
     * retrieve the photo from the incoming bundle from profileActivity interface
     * @return
     */
    private fun getPhotoFromBundle(): Photo? {
        Log.d(TAG, "getPhotoFromBundle: arguments: " + arguments!!)

        val bundle = this.arguments
        return bundle?.getParcelable(getString(R.string.photo))
    }

    /*
     * ----------------------------Firebase---------------------------------------------------------
     */

    /*
     * Setup the firebase auth object.
     */
    private fun setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.")
        mAuth = FirebaseAuth.getInstance()
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        myRef = mFirebaseDatabase?.getReference()
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser

            if (user != null) {
                //user is signed in
                Log.d(TAG, "onAuthStateChanged: signed_in" + user.uid)
            } else {
                //user is signed out
                Log.d(TAG, "onAuthStateChanged: signed_out")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mAuth?.addAuthStateListener(mAuthListener!!)
    }

    override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth?.removeAuthStateListener(mAuthListener!!)
        }
    }

    /*
     * ----------------------------Firebase---------------------------------------------------------
     */
}