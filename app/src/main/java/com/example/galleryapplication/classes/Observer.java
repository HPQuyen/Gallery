package com.example.galleryapplication.classes;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import com.example.galleryapplication.interfaces.IAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class Observer {
    public static class ObserverCode{
        public static final byte TRIGGER_ADAPTER_CHANGE = 0;
        public static final byte TRIGGER_GLIDE_UPDATE = 1;
    }
    private static final HashMap<Byte, ArrayList<IAction>> localEventListener = new HashMap<>();
    public static void AddEventListener(Byte eventCode, IAction action){
        if(!localEventListener.containsKey(eventCode)){
            localEventListener.put(eventCode, new ArrayList<>());
        }
        Objects.requireNonNull(localEventListener.get(eventCode)).add(action);
    }

    public static void RemoveEvent(Byte eventCode){
        if(!localEventListener.containsKey(eventCode))
            return;
        Objects.requireNonNull(localEventListener.remove(eventCode));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void Invoke(Byte eventCode){
        if(!localEventListener.containsKey(eventCode))
            return;
        Objects.requireNonNull(localEventListener.get(eventCode)).forEach(IAction::invoke);
    }
}
