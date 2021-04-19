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
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
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

    private HashMap<String, ArrayList<MediaFile>> dictMediaFiles = new HashMap<>();

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galleryviewactivity_main);

        startActivity(new Intent(this, CameraActivity.class));
        if(true)
            return;
        
        if (!checkPermission(this))
            return;
        init();

    }

    @SuppressLint("NonConstantResourceId")
    private void init() {

        getAllMediaFiles(dictMediaFiles);

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
    private void getAllMediaFiles(HashMap<String, ArrayList<MediaFile>> dictMediaFiles){
        Uri queryUri = MediaStore.Files.getContentUri("external");

        String[] projections = new String[]{
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.BUCKET_ID,
                MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.TITLE,
                MediaStore.Files.FileColumns.RESOLUTION,
                MediaStore.Files.FileColumns.SIZE };
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + " = ?" + " OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + " = ?";
        String []selectionArgs = new String[]{
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) };
        String sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";

        Cursor resultSet = getContentResolver().query(queryUri, projections, selection, selectionArgs, sortOrder);



        if(resultSet != null){
            while (resultSet.moveToNext()){
                Log.d("Nothing", resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.DATA)));
                Log.d("Nothing", resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns._ID)));
                Log.d("Nothing", resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.BUCKET_ID)));
                Log.d("Nothing", resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME)));
                if(resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.RESOLUTION)) != null)
                    Log.d("Nothing", resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.RESOLUTION)));
                if(resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED)) != null)
                    Log.d("Nothing", resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED)));
                String albumName = resultSet.getString(
                        resultSet.getColumnIndex(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME)
                );
                if (!dictMediaFiles.containsKey(albumName)) {
                    dictMediaFiles.put(albumName, new ArrayList<>());
                }
                int mediaType = resultSet.getInt(resultSet.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE));
                String fileUrl = resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                ArrayList<MediaFile> arrayMediaFiles = dictMediaFiles.get(albumName);
                arrayMediaFiles.add(new MediaFile(mediaType, fileUrl));
            }
        }else{
            Toast.makeText(this,"No such file", Toast.LENGTH_LONG).show();
        }
    }

    public HashMap<String, ArrayList<MediaFile>> getMediaCollections() {
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

    public boolean checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;

        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    context, Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
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

    @Override
    public void permissionGranted() {
        // TODO: The Permission was granted by the user.
        init();

    }
    @Override
    public void permissionDenied() {
        // TODO: The Permission was rejected by the user.


    }

    // *********************************************************************************
    // ***************************        Public methods for Fragments         *********
    // *********************************************************************************
    public void TransitionViewDetail(MediaFile mediaFile){
        Intent imageDetailIntent = null, videoDetailIntent = null;
        Uri queryUri = MediaStore.Files.getContentUri("external");
        String[] projections = new String[]{
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.BUCKET_ID,
                MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.TITLE,
                MediaStore.Files.FileColumns.RESOLUTION,
                MediaStore.Files.FileColumns.SIZE };
        String selection = MediaStore.Files.FileColumns.DATA + "='" + mediaFile.fileUrl + "'";
        Cursor resultSet = getContentResolver().query(queryUri, projections, selection, null, null);
        if(resultSet != null && resultSet.moveToNext()){
            Intent intent = null;
            if(resultSet.getInt(resultSet.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE)) == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE){
                Log.d("Nothing","This is image");
                if(imageDetailIntent == null)
                    imageDetailIntent = new Intent(this, PhotoDetailActivity.class);
                intent = imageDetailIntent;
            }else{
                Log.d("Nothing","This is video");
                if(videoDetailIntent == null)
                    videoDetailIntent = new Intent(this, VideoDetailActivity.class);
                intent = videoDetailIntent;
            }
            intent.putExtra(MediaFile.FILE_ID, resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns._ID)));
            intent.putExtra(MediaFile.FILE_PATH, resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.DATA)));
            intent.putExtra(MediaFile.FILE_ALBUM_NAME, resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME)));
            intent.putExtra(MediaFile.FILE_DATE, resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED)));
            intent.putExtra(MediaFile.FILE_RESOLUTION, resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.RESOLUTION)));
            intent.putExtra(MediaFile.FILE_SIZE, resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.SIZE)));
            startActivity(intent);
        }else{
            Toast.makeText(this,"No such file", Toast.LENGTH_LONG).show();
        }
    }
}
