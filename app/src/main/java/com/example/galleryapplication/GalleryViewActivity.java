package com.example.galleryapplication;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;

import ly.img.android.pesdk.ui.utils.PermissionRequest;

public class GalleryViewActivity extends AppCompatActivity implements PermissionRequest.Response {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 987;

    private ActionBar mainActionbar;
    private Menu optionsMenuActionBar;

    private _LAYOUT mainLayout = _LAYOUT._GRID;
    private _VIEW mainView = _VIEW._ALL;

    private Fragment viewAllGridFragment;
    private Fragment viewAllDateFragment;
    private Fragment viewAllDetailsFragment;

    private Fragment albumFragment;

    private HashMap<String, ArrayList<String>> dictMediaFiles;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galleryviewactivity_main);

        if (checkPermission(this)) init();

        dictMediaFiles = new HashMap<>();
        getAllImages(dictMediaFiles);
        getAllVideos(dictMediaFiles);
    }

    @SuppressLint("NonConstantResourceId")
    private void init() {
        mainActionbar = getSupportActionBar();

        assert mainActionbar != null;
        mainActionbar.setDisplayShowTitleEnabled(true);

        viewAllGridFragment = new ViewAllGridFragment();
        viewAllDateFragment = new ViewAllDateFragment();
        viewAllDetailsFragment = new ViewAllDetailsFragment();

        albumFragment = new AlbumFragment();

        setCurrentFragment(viewAllGridFragment);

        BottomNavigationView bottomNavBar = findViewById(R.id.main_bottomNavigator);
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
                                case __DETAILS:
                                    setCurrentFragment(viewAllDetailsFragment);
                                    break;
                            }

                            mainActionbar.setTitle("Photos & Videos");
                            break;

                        case R.id.fragItems_Albums:
                            mainView = _VIEW._ALBUMS;
                            invalidateOptionsMenu();

                            setCurrentFragment(albumFragment);

                            mainActionbar.setTitle("Albums");
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
                if (mainLayout == _LAYOUT.__DETAILS) break;
                mainLayout = _LAYOUT.__DETAILS;

                setCurrentFragment(viewAllDetailsFragment);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

        return false;
    }

    private void setCurrentFragment (Fragment fragment) {
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.fragment_FrameLayout, fragment);
        fragTransaction.commit();
    }

    public void settings(MenuItem item) {
        Intent intent = new Intent(
                GalleryViewActivity.this, SettingsActivity.class
        );
        startActivity(intent);
    }

    // *********************************************************************************
    // **************************  GET DATA IN MOBILE DEVICES  *************************
    // *********************************************************************************
    // Important permission request for Android 6.0 and above, don't forget to add this!
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("Recycle")
    private void getAllImages(HashMap<String, ArrayList<String>> dictImages) {
        // final int _MAXLOADING = 10;

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projections = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.BUCKET_ID,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.SIZE
        };
        Cursor cursor = this.getContentResolver().query(
                uri, projections, null, null, null
        );

        // int i = 0;
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String albumName = cursor.getString(
                        cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)
                );

                if (!dictImages.containsKey(albumName)) {
                    dictImages.put(albumName, new ArrayList<>());
                }
                ArrayList<String> arrayImages = dictImages.get(albumName);
                String imagePath = cursor.getString(
                        cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                );

                assert arrayImages != null;
                arrayImages.add(imagePath);

                // i++;
                // if (i >= _MAXLOADING) break;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("Recycle")
    private void getAllVideos(HashMap<String, ArrayList<String>> dictVideos) {
        // final int _MAXLOADING = 10;

        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projections = new String[]{
                MediaStore.Video.VideoColumns._ID,
                MediaStore.Video.VideoColumns.BUCKET_ID,
                MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Video.VideoColumns.DATA,
                MediaStore.Video.VideoColumns.SIZE
        };
        Cursor cursor = this.getContentResolver().query(
                uri, projections, null, null, null
        );

        // int i = 0;
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String albumName = cursor.getString(
                        cursor.getColumnIndex(MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME)
                );

                if (!dictVideos.containsKey(albumName)) {
                    dictVideos.put(albumName, new ArrayList<>());
                }
                ArrayList<String> arrayVideos = dictVideos.get(albumName);
                String imagePath = cursor.getString(
                        cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA)
                );

                assert arrayVideos != null;
                arrayVideos.add(imagePath);

                // i++;
                // if (i >= _MAXLOADING) break;
            }
        }
    }

    public HashMap<String, ArrayList<String>> getMediaCollections() {
        return this.dictMediaFiles;
    }

    // *********************************************************************************
    // ***************************        PERMISSION         ***************************
    // *********************************************************************************
    // Important permission request for Android 6.0 and above, don't forget to add this!
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults
    ) {
        PermissionRequest.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                init();
            } else {
                Toast.makeText(this, "Access Permission Denied",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions,
                    grantResults);
        }
    }

    public boolean checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;

        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    context, Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        (Activity) context,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
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

    @Override
    public void permissionGranted() {
        // TODO: The Permission was rejected by the user.

    }
    @Override
    public void permissionDenied() {
        // TODO: The Permission was rejected by the user.

    }
}
