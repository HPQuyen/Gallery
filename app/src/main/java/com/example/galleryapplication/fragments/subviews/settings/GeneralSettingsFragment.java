package com.example.galleryapplication.fragments.subviews.settings;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.example.galleryapplication.R;
import com.example.galleryapplication.classes.Constants;
import com.example.galleryapplication.utils.LanguageHandler;
import com.example.galleryapplication.utils.SharedPrefs;

public class GeneralSettingsFragment extends PreferenceFragmentCompat
implements SharedPreferences.OnSharedPreferenceChangeListener {

    SwitchPreferenceCompat darkThemeSwitchPreference;
    ListPreference languageListPreference;
    Preference incognitoFolderPreference;

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager()
                .getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager()
                .getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        darkThemeSwitchPreference = findPreference("theme_dark");
        languageListPreference = findPreference("language");
        incognitoFolderPreference = findPreference("incognito");

        if (darkThemeSwitchPreference != null) {
            darkThemeSwitchPreference.setVisible(true);
        }

        if (languageListPreference != null) {
            languageListPreference.setVisible(true);

            languageListPreference.setOnPreferenceChangeListener((preference, newValue) -> {

                preference.setSummaryProvider(ListPreference.SimpleSummaryProvider.getInstance());

                return true;
            });
        }

        if(incognitoFolderPreference != null){
            incognitoFolderPreference.setOnPreferenceClickListener(preference -> {
                startActivityForResult(
                        preference.getIntent(), Constants.RequestCode.INCOGNITO_FOLDER_REQUEST_CODE
                );
                return true;
            });
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(SharedPrefs.DARKTHEME)) {
            Boolean isInDarkMode =
                    SharedPrefs.getInstance().get(SharedPrefs.DARKTHEME, Boolean.class);

            if (isInDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
        else if (key.equals(SharedPrefs.LANGUAGE)) {
            String languageCode =
                    SharedPrefs.getInstance().get(SharedPrefs.LANGUAGE, String.class);

            LanguageHandler.changeLanguage(requireActivity(), languageCode);
        }
        requireActivity().recreate();
    }
}