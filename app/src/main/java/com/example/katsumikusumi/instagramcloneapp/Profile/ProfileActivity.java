package com.example.katsumikusumi.instagramcloneapp.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.katsumikusumi.instagramcloneapp.R;
import com.example.katsumikusumi.instagramcloneapp.Utils.BottomNavigationViewHelper;
import com.example.katsumikusumi.instagramcloneapp.Utils.GridÎmageAdapter;
import com.example.katsumikusumi.instagramcloneapp.Utils.UniversalImageLoader;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity{
    private static final String TAG = "ProfileActivity";
    private static final int ACTIVITY_NUM = 4;

    private ProfileActivity mContext = ProfileActivity.this;

    private ProgressBar mProgressBar;
    private ImageView profilePhoto;
    private static final int NUM_GRID_COLUMNS = 3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "onCreate: started.");

        init();

//        setupBottomNavigationView();
//        setupToolbar();
//        setupActivityWidgets();
//        setProfileImage();
//
//        tempGridSetup();
    }
    
    private void init (){
        Log.d(TAG, "init: inflating" + getString(R.string.profile_fragment));

        ProfileFragment fragment = new ProfileFragment();
        FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.profile_fragment));
        transaction.commit();
    }
    

//    private void tempGridSetup(){
//        ArrayList<String> imgURLs = new ArrayList<>();
//        imgURLs.add("https://weekly.ascii.jp/elem/000/000/019/19847/android_limg_280x.jpg");
//        imgURLs.add("https://weekly.ascii.jp/elem/000/000/019/19847/android_limg_280x.jpg");
//        imgURLs.add("https://weekly.ascii.jp/elem/000/000/019/19847/android_limg_280x.jpg");
//        imgURLs.add("https://weekly.ascii.jp/elem/000/000/019/19847/android_limg_280x.jpg");
//        imgURLs.add("https://weekly.ascii.jp/elem/000/000/019/19847/android_limg_280x.jpg");
//        imgURLs.add("https://weekly.ascii.jp/elem/000/000/019/19847/android_limg_280x.jpg");
//        imgURLs.add("https://weekly.ascii.jp/elem/000/000/019/19847/android_limg_280x.jpg");
//        imgURLs.add("https://weekly.ascii.jp/elem/000/000/019/19847/android_limg_280x.jpg");
//        imgURLs.add("https://weekly.ascii.jp/elem/000/000/019/19847/android_limg_280x.jpg");
//        imgURLs.add("https://weekly.ascii.jp/elem/000/000/019/19847/android_limg_280x.jpg");
//        setupImageGrid(imgURLs);
//
//    }
//
//    private void setupImageGrid(ArrayList<String> imgURLs){
//        GridView gridView = findViewById(R.id.gridView);
//
//        int gridWidth = getResources().getDisplayMetrics().widthPixels;
//        int imageWidth = gridWidth/NUM_GRID_COLUMNS;
//        gridView.setColumnWidth(imageWidth);
//
//        GridÎmageAdapter adapter = new GridÎmageAdapter(mContext, R.layout.layout_grid_imageview, "", imgURLs);
//        gridView.setAdapter(adapter);
//    }
//
//    private void setProfileImage(){
//        Log.d(TAG, "setProfileImage: setting profile photo.");
//        String imgURL = "https://weekly.ascii.jp/elem/000/000/019/19847/android_limg_280x.jpg";
//        UniversalImageLoader.setImage(imgURL, profilePhoto, mProgressBar, "https://");
//    }
//
//    private void setupActivityWidgets(){
//        mProgressBar = (ProgressBar) findViewById(R.id.profileProgressBar);
//        mProgressBar.setVisibility(View.GONE);
//        profilePhoto = (ImageView) findViewById(R.id.profile_photo);
//    }
//
//    private  void setupToolbar(){
//        Toolbar toolbar = (Toolbar) findViewById(R.id.profileToolBar);
//        setSupportActionBar(toolbar);
//
//        ImageView profileMenu = (ImageView) findViewById(R.id.profileMenu);
//        profileMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "onClick: navigating to accountsettings");
//                Intent intent = new Intent(mContext, AccountSettingsActivity.class);
//                startActivity(intent);
//            }
//        });
//    }
//
//    /**
//     * BottomNavigationView setup
//     */
//    private void setupBottomNavigationView(){
//        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
//        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
//        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
//        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);
//        Menu menu = bottomNavigationViewEx.getMenu();
//        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
//        menuItem.setChecked(true);
//    }

}
