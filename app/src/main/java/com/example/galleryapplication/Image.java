package com.example.galleryapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;

import java.io.File;

public class Image extends MediaFile{

    public static Bitmap GetBitMap(String imagePath){
        if(imagePath != null && !imagePath.isEmpty()){
            File f = new File(imagePath);
            return BitmapFactory.decodeFile(f.getPath());
        }
        return null;
    }

    public static Bitmap GetThumbnail(String imagePath, int thumbSize){
        if(imagePath != null && !imagePath.isEmpty()){
            File f = new File(imagePath);
            return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(f.getPath()), thumbSize, thumbSize);
        }
        return null;
    }
}
