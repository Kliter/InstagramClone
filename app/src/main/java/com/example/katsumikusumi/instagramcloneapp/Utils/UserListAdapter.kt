package com.example.katsumikusumi.instagramcloneapp.Utils

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.katsumikusumi.instagramcloneapp.Models.User
import com.example.katsumikusumi.instagramcloneapp.Models.UserAccountSettings
import com.example.katsumikusumi.instagramcloneapp.R
import com.google.firebase.database.*
import com.nostra13.universalimageloader.core.ImageLoader
import de.hdodenhof.circleimageview.CircleImageView

class UserListAdapter(context: Context, resource: Int, objects: MutableList<User>): ArrayAdapter<User>(context, resource, objects) {
    private val TAG: String = "UserListAdapter"

    private var mInflater: LayoutInflater? = null
    private var mUsers: List<User>? = null
    private var layoutResource: Int = 0
    private var mContext: Context? = null

    init {
        mContext = context
        mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        layoutResource = resource
        this.mUsers = objects
    }

    private class ViewHolder {
        var username: TextView? = null
        var email: TextView? = null
        var profileImage: CircleImageView? = null
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var returnView: View? = convertView
        val holder: ViewHolder
        if (returnView == null) {
            returnView = mInflater?.inflate(layoutResource, parent, false)
            holder = ViewHolder()

            holder.username = returnView?.findViewById(R.id.username) as TextView
            holder.email = returnView.findViewById(R.id.email) as TextView
            holder.profileImage = returnView.findViewById(R.id.profile_image)
            returnView.tag = holder
        } else {
            holder = returnView.tag as ViewHolder
        }

        holder.username?.setText(getItem(position).username)
        holder.email?.setText(getItem(position).email)

        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference()
        var query: Query = reference.child(mContext?.getString(R.string.dbname_user_account_settings)!!)
                .orderByChild(mContext?.getString(R.string.field_user_id)!!)
                .equalTo(getItem(position).user_id)
        query.addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (singleSnapshot in dataSnapshot?.children) {
                    Log.d(TAG, "onDataChange: found user: " + singleSnapshot.getValue(UserAccountSettings::class.java).toString())
                    val imageLoader : ImageLoader = ImageLoader.getInstance()
                    imageLoader.displayImage(singleSnapshot.getValue(UserAccountSettings::class.java)?.profile_photo, holder.profileImage)
                }
            }
        })

        return returnView
    }
}