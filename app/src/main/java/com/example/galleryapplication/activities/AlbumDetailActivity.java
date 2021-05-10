package com.example.galleryapplication.activities;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;

import com.example.galleryapplication.R;
import com.example.galleryapplication.classes.Constants;
import com.example.galleryapplication.classes.DataHandler;
import com.example.galleryapplication.classes.MediaFile;
import com.example.galleryapplication.classes.Observer;
import com.example.galleryapplication.enumerators._LAYOUT;
import com.example.galleryapplication.fragments.subviews.album.AlbumDetailDateFragment;
import com.example.galleryapplication.fragments.subviews.album.AlbumDetailDetailsFragment;
import com.example.galleryapplication.fragments.subviews.album.AlbumDetailGridFragment;
import com.example.galleryapplication.interfaces.IOnBackPressed;

import java.util.ArrayList;

public class AlbumDetailActivity extends AppCompatActivity {

    private Menu optionsMenuActionBar;

    private _LAYOUT albumLayout;
    private String albumName;

    private Fragment albumDetailGridFragment;
    private Fragment albumDetailDateFragment;
    private Fragment albumDetailDetailsFragment;

    private boolean isChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);

        init();
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.albumDetailToolbar);
        setSupportActionBar(toolbar);

        ActionBar mainActionBar = getSupportActionBar();
        assert mainActionBar != null;
        mainActionBar.setDisplayShowTitleEnabled(false);
        mainActionBar.setHomeAsUpIndicator(R.drawable.ic_back_to_previous);
        mainActionBar.setDisplayHomeAsUpEnabled(true);

        albumDetailGridFragment = new AlbumDetailGridFragment();
        albumDetailDateFragment = new AlbumDetailDateFragment();
        albumDetailDetailsFragment = new AlbumDetailDetailsFragment();

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();

            this.albumLayout = (_LAYOUT) bundle.getSerializable("LAYOUT");
            this.albumName = bundle.getString("ALBUM_NAME");
        }

        switch (this.albumLayout) {
            case _DATE:
                setCurrentFragment(albumDetailDateFragment);
                break;
            case _DETAILS:
                setCurrentFragment(albumDetailDetailsFragment);
                break;
            case _GRID:
            default:
                setCurrentFragment(albumDetailGridFragment);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if (optionsMenuActionBar == null) optionsMenuActionBar = menu;

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_album_detail_menu, menu);

        switch (this.albumLayout) {
            case _DATE:
                menu.findItem(R.id.ViewDropDown_AlbumDetail)
                        .setIcon(R.drawable.ic_griddate_layout);
                break;
            case _DETAILS:
                menu.findItem(R.id.ViewDropDown_AlbumDetail)
                        .setIcon(R.drawable.ic_details_layout);
                break;
            case _GRID:
            default:
                menu.findItem(R.id.ViewDropDown_AlbumDetail)
                        .setIcon(R.drawable.ic_gridonly_layout);
        }

        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /* ************************* Album ************************* */
            case R.id.GridDate_AlbumDetail:
                if (this.albumLayout == _LAYOUT._DATE) break;
                this.albumLayout = _LAYOUT._DATE;

                invalidateOptionsMenu();
                setCurrentFragment(albumDetailDateFragment);

                return true;

            case R.id.GridOnly_AlbumDetail:
                if (this.albumLayout == _LAYOUT._GRID) break;
                this.albumLayout = _LAYOUT._GRID;

                invalidateOptionsMenu();
                setCurrentFragment(albumDetailGridFragment);

                return true;

            case R.id.DetailsList_AlbumDetail:
                if (this.albumLayout == _LAYOUT._DETAILS) break;
                this.albumLayout = _LAYOUT._DETAILS;

                invalidateOptionsMenu();
                setCurrentFragment(albumDetailDetailsFragment);

                return true;

            case android.R.id.home:
                backToPrevious();

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
            backToPrevious();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode){
                case Constants.RequestCode.ALBUM_MODIFY_REQUEST_CODE:
                    if (data != null) {
                        Bundle bundle = data.getExtras();

                        String albumName = bundle.getString("ALBUM_NAME");
                        ArrayList<String> albumPhotos = bundle.getStringArrayList("SELECTED_MEDIA");

                        DataHandler.RenameAlbum(this, this.albumName, albumName);

                        DataHandler.UpdateAlbum(
                                this,
                                albumName,
                                albumPhotos
                        );
                        this.albumName = albumName;
                        this.isChanged = true;
                        Observer.Invoke(Observer.ObserverCode.TRIGGER_ADAPTER_ALBUM_CHANGE, getAlbumName());
                    }
                    break;
                case Constants.RequestCode.VIEW_DETAIL_REQUEST_CODE:
                    if(data != null){
                        Log.d("Nothing", "" + data.getBooleanExtra("CHANGE", false));
                        if(data.getBooleanExtra("CHANGE", false))
                        {
                            this.isChanged = true;
                            Observer.Invoke(Observer.ObserverCode.TRIGGER_ADAPTER_ALBUM_CHANGE, getAlbumName());
                        }
                    }
                    break;
                default:
                    break;
            }
        }

    }

    private void setCurrentFragment(Fragment fragment) {
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.albumDetailFrameLayout, fragment);
        fragTransaction.commit();
    }

    private void backToPrevious() {
        Intent intent = new Intent();

        intent.putExtra("LAYOUT", this.albumLayout);
        intent.putExtra("IS_CHANGED", this.isChanged);

        setResult(RESULT_OK, intent);

        finish();
        super.onBackPressed();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void modifyAlbum(MenuItem menuItem) {
        Intent intent = new Intent(
                this, ModifyAlbumActivity.class
        );

        intent.putExtra("ALBUM_NAME", this.albumName);

        ArrayList<MediaFile> mediaFiles =
                DataHandler.GetMediaFileByAlbum(
                        this,
                        albumName,
                        DataHandler.ALL
                );
        ArrayList<String> mediaFileIDs = new ArrayList<>();
        for (MediaFile mediaFile : mediaFiles) {
            mediaFileIDs.add(mediaFile.id);
        }

        intent.putExtra("SELECTED_MEDIA", mediaFileIDs);

        startActivityForResult(intent, Constants.RequestCode.ALBUM_MODIFY_REQUEST_CODE);
    }

    public void deleteAlbum(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.delete_album_dialog_message_1)
                .setTitle(R.string.delete_album_dialog_title_1);
        builder.setPositiveButton(
                R.string.delete_album_ok_1,
                (DialogInterface.OnClickListener) (dialog, id) -> {
                    DataHandler.RemoveAlbum(this, this.albumName);

                    isChanged = true;
                    backToPrevious();
                });
        builder.setNegativeButton(
                R.string.delete_album_cancel_1,
                (DialogInterface.OnClickListener) (dialog, id) -> dialog.dismiss()
        );

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public String getAlbumName() {
        return this.albumName;
    }
}