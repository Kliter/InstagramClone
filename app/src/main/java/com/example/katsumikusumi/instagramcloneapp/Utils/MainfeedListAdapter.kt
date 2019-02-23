package com.example.katsumikusumi.instagramcloneapp.Utils

import android.content.Context
import android.content.Intent
import android.media.Image
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.katsumikusumi.instagramcloneapp.Home.HomeActivity
import com.example.katsumikusumi.instagramcloneapp.Models.*
import com.example.katsumikusumi.instagramcloneapp.Profile.ProfileActivity
import com.example.katsumikusumi.instagramcloneapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.nostra13.universalimageloader.core.ImageLoader
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.ClassCastException
import java.lang.NullPointerException
import java.lang.StringBuilder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MainfeedListAdapter(context: Context, resource: Int, objects: MutableList<Photo>) : ArrayAdapter<Photo>(context, resource, objects) {
    
    public interface OnLoadMoreItemListener{
        fun onLoadMoreItems()
    }
    private lateinit var mOnLoadMoreItemsListener: OnLoadMoreItemListener

    private val TAG : String = "MainfeedListAdapter"

    private var mInflater : LayoutInflater
    private var mLayoutResource : Int = 0
    private var mReference : DatabaseReference
    private lateinit var currentUsername : String
    private var mContext : Context

    init {
        mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mLayoutResource = resource
        mContext = context
        mReference = FirebaseDatabase.getInstance().reference
    }

    class ViewHolder {
        lateinit var  mProfileImage : CircleImageView
        lateinit var likesString : String
        lateinit var username : TextView
        lateinit var timeDelta : TextView
        lateinit var caption : TextView
        lateinit var likes : TextView
        lateinit var comments : TextView
        lateinit var image : SquareImageView
        lateinit var heartRed : ImageView
        lateinit var heartWhite : ImageView
        lateinit var comment : ImageView

        var settings : UserAccountSettings = UserAccountSettings()
        var user : User = User()
        var likeByCurrentUser: Boolean = false
        lateinit var users : StringBuilder
        lateinit var mLikesString : String
        lateinit var heart: Heart
        lateinit var detector: GestureDetector
        lateinit var photo: Photo
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder : ViewHolder
        var returnView : View? = convertView

        if (returnView == null) {
            returnView = mInflater.inflate(mLayoutResource, parent, false)
            holder = ViewHolder()
            holder.username = returnView.findViewById(R.id.username) as TextView
            holder.image = returnView.findViewById(R.id.post_image) as SquareImageView
            holder.heartRed = returnView.findViewById(R.id.image_heart_red) as ImageView
            holder.heartWhite = returnView.findViewById(R.id.image_heart) as ImageView
            holder.comment = returnView.findViewById(R.id.speech_bubble) as ImageView
            holder.likes = returnView.findViewById(R.id.image_likes) as TextView
            holder.comments = returnView.findViewById(R.id.image_comments_link) as TextView
            holder.caption = returnView.findViewById(R.id.image_caption) as TextView
            holder.timeDelta = returnView.findViewById(R.id.image_time_posted) as TextView
            holder.mProfileImage = returnView.findViewById(R.id.profile_photo) as CircleImageView
            holder.heart = Heart(holder.heartWhite, holder.heartRed)
            holder.photo = getItem(position)
            holder.detector = GestureDetector(mContext, GestureListener(holder))
            holder.users = StringBuilder()

            returnView.tag = holder

        } else {
            holder = returnView.tag as ViewHolder
        }

        // Get the current users username (need for checking likes string)
        getCurrentUsername()

        // Get likes string
        getLikesString(holder)

        // Set the caption
        holder.caption.setText(getItem(position).caption)

        // Set the comment
        val comments : List<Comment> = getItem(position).comments
        holder.comments.setText("View all " + comments.size + " comments" )
        holder.comments.setOnClickListener{
            Log.d(TAG, "onClick : loading comment thread for " + getItem(position).photo_id)
            (mContext as HomeActivity).onCommentThreadSelected(getItem(position),
                    mContext.getString(R.string.home_activity))

            (mContext as HomeActivity).hideLayout()
        }

        // Set the time it was posted
        val timestampDifference : String = getTimestampDifference(getItem(position))
        if (!timestampDifference.equals("0")) {
            holder.timeDelta.setText(timestampDifference + "DAYS AGO")
        } else {
            holder.timeDelta.setText("TODAY")
        }


        // Set the profile image
        val imageLoader : ImageLoader = ImageLoader.getInstance()
        imageLoader.displayImage(getItem(position).image_path, holder.image)

        // Get the profile image and username
        val reference: DatabaseReference = FirebaseDatabase.getInstance().reference
        val query: Query = reference
                .child(mContext.getString(R.string.dbname_user_account_settings))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(getItem(position).user_id)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (singleSnapshot in dataSnapshot.children) {
                    // currentUsername = singleSnapshot.getValue(UserAccountSettings::class.java)?.username!!

                    Log.d(TAG, "onDataChange: found user: "
                            + singleSnapshot.getValue(UserAccountSettings::class.java)?.username!!)

                    holder.username.text = singleSnapshot.getValue(UserAccountSettings::class.java) as String
                    holder.username.setOnClickListener{
                        Log.d(TAG, "onClick: navigating to profile of: " + holder.user.username)

                        val intent : Intent = Intent(mContext, ProfileActivity::class.java)
                        intent.putExtra(mContext.getString(R.string.calling_activity),
                                mContext.getString(R.string.home_activity))
                        intent.putExtra(mContext.getString(R.string.intent_user), holder.user)
                        mContext.startActivity(intent)
                    }

                    imageLoader.displayImage(singleSnapshot.getValue(UserAccountSettings::class.java)!!.profile_photo,
                            holder.mProfileImage)
                    holder.mProfileImage.setOnClickListener{
                        Log.d(TAG, "onClick: navigating to profile of: " + holder.user.username)

                        val intent : Intent = Intent(mContext, ProfileActivity::class.java)
                        intent.putExtra(mContext.getString(R.string.calling_activity),
                                mContext.getString(R.string.home_activity))
                        intent.putExtra(mContext.getString(R.string.intent_user), holder.user)
                        mContext.startActivity(intent)
                    }

                    holder.settings = singleSnapshot.getValue(UserAccountSettings::class.java)!!
                    holder.comments.setOnClickListener{
                        (mContext as HomeActivity).onCommentThreadSelected(getItem(position),
                                mContext.getString(R.string.home_activity))
                        (mContext as HomeActivity).hideLayout()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        // Get the user Object
        val userQuery : Query = mReference
                .child(mContext.getString(R.string.dbname_users))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(getItem(position).user_id)

        userQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (singleSnapshot in dataSnapshot.children) {
                    Log.d(TAG, "onDataChange: found user"
                            + singleSnapshot.getValue(User::class.java)?.username)
                    singleSnapshot.getValue(User::class.java)?.username
                    holder.user = singleSnapshot.getValue(User::class.java)!!

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        if (reachedEndOfList(position)) {
            loadMoreData()
        }
        return returnView!!
    }

    private fun reachedEndOfList(position: Int): Boolean {
        return position == count - 1
    }

    private fun loadMoreData() {
        try{
            mOnLoadMoreItemsListener = context as OnLoadMoreItemListener
        } catch (e: ClassCastException) {
            Log.e(TAG, "loadMoreData: ClassCastException: " + e.message)
        }

        try{
            mOnLoadMoreItemsListener.onLoadMoreItems()
        } catch (e: NullPointerException) {
            Log.e(TAG, "loadMoreData: NullPointerException: " + e.message)
        }
    }

    inner class GestureListener(holder : ViewHolder) : GestureDetector.SimpleOnGestureListener() {

        val mHolder : ViewHolder
        init {
           mHolder = holder
        }

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            Log.d(TAG, "onDoubleTap: double tap detected.")

            val reference = FirebaseDatabase.getInstance().reference
            val query = reference
                    .child(mContext.getString(R.string.dbname_photos))
                    .child(mHolder.photo.getPhoto_id())
                    .child(mContext.getString(R.string.field_likes))

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (singleSnapshot in dataSnapshot.children) {

                        val keyID = singleSnapshot.key

                        if (mHolder.likeByCurrentUser && singleSnapshot.getValue(Like::class.java)!!.user_id == FirebaseAuth.getInstance().currentUser!!.uid) {
                            //case1: Then user already liked the photo
                            mReference.child(mContext.getString(R.string.dbname_photos)).child(mHolder.photo.getPhoto_id()).child(mContext.getString(R.string.field_likes)).child(keyID!!).removeValue()
                            mReference.child(mContext.getString(R.string.dbname_user_photos)).child(FirebaseAuth.getInstance().currentUser!!.uid).child(mHolder.photo.getPhoto_id()).child(mContext.getString(R.string.field_likes)).child(keyID).removeValue()

                            mHolder.heart.toggleLike()
                            getLikesString(mHolder)
                        } else if (!mHolder.likeByCurrentUser) {
                            //case2: The user has not liked the photo
                            addNewLike(mHolder)
                            break
                        }
                    }
                    if (!dataSnapshot.exists()) {
                        //Add new like
                        addNewLike(mHolder)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })

            return true
        }
    }

    private fun addNewLike(holder: ViewHolder) {
        Log.d(TAG, "addNewLike: adding new like")
        val newLikeID = mReference.push().getKey()
        val like = Like()
        like.user_id = FirebaseAuth.getInstance().currentUser!!.uid

        mReference.child(mContext.getString(R.string.dbname_photos))
                .child(holder.photo.getPhoto_id())
                .child(mContext.getString(R.string.field_likes))
                .child(newLikeID!!)
                .setValue(like)

        mReference.child(mContext.getString(R.string.dbname_user_photos))
                .child(holder.photo.getUser_id())
                .child(holder.photo.getPhoto_id())
                .child(mContext.getString(R.string.field_likes))
                .child(newLikeID!!)
                .setValue(like)

        holder.heart.toggleLike()
        getLikesString(holder)
    }

    private fun getCurrentUsername() {
        Log.d(TAG, "getCurrenUsername: retrieving user account setting")
        val reference: DatabaseReference = FirebaseDatabase.getInstance().reference
        val query: Query = reference
                .child(mContext.getString(R.string.dbname_users))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(FirebaseAuth.getInstance().currentUser?.uid)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (singleSnapshot in dataSnapshot.children) {
                    currentUsername = singleSnapshot.getValue(UserAccountSettings::class.java)?.username!!
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun getLikesString(holder: ViewHolder) {
        Log.d(TAG, "getLikesString: getting likes string")

        try {
            val reference: DatabaseReference = FirebaseDatabase.getInstance().reference
            val query: Query = reference
                    .child(mContext.getString(R.string.dbname_photos))
                    .child(holder.photo.photo_id)
                    .child(mContext.getString(R.string.field_likes))

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    holder.users = StringBuilder()

                    for (singleSnapshot in dataSnapshot.children) {
                        val reference: DatabaseReference = FirebaseDatabase.getInstance().reference
                        val query = reference.child(mContext.getString(R.string.dbname_users))
                                .orderByChild(mContext.getString(R.string.field_user_id))
                                .equalTo(singleSnapshot.getValue(Like::class.java)!!.user_id)

                        query.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                for (singleSnapshot in dataSnapshot.children) {
                                    Log.d(TAG, "onDataChange: found like: " + singleSnapshot.getValue(User::class.java)!!.username)
                                    holder.users.append(singleSnapshot.getValue(User::class.java)!!.username)
                                    holder.users.append(",")
                                }

                                val splitUsers = holder.users.toString().split(",".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                                holder.likeByCurrentUser = holder.users.toString().contains(currentUsername + ",")

                                val length = splitUsers.size
                                when (length) {
                                    1 -> holder.likesString = "Liked by " + splitUsers[0] + " and " + splitUsers[1]

                                    2 -> holder.likesString = "Liked by " + splitUsers[0] + " and " + splitUsers[1] + " and " + splitUsers[1]

                                    3 -> holder.likesString = "Liked by " + splitUsers[0] + " and " + splitUsers[1] + " and " + splitUsers[1] + " and " + splitUsers[2]

                                    4 -> holder.likesString = "Liked by " + splitUsers[0] + " and " + splitUsers[1] + " and " + splitUsers[1] + " and " + splitUsers[2] + " and " + splitUsers[3]

                                    else -> {
                                        if (length > 4) {
                                            holder.likesString = "Liked by " + splitUsers[0] + " and " + splitUsers[1] + " and " + splitUsers[1] + " and " + splitUsers[2] + " and " + (splitUsers.size - 3) + " others"
                                        }
                                        setupLikesString(holder, holder.likesString)
                                    }
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {

                            }
                        })
                    }

                    if (!dataSnapshot.exists()) {
                        holder.likesString = ""
                        holder.likeByCurrentUser = false
                        setupLikesString(holder, holder.likesString)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })

        } catch (e: NullPointerException) {
            Log.e(TAG, "getLikesString: NullPointerException: " + e.message)
            holder.likesString = ""
            holder.likeByCurrentUser = false

            setupLikesString(holder, holder.likesString)
        }
    }

    private fun setupLikesString(holder : ViewHolder, likesString : String) {
        Log.d(TAG, "setupLikesString: likes string: " + holder.likesString)
        if (holder.likeByCurrentUser) {
            Log.d(TAG, "setupLikesString: photo is liked by current user")
            holder.heartWhite.visibility = View.GONE
            holder.heartRed.visibility = View.VISIBLE
            holder.heartRed.setOnTouchListener { view, event ->
                return@setOnTouchListener holder.detector.onTouchEvent(event)
            }
        } else {
            Log.d(TAG, "setupLikesString: photo is not liked by current user")
            holder.heartWhite.visibility = View.VISIBLE
            holder.heartRed.visibility = View.GONE
            holder.heartWhite.setOnTouchListener { view, event ->
                return@setOnTouchListener holder.detector.onTouchEvent(event)
            }
        }
        holder.likes.setText(likesString)
    }

    /**
     * Returns a string representing the number of days ago the post was ade
     * @return
     */
    private fun getTimestampDifference(photo : Photo): String {
        Log.d(TAG, "getTimestampDifference: getting timestamp difference.")

        var difference = ""
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.JAPANESE)
        sdf.timeZone = TimeZone.getTimeZone("Asia/Tokyo")//google 'android list of timezones'
        val today = c.time
        sdf.format(today)
        val timestamp: Date
        val photoTimestamp = photo.getDate_created()

        try {
            timestamp = sdf.parse(photoTimestamp)
            difference = Math.round((today.time - timestamp.time).toFloat() / 1000f / 60f / 60f / 24f).toString()
        } catch (e: ParseException) {
            Log.e(TAG, "getTimestampDifference: ParceException: " + e.message)
            difference = "0"
        }

        return difference
    }
}