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
import java.util.function.Consumer;


public class Observer {
    private static ArrayList<MediaFile> currentMediaFiles;

    public static class ObserverCode{
        public static final byte TRIGGER_ADAPTER_CHANGE = 0;
        public static final byte TRIGGER_GLIDE_UPDATE = 1;
        public static final byte TRIGGER_OPEN_VIDEO = 2;
        public static final byte TRIGGER_ADAPTER_ALBUM_CHANGE = 3;
    }
    private static final HashMap<Byte, ArrayList<IAction>> localEventListener = new HashMap<>();
    private static final HashMap<Byte, ArrayList<Consumer>> localParamEventListener = new HashMap<>();

    /**
     * Add function to array event action.
     */
    public static void AddEventListener(Byte eventCode, IAction action){
        if(!localEventListener.containsKey(eventCode)){
            localEventListener.put(eventCode, new ArrayList<>());
        }
        Objects.requireNonNull(localEventListener.get(eventCode)).add(action);
    }

    /**
     * Replace previous function by current function (only one function exist in this event)
     */
    public static void AssignEventListener(Byte eventCode, IAction action){
        if(!localEventListener.containsKey(eventCode)){
            localEventListener.put(eventCode, new ArrayList<>());
            localEventListener.get(eventCode).add(null);
        }
        localEventListener.get(eventCode).set(0, action);
    }

    public static void AddEventListener(Byte eventCode, Consumer consumer){
        if(!localParamEventListener.containsKey(eventCode)){
            localParamEventListener.put(eventCode, new ArrayList<>());
        }
        Objects.requireNonNull(localParamEventListener.get(eventCode)).add(consumer);
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static <T> void Invoke(Byte eventCode, T object){
        if(!localParamEventListener.containsKey(eventCode))
            return;
        Objects.requireNonNull(localParamEventListener.get(eventCode)).forEach(consumer -> consumer.accept(object));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void InvokeOnce(Byte eventCode){
        if(!localEventListener.containsKey(eventCode))
            return;
        Objects.requireNonNull(localEventListener.get(eventCode)).forEach(IAction::invoke);
        localEventListener.get(eventCode).clear();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static <T> void InvokeOnce(Byte eventCode, T object){
        if(!localParamEventListener.containsKey(eventCode))
            return;
        Objects.requireNonNull(localParamEventListener.get(eventCode)).forEach(consumer -> consumer.accept(object));
        localParamEventListener.get(eventCode).clear();
    }


    public static void SubscribeCurrentMediaFiles(ArrayList<MediaFile> mediaFiles){
        currentMediaFiles = mediaFiles;
    }

    public static ArrayList<MediaFile> GetCurrentMediaFiles() { return currentMediaFiles == null? new ArrayList<>() : currentMediaFiles; }


}
