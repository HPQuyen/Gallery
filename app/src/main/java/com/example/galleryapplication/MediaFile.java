package com.example.galleryapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MediaFile {
    public static final String FILE_ID = "file_id";
    public static final String FILE_PATH = "file_path";
    public static final String FILE_ALBUM_NAME = "file_album_name";
    public static final String FILE_DATE = "file_date";
    public static final String FILE_RESOLUTION = "file_resolution";
    public static final String FILE_SIZE = "file_size";

    public static final int THUMBNAIL_SIZE_STANDARD = 256;
    public static final int THUMBNAIL_SIZE_SMALL = 128;

    public int mediaType;
    public String fileUrl;

    public MediaFile(int mediaType, String fileUrl){
        this.mediaType = mediaType;
        this.fileUrl = fileUrl;
    }

    public static String MillisecondToDateString(long millis){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        return formatter.format(new Date(millis));
    }
    public static String ByteToMegaByte(long b){
        return Long.valueOf(b/1048576).toString();
    }

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
