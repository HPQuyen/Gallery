package com.example.galleryapplication.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.galleryapplication.classes.DataHandler;
import com.example.galleryapplication.fragments.mainviews.AlbumFragment;
import com.example.galleryapplication.classes.MediaFile;
import com.example.galleryapplication.R;
import com.example.galleryapplication.fragments.mainviews.ViewAllDateFragment;
import com.example.galleryapplication.fragments.mainviews.ViewAllDetailsFragment;
import com.example.galleryapplication.fragments.mainviews.ViewAllGridFragment;
import com.example.galleryapplication.enumerators._LAYOUT;
import com.example.galleryapplication.enumerators._VIEW;
import com.example.galleryapplication.interfaces.IOnBackPressed;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import ly.img.android.pesdk.ui.utils.PermissionRequest;

public class GalleryViewActivity extends AppCompatActivity implements PermissionRequest.Response {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 987;

    private TextView mainTitle;
    private Menu optionsMenuActionBar;

    private _LAYOUT mainLayout = _LAYOUT._GRID;
    private _VIEW mainView = _VIEW._ALL;

    private Fragment viewAllGridFragment;
    private Fragment viewAllDateFragment;
    private Fragment viewAllDetailsFragment;

    private Fragment albumFragment;


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_galleryview);
        
        if (!checkPermission(this))
            return;
        init();

    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @SuppressLint({"NonConstantResourceId"})
    private void init() {

        DataHandler.LoadAllMediaFiles(this);

        Toolbar toolbar = findViewById(R.id.main_Toolbar);
        setSupportActionBar(toolbar);

        ActionBar mainActionBar = getSupportActionBar();
        assert mainActionBar != null;
        mainActionBar.setDisplayShowTitleEnabled(false);

        mainTitle = findViewById(R.id.main_Title);

        viewAllGridFragment = new ViewAllGridFragment();
        viewAllDateFragment = new ViewAllDateFragment();
        viewAllDetailsFragment = new ViewAllDetailsFragment();

        albumFragment = new AlbumFragment();

        setCurrentFragment(viewAllGridFragment);

        BottomNavigationView bottomNavBar = findViewById(R.id.main_BottomNavigator);
        bottomNavBar.setOnNavigationItemSelectedListener(
                item -> {
                    switch (item.getItemId()) {
                        case R.id.fragItems_ViewAll:
                            mainView = _VIEW._ALL;
                            invalidateOptionsMenu();

                            switch (mainLayout) {
                                case _GRID:
                                    setCurrentFragment(viewAllGridFragment);
                                    break;
                                case _DATE:
                                    setCurrentFragment(viewAllDateFragment);
                                    break;
                                case _DETAILS:
                                    setCurrentFragment(viewAllDetailsFragment);
                                    break;
                            }

                            mainTitle.setText(R.string.title_default_1);
                            break;

                        case R.id.fragItems_Albums:
                            mainView = _VIEW._ALBUMS;
                            invalidateOptionsMenu();

                            setCurrentFragment(albumFragment);

                            mainTitle.setText(R.string.title_albums_1);
                            break;
                    }
                    return true;
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (optionsMenuActionBar == null) optionsMenuActionBar = menu;

        MenuInflater inflater = getMenuInflater();

        switch (mainView) {
            case _ALL:
                inflater.inflate(R.menu.actionbar_viewall_menu, menu);
                return true;
            case _ALBUMS:
                inflater.inflate(R.menu.actionbar_album_menu, menu);
                return true;
        }

        return false;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.GridDate_ViewAll:
                if (mainLayout == _LAYOUT._DATE) break;
                mainLayout = _LAYOUT._DATE;

                setCurrentFragment(viewAllDateFragment);

                return true;

            case R.id.GridOnly_ViewAll:
                if (mainLayout == _LAYOUT._GRID) break;
                mainLayout = _LAYOUT._GRID;

                setCurrentFragment(viewAllGridFragment);

                return true;

            case R.id.DetailsList_ViewAll:
                if (mainLayout == _LAYOUT._DETAILS) break;
                mainLayout = _LAYOUT._DETAILS;

                setCurrentFragment(viewAllDetailsFragment);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_FrameLayout);
        if (!(fragment instanceof IOnBackPressed) || !((IOnBackPressed) fragment).onBackPressed()) {
            super.onBackPressed();
        }
    }

    private void setCurrentFragment (Fragment fragment) {
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.fragment_FrameLayout, fragment);
        // fragTransaction.addToBackStack(null);
        fragTransaction.commit();
    }

    public void settings(MenuItem item) {
        Intent intent = new Intent(
                GalleryViewActivity.this, SettingsActivity.class
        );
        startActivity(intent);
    }

    @Override
    public void permissionGranted() {
        // TODO: The Permission was granted by the user.

    }

    @Override
    public void permissionDenied() {
        // TODO: The Permission was rejected by the user.

    }

    // *********************************************************************************
    // ***************************        PERMISSION         ***************************
    // *********************************************************************************
    // Important permission request for Android 6.0 and above, don't forget to add this!
    public boolean checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;

        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    context, Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                    context, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ) {

                ActivityCompat.requestPermissions(
                        (Activity) context,
                        new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE },
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                );
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults
    ) {
        PermissionRequest.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                init();
            } else {
                Toast.makeText(this, "Access Permission Denied",
                        Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions,
                    grantResults);
        }
    }

    // *********************************************************************************
    // *******************        Public methods for Fragments         *****************
    // *********************************************************************************
    @RequiresApi(api = Build.VERSION_CODES.R)
    public void TransitionViewDetail(MediaFile mediaFile){
        Intent imageDetailIntent, videoDetailIntent;
        Intent intent;
        if(mediaFile.mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE){
            Log.d("Nothing","This is image");
            imageDetailIntent = new Intent(this, PhotoDetailActivity.class);
            intent = imageDetailIntent;
        }else{
            Log.d("Nothing","This is video");
            videoDetailIntent = new Intent(this, VideoDetailActivity.class);
            intent = videoDetailIntent;
        }
        intent.putExtra(MediaFile.FILE_ID, mediaFile.id);
        intent.putExtra(MediaFile.FILE_PATH, mediaFile.fileUrl);
        intent.putExtra(MediaFile.FILE_ALBUM_NAME, mediaFile.albumName);
        intent.putExtra(MediaFile.FILE_DATE, mediaFile.datetime);
        intent.putExtra(MediaFile.FILE_RESOLUTION, mediaFile.resolution);
        intent.putExtra(MediaFile.FILE_SIZE, mediaFile.fileSize);
        intent.putExtra(MediaFile.FILE_MEDIA_TYPE, mediaFile.mediaType);
        intent.putExtra(MediaFile.FILE_FAVOURITE, mediaFile.isFavourite);
        startActivity(intent);
    }

    public _LAYOUT getMainLayout() {
        return this.mainLayout;
    }
}
