package com.example.galleryapplication.classes;

import android.app.Application;

import com.google.gson.Gson;

public class App extends Application {

    private static App appInstance;
    private Gson gson;

    public static App self() {
        return appInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;
        gson = new Gson();
    }

    public Gson getGSon() {
        return gson;
    }

}
