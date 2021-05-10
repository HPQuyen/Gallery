package com.example.galleryapplication.activities;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.galleryapplication.R;
import com.example.galleryapplication.adapters.MediaFileAdapter;
import com.example.galleryapplication.classes.Constants;
import com.example.galleryapplication.classes.DataHandler;
import com.example.galleryapplication.classes.MediaFile;
import com.example.galleryapplication.enumerators.VIEW_DETAIL_MODE;
import com.example.galleryapplication.enumerators._LAYOUT;
import com.example.galleryapplication.utils.SharedPrefs;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.jacknkiarie.signinui.models.SignInUI;

import java.util.List;

@SuppressLint("IntentReset")
public class IncognitoFolderActivity extends AppCompatActivity {

    private ConstraintLayout newPinCodeForm;
    private Intent photoSelectIntent;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incognito_folder);
        Init();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void Init(){
        newPinCodeForm = findViewById(R.id.new_pin_code_form);

        String pinCode = SharedPrefs.getInstance().get(Constants.Value.PIN_CODE_PREFS_KEY, String.class);
        if(pinCode.isEmpty()){
            TextInputLayout pinCodeLayout = findViewById(R.id.pin_code_et_layout);
            TextInputEditText pinCodeEditText = findViewById(R.id.pin_code_et);
            TextInputLayout confirmPinCodeLayout = findViewById(R.id.confirm_pin_code_et_layout);
            TextInputEditText confirmPinCodeEditText = findViewById(R.id.confirm_pin_code_et);
            Button nextButton = findViewById(R.id.next_btn);

            newPinCodeForm.setVisibility(View.VISIBLE);
            pinCodeEditText.setOnFocusChangeListener((v, hasFocus) -> {
                if(hasFocus){
                    pinCodeLayout.setError(null);
                }else {
                    if(pinCodeEditText.getText().length() != 6){
                        pinCodeLayout.setError(getString(R.string.err_msg_pin_code_lenth));
                    }
                }
            });
            confirmPinCodeEditText.setOnFocusChangeListener((v, hasFocus) -> {
                if(hasFocus){
                    confirmPinCodeLayout.setError(null);
                }else {
                    if(confirmPinCodeEditText.getText().length() != 6){
                        confirmPinCodeLayout.setError(getString(R.string.err_msg_pin_code_lenth));
                    }
                }
            });

            nextButton.setOnClickListener(v -> {
                if(pinCodeEditText.getText().length() != 6 || confirmPinCodeEditText.getText().length() != 6){
                    return;
                }
                if(!confirmPinCodeEditText.getText().toString().equals(pinCodeEditText.getText().toString())){
                    confirmPinCodeLayout.setError(getString(R.string.err_msg_pin_code_not_match));
                    return;
                }
                SharedPrefs.getInstance().put(Constants.Value.PIN_CODE_PREFS_KEY, pinCodeEditText.getText().toString());
                SetUpSignIn();
            });
            return;
        }

        SetUpSignIn();

    }

    private void SetUpSignIn(){
        newPinCodeForm.setVisibility(View.GONE);
        new SignInUI.Builder(this)
                .setSignInType(SignInUI.PIN_FORM)
                .setFingerprintSignInEnabled(true)
                .setEmailSignInEnabled(false)
                .setPinLength(6)
                .build();


    }

    private RecyclerView recyclerView;
    private MediaFileAdapter mediaFileAdapter;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void SetUpUi(){
        ConstraintLayout mainConstraintLayout = findViewById(R.id.main_constraint_layout);
        mainConstraintLayout.setVisibility(View.VISIBLE);

        ExtendedFloatingActionButton addFab = findViewById(R.id.add_fab);
        addFab.setOnClickListener(v -> {
            Intent intent =
                    new Intent(this, PhotoSelectActivity.class);

            if (photoSelectIntent != null && photoSelectIntent.getExtras() != null) {
                // TODO: PutExtras to new Intent
                intent.putStringArrayListExtra(
                        "SELECTED_MEDIA",
                        photoSelectIntent.getExtras()
                                .getStringArrayList("SELECTED_MEDIA")
                );
            }

            startActivityForResult(intent, Constants.RequestCode.PHOTO_SELECT_REQUEST_CODE);
        });

        recyclerView = findViewById(R.id.recycler_view);

        mediaFileAdapter =
                new MediaFileAdapter(
                        this,
                        DataHandler.GetListIncognitoFile(),
                        _LAYOUT._GRID,
                        VIEW_DETAIL_MODE.INCOGNITO
                );


        this.recyclerView.setAdapter(mediaFileAdapter);
        this.recyclerView.setLayoutManager(
                new GridLayoutManager(this, 4)
        );

        String[] files = this.fileList();
        for (String file : files) {
            Log.d("Nothing", file);
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == SignInUI.RESULT_OK || resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case SignInUI.REQUEST_CODE:
                    if(data != null){
                        if(data.getStringExtra(SignInUI.PARAM_SIGN_IN_TYPE).equals(SignInUI.PIN_FORM)){
                            String pinCode = SharedPrefs.getInstance().get(Constants.Value.PIN_CODE_PREFS_KEY, String.class);
                            String pinCodeInput = data.getStringExtra(SignInUI.PARAM_PIN);
                            if(!pinCode.equals(pinCodeInput)){
                                SetUpSignIn();
                                return;
                            }
                        }
                        // Authentication success, load data here
                        SetUpUi();
                    }
                    break;
                case Constants.RequestCode.PHOTO_SELECT_REQUEST_CODE:
                    photoSelectIntent = data;

                    if (photoSelectIntent != null && photoSelectIntent.getExtras() != null) {
                        // TODO: PutExtras to TextView
                        List<String> mediaFileId = photoSelectIntent.getExtras()
                                .getStringArrayList("SELECTED_MEDIA");
                        if(mediaFileId == null)
                            return;
                        new Thread(() -> {
                            for (int i = 0; i < mediaFileId.size(); i++){

                                MediaFile mediaFile = DataHandler.GetMediaFileById(this, mediaFileId.get(i));
                                MediaFile.HideMediaFile(this, mediaFile, (isSuccess)->{
                                    runOnUiThread(()->{
                                        if(isSuccess){
                                            mediaFileAdapter.notifyDataSetChanged();
                                            Snackbar.make(findViewById(R.id.constraintLayout), R.string.err_msg_hide_file_success, Snackbar.LENGTH_SHORT);
                                        }else{
                                            Snackbar.make(findViewById(R.id.constraintLayout), R.string.err_msg_hide_file_failed, Snackbar.LENGTH_SHORT);
                                        }
                                    });
                                });
                            }
                        }).start();
                    }
                    break;
                case Constants.RequestCode.VIEW_DETAIL_REQUEST_CODE:
                    if(data != null){
                        if(data.getBooleanExtra("CHANGE", false))
                        {
                            DataHandler.UpdateMediaFiles(this);
                            mediaFileAdapter.notifyDataSetChanged();
                        }
                    }
                    break;
                default:
                    break;
            }
        }else if(resultCode == SignInUI.RESULT_CANCEL || resultCode == RESULT_CANCELED && requestCode == SignInUI.REQUEST_CODE){
            finish();
        }
    }


    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("CHANGE", true);
        setResult(Activity.RESULT_OK, returnIntent);
        super.onBackPressed();
    }
}