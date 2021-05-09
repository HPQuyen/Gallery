package com.example.galleryapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galleryapplication.R;
import com.example.galleryapplication.adapters.PhotoSelectAdapter;
import com.example.galleryapplication.classes.DataHandler;
import com.example.galleryapplication.classes.MediaFile;

import java.util.ArrayList;
import java.util.Arrays;

public class PhotoSelectActivity extends AppCompatActivity {

    private Menu optionsMenuActionBar;

    private PhotoSelectAdapter photoSelectAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_select);

        init();
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.selectPhotosToolbar);
        setSupportActionBar(toolbar);

        ActionBar mainActionBar = getSupportActionBar();
        assert mainActionBar != null;
        mainActionBar.setDisplayShowTitleEnabled(false);
        mainActionBar.setHomeAsUpIndicator(R.drawable.ic_back_to_previous);
        mainActionBar.setDisplayHomeAsUpEnabled(true);

        ArrayList<MediaFile> mediaEntries = DataHandler.GetListMediaFiles();

        // TODO: GetExtras
        ArrayList<String> selectedMediaFiles = new ArrayList<>();

        if (getIntent().getExtras() != null) {
            selectedMediaFiles =
                    getIntent()
                            .getExtras()
                            .getStringArrayList("SELECTED_MEDIA");
        }

        photoSelectAdapter =
                new PhotoSelectAdapter(
                        this,
                        mediaEntries,
                        selectedMediaFiles
                );

        RecyclerView recyclerView = findViewById(R.id.selectPhotosRecyclerView);

        recyclerView.setAdapter(photoSelectAdapter);
        recyclerView.setLayoutManager(
                new GridLayoutManager(this, 4)
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (optionsMenuActionBar == null) optionsMenuActionBar = menu;

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_save_selection_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Respond to the action bar's Up/Home button
        if (item.getItemId() == android.R.id.home) {
            backToPrevious(RESULT_CANCELED);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        backToPrevious(RESULT_CANCELED);
    }

    public void saveSelections(MenuItem item) {
        backToPrevious(RESULT_OK);
    }

    private void backToPrevious(int result) {
        Intent intent = new Intent();

        if (result == RESULT_OK) {
            // TODO: PutExtras
            intent.putStringArrayListExtra(
                    "SELECTED_MEDIA",
                    photoSelectAdapter.getSelectedPhotos()
            );
        }

        setResult(result, intent);

        finish();
        super.onBackPressed();
    }
}