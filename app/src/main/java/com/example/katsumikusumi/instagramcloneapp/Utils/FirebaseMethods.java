package com.example.katsumikusumi.instagramcloneapp.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.katsumikusumi.instagramcloneapp.R;
import com.example.katsumikusumi.instagramcloneapp.models.User;
import com.example.katsumikusumi.instagramcloneapp.models.UserAccountSettings;
import com.example.katsumikusumi.instagramcloneapp.models.UserSettings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseMethods {
    private static final String TAG = "FirebaseMethods";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private String userID;


    private Context mContext;

    public FirebaseMethods(Context context){
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mContext = context;

        if (mAuth.getCurrentUser() != null) {
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    public boolean checkIfUserExists(String username, DataSnapshot dataSnapshot) {
        Log.d(TAG, "checkIfUserExists: checking if " + username + "already exists.");

        User user = new User();
        for (DataSnapshot ds: dataSnapshot.child(userID).getChildren()) {
            Log.d(TAG, "checkIfUserExists: datasnapshot: " + ds);

            user.setEmail(ds.getValue(User.class).getUsername());
            Log.d(TAG, "checkIfUserExists: username: " + user.getUsername());

            if (StringManipulation.expandUsername(user.getUsername()).equals(username)) {
                Log.d(TAG, "checkIfUserExists: FOUND A MATCH: "+ user.getUsername());
                return true;
            }
        }
        return false;
    }

    /**
     * Register a new email and password to Firebase Authentication.
     * @param email
     * @param username
     * @param password
     */
    public void registerNewEmail(final String email, final String username, final String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            sendVerificationEmail();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            
                            userID = mAuth.getCurrentUser().getUid();
                            Log.d(TAG, "onComplete: Authstate chenged: " + userID);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(mContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void sendVerificationEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                            } else {
                                Toast.makeText(mContext, "couldn't send verification email.", Toast.LENGTH_SHORT);
                            }
                        }
                    });
        }
    }

    /**
     * Add information to the users nodes
     * Add information to the user_accountsettings node
     *
     * @param email
     * @param username
     * @param description
     * @param website
     * @param profile_photo
     */
    public void addNewUser(String email, String username, String description, String website, String profile_photo) {
        User user = new User(userID, 1, email, StringManipulation.condenseUsername(username));

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .setValue(user);

        UserAccountSettings settings = new UserAccountSettings(
                description,
                username,
                0,
                0,
                0,
                profile_photo,
                StringManipulation.condenseUsername(username),
                website
        );

        myRef.child(mContext.getString(R.string.user_account_settigs))
                .child(userID)
                .setValue(settings);
    }

    /**
     * Retrievets the account settings for the user currently Logged in
     * Database: user_account_Settings node
     * @param dataSnapshot
     * @return
     */
    private UserSettings getUserAccountSettings(DataSnapshot dataSnapshot){
        Log.d(TAG, "getUserAccountSettings: retrieving user account settings from firebase.");

        UserAccountSettings settings = new UserAccountSettings();
        User user = new User();

        for (DataSnapshot ds: dataSnapshot.getChildren()) {

            //user account settings node
            if (ds.getKey().equals(mContext.getString(R.string.user_account_settigs))) {
                Log.d(TAG, "getUserAccountSettings: datasnapshot: " +ds);

                try {
                    settings.setDisplay_name(
                            ds.child(userID)
                            .getValue(UserAccountSettings.class)
                            .getDisplay_name()
                    );
                    settings.setUsername(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getUsername()
                    );
                    settings.setWebsite(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getWebsite()
                    );
                    settings.setDescription(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getDescription()
                    );
                    settings.setProfile_photo(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getProfile_photo()
                    );
                    settings.setPosts(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getPosts()
                    );
                    settings.setFollowings(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getFollowings()
                    );
                    settings.setFollowers(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getFollowers()
                    );

                    Log.d(TAG, "getUserAccountSettings: retrieved user_account_settings information." + settings.toString());
                } catch (NullPointerException e) {
                    Log.e(TAG, "getUserAccountSettings: NullPointerException: " + e.getMessage());
                }
            }

            //users node
            if (ds.getKey().equals(mContext.getString(R.string.dbname_users))) {
                Log.d(TAG, "getUserAccountSettings: datasnapshot: " + ds);

                try {
                    user.setUsername(
                            ds.child(userID)
                                    .getValue(User.class)
                                    .getUsername()
                    );
                    user.setEmail(
                            ds.child(userID)
                                    .getValue(User.class)
                                    .getEmail()
                    );
                    user.setPhone_number(
                            ds.child(userID)
                                    .getValue(User.class)
                                    .getPhone_number()
                    );
                    user.setUser_id(
                            ds.child(userID)
                                    .getValue(User.class)
                                    .getUser_id()
                    );
                    Log.d(TAG, "getUserAccountSettings: retrieved user information." + user.toString());

                } catch (NullPointerException e) {
                    Log.e(TAG, "getUserAccountSettings: NullPointerException: " + e.getMessage());
                }
            }
        }
        return new UserSettings(user, settings);
    }
}