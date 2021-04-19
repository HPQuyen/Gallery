package com.example.galleryapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoDetailActivity extends AppCompatActivity {

    private VideoView videoView;
    private int currentPosition = 0;
    private static final String PLAYBACK_TIME = "play_time";

    private String fileId;
    private String filePath;
    private String fileAlbumName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TurnOffTitle();
        setContentView(R.layout.activity_video_detail);

        if(savedInstanceState != null){
            currentPosition = savedInstanceState.getInt(PLAYBACK_TIME);
            Log.d("Nothing","load instance");
        }
        Intent intent = getIntent();
        if(intent != null){
            fileId = intent.getStringExtra(MediaFile.FILE_ID);
            filePath = intent.getStringExtra(MediaFile.FILE_PATH);
            fileAlbumName = intent.getStringExtra(MediaFile.FILE_ALBUM_NAME);
            Log.d("Nothing", fileId);
            Log.d("Nothing", filePath);
            Log.d("Nothing", fileAlbumName);
        }

        videoView = findViewById(R.id.video_view_detail);

        MediaController videoController = new MediaController(this);

        videoController.setAnchorView(videoView);
        videoView.setMediaController(videoController);
        videoView.setOnCompletionListener(mediaPlayer -> {
            Toast.makeText(VideoDetailActivity.this, "Playback completed", Toast.LENGTH_SHORT).show();
            videoView.seekTo(1);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        InitializePlayer();
    }

    private void InitializePlayer(){
        videoView.setVideoPath(filePath);
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

    private void TurnOffTitle(){
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
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
}