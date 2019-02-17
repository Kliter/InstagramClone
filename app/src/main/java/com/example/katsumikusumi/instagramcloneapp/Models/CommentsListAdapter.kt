package com.example.katsumikusumi.instagramcloneapp.Models

import android.content.Context
import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.katsumikusumi.instagramcloneapp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener
import de.hdodenhof.circleimageview.CircleImageView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CommentsListAdapter(context : Context?, val resource : Int, objects : List<Comment>?) : ArrayAdapter<Comment>(context, resource, objects) {

    val TAG : String = CommentsListAdapter::class.java.simpleName
    var mInflater : LayoutInflater? = null
    var layoutResouce : Int = 0
    var mContext : Context?

    init {
        mInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        mContext = context
        layoutResouce = resource
    }

    class ViewHolder{
        var comment : TextView? = null
        var username : TextView? = null
        var timestamp : TextView? = null
        var reply : TextView? = null
        var likes : TextView? = null
        var profileImage : CircleImageView? = null
        var like : ImageView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {

        var holder : ViewHolder
        var returnView : View? = convertView

        if (returnView == null) {
            returnView = mInflater?.inflate(layoutResouce, parent, false)

            holder = ViewHolder()
            holder.comment = returnView?.findViewById(R.id.comment)
            holder.username = returnView?.findViewById(R.id.comment_username)
            holder.timestamp = returnView?.findViewById(R.id.comment_time_posted)
            holder.reply = returnView?.findViewById(R.id.comment_reply)
            holder.like = returnView?.findViewById(R.id.comment_like)
            holder.likes = returnView?.findViewById(R.id.comment_likes)
            holder.profileImage = returnView?.findViewById(R.id.comment_profile_image)

            returnView?.tag = holder

        } else {
            holder = returnView.tag as ViewHolder
        }

        // set the comment
        holder.comment?.text = getItem(position).comment

        //set the timestamp difference
        var timestampDifference : String = getTimestampDifference(getItem(position))
        if (!timestampDifference.equals("0")) {
            holder.timestamp?.text = timestampDifference + " d"
        } else {
            holder.timestamp?.text = "today"
        }

        //set the username


        val reference = FirebaseDatabase.getInstance().reference
        val query = reference.child(mContext?.getString(R.string.dbname_user_account_settings)!!)
                .orderByChild(mContext?.getString(R.string.field_user_id)!!)
                .equalTo(getItem(position).user_id)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (singleSnapshot in dataSnapshot.children) {
                    holder.username?.text = singleSnapshot.getValue(UserAccountSettings::class.java)?.username

                    var imageLoader : ImageLoader = ImageLoader.getInstance()
                    imageLoader.displayImage(
                            singleSnapshot.getValue(UserAccountSettings::class.java)?.profile_photo,
                            holder.profileImage)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(TAG, "onCancelled: query canceled.")
            }
        })

        try {
            if (position == 0) {
                holder.like?.visibility = View.GONE
                holder.likes?.visibility = View.GONE
                holder.reply?.visibility = View.GONE
            }
        } catch(e: NullPointerException) {
            Log.e(TAG, "getView: NullPointerException: " + e.message)
        }


        return returnView
    }

    /**
     * Returns a string representing the number of days ago the post was ade
     * @return
     */
    private fun getTimestampDifference(comment : Comment): String {
        Log.d(TAG, "getTimestampDifference: getting timestamp difference.")

        var difference = ""
        val c = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.JAPANESE)
        sdf.timeZone = TimeZone.getTimeZone("Asia/Tokyo")//google 'android list of timezones'
        val today = c.time
        sdf.format(today)
        val timestamp: Date
        val photoTimestamp = comment.date_created

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