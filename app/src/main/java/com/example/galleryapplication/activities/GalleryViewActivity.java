package com.example.galleryapplication.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.example.galleryapplication.R;
import com.example.galleryapplication.classes.Constants;
import com.example.galleryapplication.classes.DataHandler;
import com.example.galleryapplication.classes.MediaFile;
import com.example.galleryapplication.classes.Observer;
import com.example.galleryapplication.enumerators._LAYOUT;
import com.example.galleryapplication.enumerators._VIEW;
import com.example.galleryapplication.fragments.mainviews.AlbumFragment;
import com.example.galleryapplication.fragments.mainviews.SlideshowFragment;
import com.example.galleryapplication.fragments.mainviews.ViewAllDateFragment;
import com.example.galleryapplication.fragments.mainviews.ViewAllDetailsFragment;
import com.example.galleryapplication.fragments.mainviews.ViewAllGridFragment;
import com.example.galleryapplication.interfaces.IAction;
import com.example.galleryapplication.interfaces.IOnBackPressed;
import com.example.galleryapplication.utils.LanguageHandler;
import com.example.galleryapplication.utils.SharedPrefs;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import ly.img.android.pesdk.ui.utils.PermissionRequest;

public class GalleryViewActivity extends AppCompatActivity implements PermissionRequest.Response {
    
    private TextView mainTitle;
    private Menu optionsMenuActionBar;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavBar;

    private _LAYOUT mainLayout = _LAYOUT._GRID;
    private _VIEW mainView = _VIEW._ALL;

    private _LAYOUT albumLayout = _LAYOUT._GRID;
    private _LAYOUT favoriteLayout = _LAYOUT._GRID;

    private Fragment viewAllGridFragment;
    private Fragment viewAllDateFragment;
    private Fragment viewAllDetailsFragment;
    private Fragment slideshowFragment;

    private Fragment albumFragment;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Dark mode setup
        Boolean isInDarkMode =
                SharedPrefs.getInstance().get(SharedPrefs.DARKTHEME, Boolean.class);

        if (isInDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Language setup
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        LanguageHandler.loadLocale(this);

        // Layout setup
        setContentView(R.layout.activity_main_galleryview);

        if (!checkPermission(this))
            return;
        init();
    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    @SuppressLint({"NonConstantResourceId", "ResourceAsColor"})
    private void init() {

        new Thread(() -> DataHandler.LoadAllMediaFiles(this)).start();


        toolbar = findViewById(R.id.main_Toolbar);
        setSupportActionBar(toolbar);

        ActionBar mainActionBar = getSupportActionBar();
        assert mainActionBar != null;
        mainActionBar.setDisplayShowTitleEnabled(false);

        mainTitle = findViewById(R.id.main_Title);

        viewAllGridFragment = new ViewAllGridFragment();
        viewAllDateFragment = new ViewAllDateFragment();
        viewAllDetailsFragment = new ViewAllDetailsFragment();
        slideshowFragment = SlideshowFragment.newInstance();

        albumFragment = new AlbumFragment();

        setCurrentFragment(viewAllGridFragment);

        bottomNavBar = findViewById(R.id.main_BottomNavigator);
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

                        case R.id.fragItems_Favorite:
                            mainView = _VIEW._FAVORITE;
                            invalidateOptionsMenu();

                            switch (favoriteLayout) {
                                case _GRID:
                                    // TODO
                                    break;
                                case _DATE:
                                    // TODO
                                    break;
                                case _DETAILS:
                                    // TODO
                                    break;
                            }

                            mainTitle.setText(R.string.title_favorite_1);
                            break;

                        case R.id.fragItems_Camera:
                            // TODO

                            item.setCheckable(false);
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

                switch (this.mainLayout) {
                    case _DATE:
                        menu.findItem(R.id.ViewDropDown_ViewAll)
                                .setIcon(R.drawable.ic_griddate_layout);
                        break;
                    case _DETAILS:
                        menu.findItem(R.id.ViewDropDown_ViewAll)
                                .setIcon(R.drawable.ic_details_layout);
                        break;
                    case _GRID:
                    default:
                        menu.findItem(R.id.ViewDropDown_ViewAll)
                                .setIcon(R.drawable.ic_gridonly_layout);
                }
                return true;
            case _ALBUMS:
                inflater.inflate(R.menu.actionbar_album_menu, menu);
                return true;
            case _FAVORITE:
                inflater.inflate(R.menu.actionbar_favorite_menu, menu);

                switch (this.favoriteLayout) {
                    case _DATE:
                        menu.findItem(R.id.ViewDropDown_Favorite)
                                .setIcon(R.drawable.ic_griddate_layout);
                        break;
                    case _DETAILS:
                        menu.findItem(R.id.ViewDropDown_Favorite)
                                .setIcon(R.drawable.ic_details_layout);
                        break;
                    case _GRID:
                    default:
                        menu.findItem(R.id.ViewDropDown_Favorite)
                                .setIcon(R.drawable.ic_gridonly_layout);
                }

                return true;
        }

        return false;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /* ************************* View All ************************* */
            case R.id.GridDate_ViewAll:
                if (mainLayout == _LAYOUT._DATE) break;
                mainLayout = _LAYOUT._DATE;

                invalidateOptionsMenu();
                setCurrentFragment(viewAllDateFragment);

                return true;

            case R.id.GridOnly_ViewAll:
                if (mainLayout == _LAYOUT._GRID) break;
                mainLayout = _LAYOUT._GRID;

                invalidateOptionsMenu();
                setCurrentFragment(viewAllGridFragment);

                return true;

            case R.id.DetailsList_ViewAll:
                if (mainLayout == _LAYOUT._DETAILS) break;
                mainLayout = _LAYOUT._DETAILS;

                invalidateOptionsMenu();
                setCurrentFragment(viewAllDetailsFragment);

                return true;

            /* ************************* Favorite ************************* */
            case R.id.GridDate_Favorite:
                if (favoriteLayout == _LAYOUT._DATE) break;
                favoriteLayout = _LAYOUT._DATE;

                invalidateOptionsMenu();
                // TODO

                return true;

            case R.id.GridOnly_Favorite:
                if (favoriteLayout == _LAYOUT._GRID) break;
                favoriteLayout = _LAYOUT._GRID;

                invalidateOptionsMenu();
                // TODO

                return true;

            case R.id.DetailsList_Favorite:
                if (favoriteLayout == _LAYOUT._DETAILS) break;
                favoriteLayout = _LAYOUT._DETAILS;

                invalidateOptionsMenu();
                // TODO

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_Settings_FrameLayout);
        if (!(fragment instanceof IOnBackPressed) || !((IOnBackPressed) fragment).onBackPressed()) {
            super.onBackPressed();
        }
    }

    private void setCurrentFragment (Fragment fragment) {
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.fragment_Settings_FrameLayout, fragment);
        fragTransaction.commit();
    }

    public void addAlbum(MenuItem item) {
        Intent intent = new Intent(
                GalleryViewActivity.this, CreateAlbumActivity.class
        );
        startActivityForResult(intent, Constants.RequestCode.CREATE_ALBUM_REQUEST_CODE);

        // DataHandler.AddNewAlbum(this, )
    }

    public void HideUI(){
        toolbar.setVisibility(View.GONE);
        bottomNavBar.setVisibility(View.GONE);
    }

    public void ShowUI(){
        toolbar.setVisibility(View.VISIBLE);
        bottomNavBar.setVisibility(View.VISIBLE);
    }

    //#region On Click Event
    public void settings(MenuItem item) {
        Intent intent = new Intent(
                GalleryViewActivity.this, SettingsActivity.class );
        startActivityForResult(intent, Constants.RequestCode.SETTINGS_REQUEST_CODE);
    }

    public void OnClickSlideshow(MenuItem item){
//        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
//        fragTransaction.replace(R.id.fragment_Settings_FrameLayout, slideshowFragment);
//        fragTransaction.addToBackStack(null);
//        fragTransaction.commit();
        startActivityForResult(new Intent(this, IncognitoFolderActivity.class), Constants.RequestCode.INCOGNITO_FOLDER_REQUEST_CODE);
    }

    public void OnClickIncognitoFolder(){
        startActivityForResult(new Intent(this, IncognitoFolderActivity.class), Constants.RequestCode.INCOGNITO_FOLDER_REQUEST_CODE);
    }

    public void OnClickCamera(){
        startActivityForResult(new Intent(this, CameraActivity.class), Constants.RequestCode.CAMERA_REQUEST_CODE);
    }
    //#endregion

    // *********************************************************************************
    // ***************************        PERMISSION         ***************************
    // *********************************************************************************
    @Override
    public void permissionGranted() {
        // TODO: The Permission was granted by the user.

    }

    @Override
    public void permissionDenied() {
        // TODO: The Permission was rejected by the user.

    }

    // Important permission request for Android 6.0 and above, don't forget to add this!
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public boolean checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;

        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    context, Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                    context, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ){

                ActivityCompat.requestPermissions(
                        (Activity) context,
                        new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE },
                        Constants.RequestCode.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionRequest.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.RequestCode.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                init();
            }else{
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
    public void TransitionAlbumFragment(String albumName) {
        Intent intent = new Intent(
                GalleryViewActivity.this, AlbumDetailActivity.class
        );

        intent.putExtra("LAYOUT", albumLayout);
        intent.putExtra("ALBUM_NAME", albumName);

        startActivityForResult(intent, Constants.RequestCode.ALBUM_DETAIL_REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case Constants.RequestCode.CAMERA_REQUEST_CODE:
                case Constants.RequestCode.INCOGNITO_FOLDER_REQUEST_CODE:
                case Constants.RequestCode.VIEW_DETAIL_REQUEST_CODE:
                    if(data != null){
                        if(data.getBooleanExtra("CHANGE", false))
                        {
                            DataHandler.UpdateMediaFiles(this);
                            Observer.Invoke(Observer.ObserverCode.TRIGGER_ADAPTER_CHANGE);
                        }
                    }
                    break;

                case Constants.RequestCode.SETTINGS_REQUEST_CODE:
                    recreate();
                    break;

                case Constants.RequestCode.CREATE_ALBUM_REQUEST_CODE:
                    if (data != null) {
                        DataHandler.AddNewAlbum(
                                this,
                                data.getExtras().getString("ALBUM_NAME"),
                                data.getExtras()
                                        .getStringArrayList("SELECTED_MEDIA")
                                );
                        Observer.Invoke(Observer.ObserverCode.TRIGGER_ADAPTER_CHANGE);
                    }
                    break;
                case Constants.RequestCode.ALBUM_DETAIL_REQUEST_CODE:
                    break;
            }
        }
    }

}
