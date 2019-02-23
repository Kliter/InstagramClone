package com.example.katsumikusumi.instagramcloneapp.Home

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.example.katsumikusumi.instagramcloneapp.Models.Comment
import com.example.katsumikusumi.instagramcloneapp.Models.Photo
import com.example.katsumikusumi.instagramcloneapp.R
import com.example.katsumikusumi.instagramcloneapp.Utils.MainfeedListAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.lang.IndexOutOfBoundsException
import java.lang.NullPointerException
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment() : Fragment() {
    private val TAG : String = "HomeFragment"

    //varibles
    private lateinit var mPhotos: ArrayList<Photo>;
    private lateinit var mPaginatedPhotos: ArrayList<Photo>
    private lateinit var mFollowing: ArrayList<String>
    private lateinit var mListView: ListView
    private lateinit var mAdapter: MainfeedListAdapter
    private var mResults: Int = 10

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view : View = inflater.inflate(R.layout.fragment_home, container, false)
        mListView = view.findViewById(R.id.listView) as ListView
        mFollowing = ArrayList<String>()
        mPhotos = ArrayList<Photo>()

        getFollowing()

        return view
    }

    private fun getFollowing() {
        val reference: DatabaseReference = FirebaseDatabase.getInstance().reference
        val query: Query = reference
                .child(getString(R.string.dbname_following))
                .child(FirebaseAuth.getInstance().currentUser?.uid!!)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (singleSnapshot in dataSnapshot.children) {
                    Log.d(TAG, "onDataChange: found user: "
                            + singleSnapshot.child(getString(R.string.field_user_id)).getValue())
                    mFollowing.add(singleSnapshot.child(getString(R.string.field_user_id)).getValue().toString())
                }
                mFollowing.add(FirebaseAuth.getInstance().currentUser?.uid!!)

                getPhotos()
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun getPhotos() {
        Log.d(TAG, "getPhotos: getting photos")

        val reference: DatabaseReference = FirebaseDatabase.getInstance().reference

        for (index : Int in mFollowing.indices) {
            val query: Query = reference
                    .child(getString(R.string.dbname_user_photos))
                    .child(mFollowing.get(index))
                    .orderByChild(getString(R.string.field_user_id))
                    .equalTo(mFollowing.get(index))

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var count: Int = index;
                    for (singleSnapshot in dataSnapshot.children) {
                        val photo = Photo()
                        val objectMap = singleSnapshot.value as HashMap<String, Any>?

                        photo.caption = objectMap!![getString(R.string.field_caption)]!!.toString()
                        photo.tags = objectMap[getString(R.string.field_tags)]!!.toString()
                        photo.photo_id = objectMap[getString(R.string.field_photo_id)]!!.toString()
                        photo.user_id = objectMap[getString(R.string.field_user_id)]!!.toString()
                        photo.date_created = objectMap[getString(R.string.field_date_created)]!!.toString()
                        photo.image_path = objectMap[getString(R.string.field_image_path)]!!.toString()

                        val comments = java.util.ArrayList<Comment>()
                        for (dSnapshot in singleSnapshot.child(getString(R.string.field_comments)).children) {
                            val comment = Comment()
                            comment.user_id = dSnapshot.getValue(Comment::class.java)!!.user_id
                            comment.comment = dSnapshot.getValue(Comment::class.java)!!.comment
                            comment.date_created = dSnapshot.getValue(Comment::class.java)!!.date_created
                            comments.add(comment)
                        }
                        photo.comments = comments
                        mPhotos.add(photo)
                    }

                    if (count >= mFollowing.size -1) {
                        displayPhotos()
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
        }
    }

    private fun displayPhotos() {
        mPaginatedPhotos = ArrayList<Photo>()
        try {
            Collections.sort(mPhotos) { o1, o2 ->
                return@sort o2.date_created.compareTo(o1.date_created)
            }

            var iterations : Int = mPhotos.size
            if (iterations > 10) {
                iterations = 10
            }

            for (index: Int in 0..iterations) {
                mPaginatedPhotos.add(mPhotos.get(index))
            }

            mAdapter = MainfeedListAdapter(context!!, R.layout.layout_mainfeed_listitem, mPaginatedPhotos)
            mListView.setAdapter(mAdapter)

        } catch (e: NullPointerException) {
            Log.e(TAG, "displayPhotos: NullPointerException: " + e.message)

        } catch (e: IndexOutOfBoundsException) {
            Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: " + e.message)
        }
    }

    public fun displayMorePhoto() {
        Log.d(TAG, "displayMorePhoto: displaying more photos")

        try {
            var iterations : Int
            if (mPhotos.size > (mResults + 10)) {
                Log.d(TAG, "displayMorePhoto: there are greater than 10 more photos")
                iterations = 10
            } else {
                Log.d(TAG, "displayMorePhoto: there is less than 10 more photos")
                iterations = mPhotos.size - mResults
            }

            // Add the new photos to the paginated results
            for (index : Int in 0..(mResults + iterations)) {
                mPaginatedPhotos.add(mPhotos.get(index))
            }
            mResults += iterations
            mAdapter.notifyDataSetChanged()

        } catch (e: NullPointerException) {
            Log.e(TAG, "displayPhotos: NullPointerException: " + e.message)

        } catch (e: IndexOutOfBoundsException) {
            Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: " + e.message)
        }
    }
}