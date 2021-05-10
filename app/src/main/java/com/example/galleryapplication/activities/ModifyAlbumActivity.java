package com.example.galleryapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.galleryapplication.R;
import com.example.galleryapplication.classes.Constants;
import com.example.galleryapplication.classes.DataHandler;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ModifyAlbumActivity extends AppCompatActivity {

    private Menu optionsMenuActionBar;

    private TextView selectedTextView;
    private TextInputLayout albumInputLayout;
    private TextInputEditText albumNameEditText;

    private Bundle bundle = null;
    private Intent photoSelectIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_album);

        init();
    }

    private void init() {
        Toolbar toolbar = findViewById(R.id.modifyAlbumToolbar);
        setSupportActionBar(toolbar);

        ActionBar mainActionBar = getSupportActionBar();
        assert mainActionBar != null;
        mainActionBar.setDisplayShowTitleEnabled(false);
        mainActionBar.setHomeAsUpIndicator(R.drawable.ic_back_to_previous);
        mainActionBar.setDisplayHomeAsUpEnabled(true);

        TextView titleTextView = findViewById(R.id.modifyAlbumTitle);
        titleTextView.setText(R.string.modify_album_1);

        if (getIntent().getExtras() != null) {
            bundle = getIntent().getExtras();
        }

        albumInputLayout = findViewById(R.id.albumModifyNameInputLayout);
        albumNameEditText = findViewById(R.id.albumModifyNameEditText);

        if (bundle != null) {
            albumNameEditText.setText(bundle.getString("ALBUM_NAME"));
        }

        Button modifyButton = findViewById(R.id.buttonModifyPhotosToAlbum);
        modifyButton.setText(R.string.modify_photos_1);

        albumNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    albumInputLayout.setError(getString(R.string.album_name_create_error_1));
                } else {
                    albumInputLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        selectedTextView = findViewById(R.id.numberModifyPhotosToAlbum);

        String displayText = "0" + getString(R.string.number_photo_selected_add_album_1);
        selectedTextView.setText(displayText);

        if (bundle != null) {
            displayText =
                    bundle.getStringArrayList("SELECTED_MEDIA").size()
                            + getString(R.string.number_photo_selected_add_album_1);

            selectedTextView.setText(displayText);
        }
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

        String albumName = albumNameEditText.getText().toString();

        if (albumName.length() == 0) {
            albumInputLayout.setError(getString(R.string.album_name_create_error_1));
        } else if (!(bundle != null
                && bundle.getString("ALBUM_NAME").equals(albumName))
                && (DataHandler.GetListAlbumName() != null
                && DataHandler.GetListAlbumName().contains(albumName))
        ) {
            albumInputLayout.setError(getString(R.string.album_name_duplicate_error_warning_1));
        } else {
            if (photoSelectIntent != null
                    && photoSelectIntent.getExtras()
                    .getStringArrayList("SELECTED_MEDIA").size() == 0) {
                Toast.makeText(
                        this,
                        getString(R.string.album_image_empty_error_warning_1),
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            backToPrevious(RESULT_OK);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.RequestCode.PHOTO_SELECT_REQUEST_CODE) {

                photoSelectIntent = data;

                if (photoSelectIntent != null && photoSelectIntent.getExtras() != null) {
                    // TODO: PutExtras to TextView
                    String displayText =
                            photoSelectIntent.getExtras()
                                    .getStringArrayList("SELECTED_MEDIA").size()
                            + getString(R.string.number_photo_selected_add_album_1);
                    selectedTextView.setText(displayText);
                }
            }
        }
    }

    public void selectPhotos (View view) {
        Intent intent =
                new Intent(ModifyAlbumActivity.this, PhotoSelectActivity.class);

        if (photoSelectIntent != null && photoSelectIntent.getExtras() != null) {
            // TODO: PutExtras to new Intent
            intent.putStringArrayListExtra(
                    "SELECTED_MEDIA",
                    photoSelectIntent.getExtras()
                            .getStringArrayList("SELECTED_MEDIA")
            );
        }
        else {
            intent.putStringArrayListExtra(
                    "SELECTED_MEDIA",
                    bundle.getStringArrayList("SELECTED_MEDIA")
            );
        }

        startActivityForResult(intent, Constants.RequestCode.PHOTO_SELECT_REQUEST_CODE);
    }

    private void backToPrevious(int result) {
        Intent intent = new Intent();

        if (result == RESULT_OK) {
            intent.putExtra(
                    "ALBUM_NAME",
                    albumNameEditText.getText().toString()
            );

            intent.putStringArrayListExtra(
                    "SELECTED_MEDIA",
                    (photoSelectIntent == null) ?
                            bundle.getStringArrayList("SELECTED_MEDIA") :
                            photoSelectIntent.getExtras()
                                    .getStringArrayList("SELECTED_MEDIA")
            );
        }

        setResult(result, intent);

        finish();
        super.onBackPressed();
    }
}
