package com.example.galleryapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {

    private ActionBar mainActionbar;

    private Fragment settingsFragment;
    private Fragment settingsLanguageFragment;
    private Fragment settingsThemeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mainActionbar = getSupportActionBar();

        mainActionbar.setDisplayHomeAsUpEnabled(true);

        settingsFragment = new GeneralSettingsFragment();
        settingsLanguageFragment = new LanguageSettingsFragment();
        settingsThemeFragment = new ThemeSettingsFragment();

        setCurrentFragment(settingsFragment);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setCurrentFragment (Fragment fragment) {
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.fragment_FrameLayout, fragment);
        fragTransaction.commit();
    }

    public void settingsLanguage(View view) {
        mainActionbar.setTitle("Language");

        setCurrentFragment(settingsLanguageFragment);
    }

    public void settingsTheme(View view) {
        mainActionbar.setTitle("Theme");

        setCurrentFragment(settingsThemeFragment);
    }
}