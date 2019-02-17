package com.example.katsumikusumi.instagramcloneapp.Utils

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.example.katsumikusumi.instagramcloneapp.Models.Comment
import com.example.katsumikusumi.instagramcloneapp.Models.CommentsListAdapter
import com.example.katsumikusumi.instagramcloneapp.Models.Like
import com.example.katsumikusumi.instagramcloneapp.Models.Photo
import com.example.katsumikusumi.instagramcloneapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
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


        return view
    }

    private fun setupWidgets() {
        val adapter : CommentsListAdapter = CommentsListAdapter(activity, R.layout.layout_comment, mComments)
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

        mBackArrow?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                Log.d(TAG,"onClick: navigating back")
                activity?.supportFragmentManager?.popBackStack()
            }
        })
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

        if (mPhoto?.comments?.size == 0) {
            mComments?.clear()
            var firstComment: Comment  = Comment()
            firstComment.comment = mPhoto?.caption
            firstComment.user_id = mPhoto?.user_id
            firstComment.date_created = mPhoto?.date_created
            mComments?.add(firstComment)
            mPhoto?.comments = mComments
            setupWidgets()
        }

        myRef?.child(activity?.getString(R.string.dbname_photos)!!)
                ?.child(mPhoto?.photo_id!!)
                ?.child(activity?.getString(R.string.field_comments)!!)
                ?.addChildEventListener(object : ChildEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                        val query: Query? = myRef?.child(activity?.getString(R.string.dbname_photos)!!)
                                ?.orderByChild(activity?.getString(R.string.field_photo_id)!!)
                                ?.equalTo(mPhoto?.photo_id)

                        query?.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                for (singleSnapshot in dataSnapshot.children) {
                                    val photo = Photo()
                                    val objectMap = singleSnapshot.value as HashMap<String, Any>?

                                    photo.caption = objectMap!![activity?.getString(R.string.field_caption)]!!.toString()
                                    photo.tags = objectMap[activity?.getString(R.string.field_tags)]!!.toString()
                                    photo.photo_id = objectMap[activity?.getString(R.string.field_photo_id)]!!.toString()
                                    photo.user_id = objectMap[activity?.getString(R.string.field_user_id)]!!.toString()
                                    photo.date_created = objectMap[activity?.getString(R.string.field_date_created)]!!.toString()
                                    photo.image_path = objectMap[activity?.getString(R.string.field_image_path)]!!.toString()

                                    mComments?.clear()
                                    val firstComment : Comment = Comment()
                                    firstComment.comment = mPhoto?.caption
                                    firstComment.user_id = mPhoto?.user_id
                                    firstComment.date_created = mPhoto?.date_created
                                    mComments?.add(firstComment)

                                    for (dSnapshot in singleSnapshot
                                            .child(activity?.getString(R.string.field_comments)!!).children) {
                                        val comment = Comment()
                                        comment.user_id = dSnapshot.getValue(Comment::class.java)?.user_id
                                        comment.comment = dSnapshot.getValue(Comment::class.java)?.comment
                                        comment.date_created = dSnapshot.getValue(Comment::class.java)?.date_created
                                        mComments?.add(comment)
                                    }

                                    photo.comments = mComments
                                    mPhoto = photo;

                                    setupWidgets()
                                }

                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                Log.d(TAG, "onCancelled: query canceled.")
                            }
                        })
                    }

                    override fun onChildRemoved(p0: DataSnapshot) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }
                }
        )
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