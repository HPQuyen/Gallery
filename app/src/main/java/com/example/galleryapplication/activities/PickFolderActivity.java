package com.example.galleryapplication.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;

import com.example.galleryapplication.R;
import com.example.galleryapplication.adapters.FolderAdapter;
import com.example.galleryapplication.classes.DataHandler;
import com.example.galleryapplication.classes.MediaFile;

public class PickFolderActivity extends AppCompatActivity {

    //#region Fields
    private ListView listView = null;
    private FolderAdapter albumAdapter = null;
    //#endregion

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_folder);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        listView = findViewById(R.id.list_album_view);

        // Create an adapter class
        albumAdapter = new FolderAdapter(this, DataHandler.GetListFolderName());
        listView.setAdapter(albumAdapter);
    }

    private int RESULT_CODE;
    //#region On Click Event
    public void OnClickCancel(View view){
        RESULT_CODE = Activity.RESULT_CANCELED;
        onBackPressed();
    }
    public void OnClickOk(View view){
        RESULT_CODE = Activity.RESULT_OK;
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        if(RESULT_CODE == Activity.RESULT_CANCELED || albumAdapter.GetAlbumPicked() == null){
            setResult(Activity.RESULT_CANCELED, null);
        }else{
            returnIntent.putExtra(MediaFile.FILE_FOLDER_NAME, albumAdapter.GetAlbumPicked());
            setResult(Activity.RESULT_OK, returnIntent);
        }
        super.onBackPressed();
    }

    //#endregion
}