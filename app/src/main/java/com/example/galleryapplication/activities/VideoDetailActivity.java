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
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.galleryapplication.adapters.SlideshowAdapter;
import com.example.galleryapplication.classes.DataHandler;
import com.example.galleryapplication.classes.MediaFile;
import com.example.galleryapplication.R;
import com.example.galleryapplication.classes.Observer;
import com.example.galleryapplication.enumerators.VIEW_DETAIL_MODE;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class VideoDetailActivity extends AppCompatActivity {

    //#region Fiedls

    //#region Layout Fields
    private VideoView videoView;
    //#endregion
    private static final String PLAYBACK_TIME = "play_time";

    private int currentPosition = 0;
    private String videoFilePath;


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
        // Set up Video View
        videoView = findViewById(R.id.video_view_detail);
        MediaController videoController = new MediaController(this);
        videoController.setAnchorView(videoView);
        videoView.setMediaController(videoController);
        videoView.setOnCompletionListener(mediaPlayer -> {
            Toast.makeText(VideoDetailActivity.this, "Playback completed", Toast.LENGTH_SHORT).show();
            videoView.seekTo(1);
        });

        // Load data
        Intent intent = getIntent();
        if(intent != null){
            videoFilePath = intent.getStringExtra(MediaFile.FILE_PATH);
        }

    }
    @Override
    protected void onStart() {
        super.onStart();
        InitializePlayer();
    }

    private void InitializePlayer(){
        videoView.setVideoPath(videoFilePath);
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}