package com.example.katsumikusumi.instagramcloneapp.Utils

import android.content.Context
import android.media.Image
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.katsumikusumi.instagramcloneapp.Models.Like
import com.example.katsumikusumi.instagramcloneapp.Models.Photo
import com.example.katsumikusumi.instagramcloneapp.Models.User
import com.example.katsumikusumi.instagramcloneapp.Models.UserAccountSettings
import com.example.katsumikusumi.instagramcloneapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.layout_mainfeed_listitem.view.*
import org.w3c.dom.Text
import java.lang.StringBuilder

class MainfeedListAdapter(context: Context, resource: Int, objects: MutableList<Photo>) : ArrayAdapter<Photo>(context, resource, objects) {

    private val TAG : String = "MainfeedListAdapter"

    private var mInflater : LayoutInflater
    private var mLayoutResource : Int = 0
    private lateinit var mReference : DatabaseReference
    private lateinit var currentUsername : String
    private var mContext : Context

    init {
        mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mLayoutResource = resource
        mContext = context
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

        val settings : UserAccountSettings = UserAccountSettings()
        val user : User = User()
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
            holder.mProfileImage = returnView.findViewById(R.id.profile_image) as CircleImageView
            holder.heart = Heart(holder.heartWhite, holder.heartRed)
            holder.photo = getItem(position)
            holder.detector = GestureDetector(mContext, GestureListener(holder))
            holder.users = StringBuilder()

        } else {

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
                            getLikesString()
                        } else if (!mHolder.likeByCurrentUser) {
                            //case2: The user has not liked the photo
                            addNewLike()
                            break
                        }
                    }
                    if (!dataSnapshot.exists()) {
                        //Add new like
                        addNewLike()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })

            return true
        }
    }
}