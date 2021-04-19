package com.example.galleryapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

public class CameraActivity extends AppCompatActivity {

    class Item{
        public final String text;
        public final int icon;
        public Item(String text, Integer icon) {
            this.text = text;
            this.icon = icon;
        }
    }

    private static final int PHOTO_CAMERA_REQUEST = 1888;
    private static final int VIDEO_CAMERA_REQUEST = 1999;
    private static final int PHOTO_REQUEST_PERMISSION_CODE = 100;
    private static final int VIDEO_REQUEST_PERMISSION_CODE = 200;

    private ImageView imageView;
    private VideoView videoView;
    private ConstraintLayout photo_camera_layout, video_camera_layout;
    private MaterialAlertDialogBuilder alertDialogBuilder;
    private boolean dialogPopup = false;


    private String currentPhotoPath;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_camera);
        Init();
    }

//    private void InitializePlayer(Uri videoUri){
//        videoView.setVideoURI(videoUri);
//        videoView.setOnPreparedListener(mp -> {
//            videoView.seekTo(1);
//        });
//    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void Init(){
        //        photo_camera_layout = findViewById(R.id.photo_camera_layout);
//        imageView = (ImageView)this.findViewById(R.id.image_view_preview);
//        photo_camera_layout.setVisibility(View.GONE);
//
//        video_camera_layout = findViewById(R.id.video_camera_layout);
//        videoView = findViewById(R.id.video_view_preview);
//        MediaController videoController = new MediaController(this);
//        videoController.setAnchorView(videoView);
//        videoView.setMediaController(videoController);
//        videoView.setOnCompletionListener(mediaPlayer -> {
//            videoView.seekTo(1);
//        });
//        video_camera_layout.setVisibility(View.GONE);


        final Item[] items = new Item[] {
                new Item(getString(R.string.tv_photocamera_pick_6), R.drawable.ic_baseline_photo_camera_24),
                new Item(getString(R.string.tv_videocamera_pick_6), R.drawable.ic_baseline_videocam_32),
        };

        int selectedItem = 1;
        alertDialogBuilder = new MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog);
        // Add customization options here
        alertDialogBuilder.setTitle("Choose camera");
        alertDialogBuilder.setIcon(R.drawable.ic_baseline_center_focus_weak_32);
        ListAdapter adapter = new ArrayAdapter<Item>(
                this,
                android.R.layout.select_dialog_item,
                android.R.id.text1,
                items){
            public View getView(int position, View convertView, ViewGroup parent) {
                //Use super class to create the View
                View v = super.getView(position, convertView, parent);
                TextView tv = (TextView)v.findViewById(android.R.id.text1);

                //Put the image on the TextView
                tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0);

                //Add margin between image and text (support various screen densities)
                int dp5 = (int) (5 * getResources().getDisplayMetrics().density + 0.5f);
                tv.setCompoundDrawablePadding(dp5);
                tv.setText(items[position].text);
                return v;
            }
        };

        alertDialogBuilder.setSingleChoiceItems(adapter, selectedItem, (dialog, which) -> {
            final Handler handler = new Handler(Looper.getMainLooper());
            Consumer<String> consumer;
            if(which == 0){
                consumer = s -> {
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, PHOTO_REQUEST_PERMISSION_CODE);
                    }
                    else
                    {
                        try {
                            OpenBackCamera();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                };
            }else {
                consumer = s -> {
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    {
                        requestPermissions(new String[]{ Manifest.permission.CAMERA }, VIDEO_REQUEST_PERMISSION_CODE);
                    }
                    else
                    {
                        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        startActivityForResult(videoIntent, VIDEO_CAMERA_REQUEST);
                    }
                };
            }
            handler.postDelayed(() -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    dialogPopup = false;
                    dialog.dismiss();
                    consumer.accept(null);
                }
            }, 200);
        });
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setNegativeButton("Cancel", (dialog, which) -> {
            onBackPressed();
        });
        alertDialogBuilder.show();
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onStart() {
        super.onStart();
        if(dialogPopup){
            alertDialogBuilder.show();
            Log.d("Nothing", "show");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PHOTO_REQUEST_PERMISSION_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                try {
                    OpenBackCamera();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                dialogPopup = true;
                onStart();
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }else if(requestCode == VIDEO_REQUEST_PERMISSION_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivityForResult(videoIntent, VIDEO_CAMERA_REQUEST);
            }else{
                dialogPopup = true;
                onStart();
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Nothing", "onActivityResult");
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PHOTO_CAMERA_REQUEST) {
                File imgFile = new File(currentPhotoPath);
                if (imgFile.exists()) {
                    Bitmap photo = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    Log.d("Nothing", currentPhotoPath);
                    Log.d("Nothing", String.valueOf(photo.getWidth()));
                    Log.d("Nothing", String.valueOf(photo.getHeight()));
                    if (MediaFile.SaveImage(this, photo, "Camera"))
                        Log.d("Nothing", "Save image success");
                }
            } else if (requestCode == VIDEO_CAMERA_REQUEST) {
                Uri videoUri = data.getData();
//                InitializePlayer(videoUri);
//                video_camera_layout.setVisibility(View.VISIBLE);

            }
        }
        dialogPopup = true;
        Log.d("Nothing", "" + dialogPopup);
    }

    private void OpenBackCamera() throws IOException {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = CreateImageFile();
        if(photoFile != null){
            Uri outputFileUri = FileProvider.getUriForFile(this,
                    "com.example.galleryapplication.fileprovider",
                    photoFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(cameraIntent, PHOTO_CAMERA_REQUEST);
        }
    }

    private File CreateImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "Temporary_capture_image";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File tempImageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = tempImageFile.getAbsolutePath();
        return tempImageFile;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}