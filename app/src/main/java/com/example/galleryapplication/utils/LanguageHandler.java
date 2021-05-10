package com.example.galleryapplication.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import com.example.galleryapplication.R;
import com.example.galleryapplication.classes.App;
import com.example.galleryapplication.classes.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class LanguageHandler {

    private static String currentLanguage = "";

    public static String getCurrentLanguage() {
        if (currentLanguage.equals("")) {
            currentLanguage = initCurrentLanguage();
        }
        return currentLanguage;
    }

    /**
     * Return language list from string.xml
     */
    public static List<String> getLanguageList() {
        List<String> languageList = new ArrayList<>();

        List<String> languageCodes =
                Arrays.asList(App.self().getResources().getStringArray(R.array.language_codes));

        for (int i = 0, size = languageCodes.size(); i < size; i++) {
            languageList.add(languageCodes.get(i));
        }

        return languageList;
    }

    /**
     * Check language exist in SharedPrefs, if not exist then default language is English
     */
    private static String initCurrentLanguage() {
        currentLanguage =
                SharedPrefs.getInstance().get(SharedPrefs.LANGUAGE, String.class);

        if (!currentLanguage.equals("")) {
            return currentLanguage;
        }

        currentLanguage = getLanguageList().get(Constants.Value.DEFAULT_LANGUAGE_ID);

        SharedPrefs.getInstance().put(SharedPrefs.LANGUAGE, currentLanguage);
        return currentLanguage;
    }

    /**
     * Load current locale and change language
     */
    public static void loadLocale(Context context) {
        changeLanguage(context, initCurrentLanguage());
    }

    /**
     * Change app language
     */
    public static void changeLanguage(Context context, String languageCode) {
        currentLanguage = languageCode;

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }

        /* if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            context.createConfigurationContext(configuration);
        } else {
            resources.updateConfiguration(configuration, displayMetrics);
        } */

        resources.updateConfiguration(configuration, displayMetrics);
    }
}
