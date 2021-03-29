package com.example.galleryapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;

import java.io.File;

public class Video extends MediaFile{


    public static Bitmap GetBitMap(String videoPath){
        if(videoPath != null && !videoPath.isEmpty()){
            File f = new File(videoPath);
            return BitmapFactory.decodeFile(f.getPath());
        }
        return null;
    }

    public static Bitmap GetThumbnail(String videoPath, int thumbSize){
        if(videoPath != null && !videoPath.isEmpty()){
            File f = new File(videoPath);
            return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(f.getPath()), thumbSize, thumbSize);
        }
        return null;
    }
}
