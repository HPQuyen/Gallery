package com.example.galleryapplication.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.galleryapplication.R;
import com.example.galleryapplication.fragments.subviews.settings.GeneralSettingsFragment;
import com.example.galleryapplication.utils.SharedPrefs;

public class SettingsActivity extends AppCompatActivity {

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Layout setup
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.main_Toolbar);
        setSupportActionBar(toolbar);

        ActionBar mainActionBar = getSupportActionBar();
        assert mainActionBar != null;
        mainActionBar.setDisplayShowTitleEnabled(false);

        boolean isInDarkMode = SharedPrefs.getInstance().get(SharedPrefs.DARKTHEME, Boolean.class);

        if (!isInDarkMode)
            mainActionBar.setHomeAsUpIndicator(R.drawable.ic_back_to_previous);
        else
            mainActionBar.setHomeAsUpIndicator(R.drawable.ic_back_to_previous_darkmode);

        mainActionBar.setDisplayHomeAsUpEnabled(true);

        Fragment settingsFragment = new GeneralSettingsFragment();

        setCurrentFragment(settingsFragment);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Respond to the action bar's Up/Home button
        if (item.getItemId() == android.R.id.home) {
            setResult(Activity.RESULT_OK);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setCurrentFragment (Fragment fragment) {
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.fragment_Settings_FrameLayout, fragment);
        fragTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK);
        finish();
        super.onBackPressed();
    }
}