package com.example.katsumikusumi.instagramcloneapp.Utils;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.katsumikusumi.instagramcloneapp.Models.Like;
import com.example.katsumikusumi.instagramcloneapp.Models.Photo;
import com.example.katsumikusumi.instagramcloneapp.Models.User;
import com.example.katsumikusumi.instagramcloneapp.Models.UserAccountSettings;
import com.example.katsumikusumi.instagramcloneapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ViewPostFragment extends Fragment {

    private static final String TAG = "ViewPostFragment";

    //widgets
    private SquareImageView mPostImage;
    private BottomNavigationViewEx bottomNavigationView;
    private TextView mBackLabel, mCaption, mUsername, mTimestamp, mLikes;
    private ImageView mBackArrow, mElipses, mHeartRed, mHeartWhite, mProfileImage;

    //variables
    private Photo mPhoto;
    private int mActivityNumber = 0;
    private String photoUsername;
    private String photoUrl;
    private UserAccountSettings mUserAccountSettings;
    private GestureDetector mGestureDetector;
    private Heart mHeart;
    private Boolean mLikedByCurrentUser;
    private StringBuilder mUsers;
    private String mLikesString = "";

    public ViewPostFragment() {
        super();
        setArguments(new Bundle());
    }

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_post, container, false);
        mPostImage = (SquareImageView) view.findViewById(R.id.post_image);
        bottomNavigationView = (BottomNavigationViewEx) view.findViewById(R.id.bottomNavViewBar);
        mBackArrow = (ImageView) view.findViewById(R.id.backArrow);
        mBackLabel = (TextView) view.findViewById(R.id.tvBackLabel);
        mCaption = (TextView) view.findViewById(R.id.image_caption);
        mUsername = (TextView) view.findViewById(R.id.username);
        mTimestamp = (TextView) view.findViewById(R.id.image_time_posted);
        mElipses = (ImageView) view.findViewById(R.id.ivElipses);
        mHeartRed = (ImageView) view.findViewById(R.id.image_heart_red);
        mHeartWhite = (ImageView) view.findViewById(R.id.image_heart);
        mProfileImage = (ImageView) view.findViewById(R.id.profile_photo);
        mLikes = (TextView) view.findViewById(R.id.image_likes);

        mHeart = new Heart(mHeartWhite, mHeartRed);
        mGestureDetector = new GestureDetector(getActivity(), new GestureListener());

        try {
            mPhoto = getPhotoFromBundle();
            UniversalImageLoader.setImage(mPhoto.getImage_path(), mPostImage, null, "");
            mActivityNumber = getActivityNumberFromBundle();
            getPhotoDetails();
            getLikesString();
        } catch(NullPointerException e) {
            Log.e(TAG, "onCreateView: NullPointerException: " + e.getMessage());
        }

        setupBottomNavigationView();
        setupFirebaseAuth();

        return view;
    }

    private void getLikesString() {
        Log.d(TAG, "getLikesString: getting likes string");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_photos))
                .child(mPhoto.getPhoto_id())
                .child(getString(R.string.field_likes));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers = new StringBuilder();

                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                    Query query = reference.child(getString(R.string.dbname_users))
                            .orderByChild(getString(R.string.field_user_id))
                            .equalTo(singleSnapshot.getValue(Like.class).getUser_id());

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
                                Log.d(TAG, "onDataChange: found like: " + singleSnapshot.getValue(User.class).getUsername());
                                mUsers.append(singleSnapshot.getValue(User.class).getUsername());
                                mUsers.append(",");
                            }

                            String[] splitUsers = mUsers.toString().split(",");
                            if (mUsers.toString().contains(mUserAccountSettings.getUsername() + "," )) {
                                mLikedByCurrentUser = true;
                            } else {
                                mLikedByCurrentUser = false;
                            }

                            int length = splitUsers.length;
                            switch (length){
                                case 1:
                                    mLikesString = "Liked by " + splitUsers[0] + " and " + splitUsers[1];
                                    break;

                                case 2:
                                    mLikesString = "Liked by " + splitUsers[0] + " and " + splitUsers[1] + " and " + splitUsers[1];
                                    break;

                                case 3:
                                    mLikesString = "Liked by " + splitUsers[0] + " and " + splitUsers[1] + " and " + splitUsers[1] + " and " + splitUsers[2];
                                    break;

                                case 4:
                                    mLikesString = "Liked by " + splitUsers[0] + " and " + splitUsers[1] + " and " + splitUsers[1] + " and " + splitUsers[2] + " and " + splitUsers[3];
                                    break;

                                default:
                                    if (length > 4) {
                                        mLikesString = "Liked by " + splitUsers[0] + " and " + splitUsers[1] + " and " + splitUsers[1] + " and " + splitUsers[2] + " and " + (splitUsers.length - 3) + " others";
                                    }
                                    setupWidgets();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                if (!dataSnapshot.exists()) {
                    mLikesString = "";
                    mLikedByCurrentUser = false;
                    setupWidgets();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(TAG, "onDoubleTap: double tap detected.");

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            Query query = reference
                    .child(getString(R.string.dbname_photos))
                    .child(mPhoto.getPhoto_id())
                    .child(getString(R.string.field_likes));

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {

                        String keyID = singleSnapshot.getKey();

                        if (mLikedByCurrentUser && singleSnapshot.getValue(Like.class).getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            //case1: Then user already liked the photo
                            myRef.child(getString(R.string.dbname_photos)).child(mPhoto.getPhoto_id()).child(getString(R.string.field_likes)).child(keyID).removeValue();
                            myRef.child(getString(R.string.dbname_user_photos)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(mPhoto.getPhoto_id()).child(getString(R.string.field_likes)).child(keyID).removeValue();

                            mHeart.toggleLike();
                            getLikesString();
                        } else if (!mLikedByCurrentUser) {
                            //case2: The user has not liked the photo
                            addNewLike();
                            break;
                        }
                    }
                    if (!dataSnapshot.exists()) {
                        //Add new like
                        addNewLike();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            return true;
        }
    }

    private void addNewLike() {
        Log.d(TAG, "addNewLike: adding new like");
        String newLikeID = myRef.push().getKey();
        Like like = new Like();
        like.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        myRef.child(getString(R.string.dbname_photos)).child(mPhoto.getPhoto_id()).child(getString(R.string.field_likes)).child(newLikeID).setValue(like);
        myRef.child(getString(R.string.dbname_user_photos)).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(getString(R.string.field_likes)).child(newLikeID).setValue(like);

        mHeart.toggleLike();
        getLikesString();
    }

    private void getPhotoDetails() {
        Log.d(TAG, "getPhotoDetails: retrieving photo details.");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbname_user_account_settings))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(mPhoto.getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()) {
                    mUserAccountSettings = singleSnapshot.getValue(UserAccountSettings.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query canceled.");
            }
        });

    }

    private void setupWidgets() {
        String timestampDiff = getTimestampDifference();
        if (!timestampDiff.equals("0")) {
            mTimestamp.setText(timestampDiff + " DAYS AGO");
        } else {
           mTimestamp.setText("TODAY");
        }
        UniversalImageLoader.setImage(mUserAccountSettings.getProfile_photo(), mProfileImage, null, "");
        mUsername.setText(mUserAccountSettings.getUsername());
        mLikes.setText(mLikesString);
        mCaption.setText(mPhoto.getCaption());

        if (mLikedByCurrentUser) {
            mHeartWhite.setVisibility(View.GONE);
            mHeartRed.setVisibility(View.VISIBLE);
            mHeartRed.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return mGestureDetector.onTouchEvent(event);
                }
            });
        } else {
            mHeartWhite.setVisibility(View.VISIBLE);
            mHeartRed.setVisibility(View.GONE);
            mHeartWhite.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return mGestureDetector.onTouchEvent(event);
                }
            });
        }
    }

    /**
     * Returns a string representing the number of days ago the post was ade
     * @return
     */
    private String getTimestampDifference() {
        Log.d(TAG, "getTimestampDifference: getting timestamp difference.");

        String difference = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.JAPANESE);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));//google 'android list of timezones'
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = mPhoto.getDate_created();

        try {
            timestamp = sdf.parse(photoTimestamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000f / 60f / 60f / 24f)));
        } catch(ParseException e) {
            Log.e(TAG, "getTimestampDifference: ParceException: " + e.getMessage());
            difference = "0";
        }

        return difference;
    }

    /**
     * retrieve the activity number from the incoming bundle from profileActivity interface
     * @return
     */
    private int getActivityNumberFromBundle() {
        Log.d(TAG, "getActivityNumberFromBundle: arguments: " + getArguments());
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getInt(getString(R.string.activity_number));
        } else {
            return 0;
        }
    }

    /**
     * retrieve the photo from the incoming bundle from profileActivity interface
     * @return
     */
    private Photo getPhotoFromBundle() {
        Log.d(TAG, "getPhotoFromBundle: arguments: " + getArguments());
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getParcelable(getString(R.string.photo));
        } else {
            return null;
        }
    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView);
        BottomNavigationViewHelper.enableNavigation(getActivity(), getActivity(), bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(mActivityNumber);
        menuItem.setChecked(true);
    }
    /*
     * ----------------------------Firebase---------------------------------------------------------
     */

    /*
     * Setup the firebase auth object.
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    //user is signed in
                    Log.d(TAG, "onAuthStateChanged: signed_in" + user.getUid());
                } else {
                    //user is signed out
                    Log.d(TAG, "onAuthStateChanged: signed_out");
                }
            }
        };
}

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /*
     * ----------------------------Firebase---------------------------------------------------------
     */
}