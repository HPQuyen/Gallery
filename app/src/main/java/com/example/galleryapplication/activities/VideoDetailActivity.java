package com.example.galleryapplication.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.galleryapplication.classes.DataHandler;
import com.example.galleryapplication.classes.MediaFile;
import com.example.galleryapplication.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class VideoDetailActivity extends AppCompatActivity {

    //#region Fiedls

    //#region Layout Fields
    private VideoView videoView;
    private BottomSheetDialog infoBottomSheetDialog = null;
    private PopupMenu popupMenu = null;

    private FloatingActionButton menuFab = null;
    private FloatingActionButton favouriteFab = null;
    private FloatingActionButton shareFab = null;
    private FloatingActionButton deleteFab = null;
    private FloatingActionButton moreFab = null;

    private Animation rotateOpen;
    private Animation rotateClose;
    private Animation fromBotton;
    private Animation toBottom;
    //#endregion
    private static final String PLAYBACK_TIME = "play_time";
    public final static int PICK_FOLDER_REQUEST_CODE = 9999;

    private int currentPosition = 0;
    private boolean isChange = false;
    private boolean menuFabClick = false;
    private MediaFile selectedMediaFile;
    //#endregion
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_video);

        if(savedInstanceState != null){
            currentPosition = savedInstanceState.getInt(PLAYBACK_TIME);
            Log.d("Nothing","load instance");
        }
        Init();
    }

    @SuppressLint("RestrictedApi")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void Init(){
        // Set up animation
        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        fromBotton = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);

        // Load data
        Intent intent = getIntent();
        if(intent != null){
            selectedMediaFile = new MediaFile(
                    intent.getStringExtra(MediaFile.FILE_ID),
                    intent.getIntExtra(MediaFile.FILE_MEDIA_TYPE, MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                    intent.getStringExtra(MediaFile.FILE_PATH),
                    intent.getStringExtra(MediaFile.FILE_DATE),
                    intent.getStringExtra(MediaFile.FILE_SIZE),
                    intent.getStringExtra(MediaFile.FILE_RESOLUTION),
                    intent.getStringExtra(MediaFile.FILE_FOLDER_NAME),
                    intent.getBooleanExtra(MediaFile.FILE_FAVOURITE, false));
        }

        // Set up view detail info
        infoBottomSheetDialog = new BottomSheetDialog(this);
        View infoBottomSheetView = LayoutInflater.from(this).inflate(R.layout.fragment_info_bottom_sheet, findViewById(R.id.info_bottom_sheet_container));
        TextInputEditText infoDatetime = infoBottomSheetView.findViewById(R.id.info_datetime_tv);
        infoDatetime.setText(selectedMediaFile.datetime);
        TextInputEditText infoResolution = infoBottomSheetView.findViewById(R.id.info_resolution_tv);
        infoResolution.setText(selectedMediaFile.resolution);
        TextInputEditText infoFileSize = infoBottomSheetView.findViewById(R.id.info_file_size_tv);
        infoFileSize.setText(selectedMediaFile.fileSize);
        TextInputEditText infoAlbumName = infoBottomSheetView.findViewById(R.id.info_folder_name_tv);
        infoAlbumName.setText(selectedMediaFile.folderName);
        TextInputEditText infoFilePath = infoBottomSheetView.findViewById(R.id.info_file_path_tv);
        infoFilePath.setText(selectedMediaFile.fileUrl);
        infoBottomSheetDialog.setContentView(infoBottomSheetView);

        // Set up Video View
        videoView = findViewById(R.id.video_view_detail);
        MediaController videoController = new MediaController(this);
        videoController.setAnchorView(videoView);
        videoView.setMediaController(videoController);
        videoView.setOnCompletionListener(mediaPlayer -> {
            Toast.makeText(VideoDetailActivity.this, "Playback completed", Toast.LENGTH_SHORT).show();
            videoView.seekTo(1);
        });


        // Set up menu float action button
        menuFab = findViewById(R.id.fab_menu);
        menuFab.setOnClickListener(this::OnClickFabMenu);
        // Set up favourite fab
        favouriteFab = findViewById(R.id.favourite_fab);
        favouriteFab.setImageDrawable(selectedMediaFile.isFavourite?getDrawable(R.drawable.ic_baseline_favorite_32):getDrawable(R.drawable.ic_baseline_favorite_border_24));
        favouriteFab.setOnClickListener(this::OnClickFavorite);
        // Set up share fab
        shareFab = findViewById(R.id.share_fab);
        shareFab.setOnClickListener(this::OnClickShare);
        // Set up delete fab
        deleteFab = findViewById(R.id.delete_fab);
        deleteFab.setOnClickListener(this::OnClickDelete);
        // Set up more fab
        moreFab = findViewById(R.id.more_fab);
        moreFab.setOnClickListener(this::OnClickMore);

        popupMenu = new PopupMenu(this, moreFab);
        popupMenu.inflate(R.menu.top_app_bar_menu_3);
        MenuBuilder menu = (MenuBuilder) popupMenu.getMenu();
        menu.setOptionalIconsVisible(true);
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.info:
                    Log.d("Nothing", "Info Click");
                    OnClickInfoDetail();
                    return true;
                case R.id.copy:
                    Log.d("Nothing", "Copy Click");
                    OnClickCopy();
                    return true;
                default:
                    return false;
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        InitializePlayer();
    }

    private void InitializePlayer(){
        videoView.setVideoPath(selectedMediaFile.fileUrl);
        videoView.setOnPreparedListener(mp -> {
            // Restore saved position, if available.
            if (currentPosition > 0) {
                videoView.seekTo(currentPosition);
            } else {
                // Skipping to 1 shows the first frame of the video.
                videoView.seekTo(1);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("Nothing","save instance");
        outState.putInt(PLAYBACK_TIME, videoView.getCurrentPosition());
    }
    @Override
    protected void onStop() {
        super.onStop();
        videoView.stopPlayback();
    }

    //#region On Click Event
    private void OnClickMore(View view){
        popupMenu.show();
    }
    private void OnClickFabMenu(View view){
        menuFabClick = !menuFabClick;
        if(menuFabClick){
            favouriteFab.setVisibility(View.VISIBLE);
            shareFab.setVisibility(View.VISIBLE);
            deleteFab.setVisibility(View.VISIBLE);
            moreFab.setVisibility(View.VISIBLE);

            menuFab.startAnimation(rotateOpen);
            favouriteFab.startAnimation(fromBotton);
            shareFab.startAnimation(fromBotton);
            deleteFab.startAnimation(fromBotton);
            moreFab.startAnimation(fromBotton);

            favouriteFab.setClickable(true);
            shareFab.setClickable(true);
            deleteFab.setClickable(true);
            moreFab.setClickable(true);
        }else {
            favouriteFab.setVisibility(View.INVISIBLE);
            shareFab.setVisibility(View.INVISIBLE);
            deleteFab.setVisibility(View.INVISIBLE);
            moreFab.setVisibility(View.INVISIBLE);

            menuFab.startAnimation(rotateClose);
            favouriteFab.startAnimation(toBottom);
            shareFab.startAnimation(toBottom);
            deleteFab.startAnimation(toBottom);
            moreFab.startAnimation(toBottom);

            favouriteFab.setClickable(false);
            shareFab.setClickable(false);
            deleteFab.setClickable(false);
            moreFab.setClickable(false);
        }
    }
    private void OnClickDelete(View view){
        MaterialAlertDialogBuilder deleteAlert = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.title_delete_confirm)
                .setMessage(R.string.sub_text_delete_confirm)
                .setNegativeButton(R.string.cancel_btn, (dialog, which) -> {
                    Snackbar.make(findViewById(R.id.constraintLayout), R.string.title_delete_cancel, Snackbar.LENGTH_SHORT)
                            .show();
                })
                .setPositiveButton(R.string.delete_btn, (dialog, which) -> {
                    if(MediaFile.DeleteMediaFile(this, selectedMediaFile.fileUrl)){
                        isChange = true;
                        onBackPressed();
                    }
                });
        deleteAlert.show();
    }
    private void OnClickShare(View view){
        MediaFile.ShareMediaFile(this, selectedMediaFile);
    }
    private void OnClickInfoDetail(){
        if(infoBottomSheetDialog.isShowing()){
            infoBottomSheetDialog.dismiss();
        }else {
            infoBottomSheetDialog.show();
        }
    }
    private void OnClickCopy(){
        Intent intent = new Intent(this, PickFolderActivity.class);
        startActivityForResult(intent, PICK_FOLDER_REQUEST_CODE);
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void OnClickFavorite(View view){
        Log.d("Nothing", "Favourite Click");
        if(selectedMediaFile.isFavourite){
            Snackbar.make(findViewById(R.id.constraintLayout), R.string.title_remove_from_favourite, Snackbar.LENGTH_SHORT)
                    .show();
            DataHandler.RemoveFromFavourite(this, selectedMediaFile.id);
        }else{
            DataHandler.AddToFavourite(this, selectedMediaFile.id);
            Snackbar.make(findViewById(R.id.constraintLayout), R.string.title_add_to_favourite, Snackbar.LENGTH_SHORT)
                    .show();
        }
        selectedMediaFile.isFavourite = !selectedMediaFile.isFavourite;
        favouriteFab.setImageDrawable(selectedMediaFile.isFavourite?getDrawable(R.drawable.ic_baseline_favorite_32):getDrawable(R.drawable.ic_baseline_favorite_border_24));
        isChange = true;
    }
    //#endregion

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case PICK_FOLDER_REQUEST_CODE:
                    if(intent != null){
                        String folderName = intent.getStringExtra(MediaFile.FILE_FOLDER_NAME);
                        isChange = true;

                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("CHANGE", isChange);
        setResult(Activity.RESULT_OK, returnIntent);
        super.onBackPressed();
    }
}