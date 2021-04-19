package com.example.galleryapplication;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

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


    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static boolean SaveImage(@NonNull final Context context, @NonNull final Bitmap bitmap, @NonNull final String bucketName){
        final String SALT = context.getString(R.string.app_name);
        final String relativeLocation = Environment.DIRECTORY_DCIM + File.separator + bucketName;
        long timeMillis = System.currentTimeMillis();
        try {
            OutputStream fos;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver resolver = context.getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, SALT + timeMillis + ".jpg");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation);
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
            } else {
                String imagesDir = Environment.getExternalStoragePublicDirectory(relativeLocation).toString();
                File directory = new File(imagesDir);
                if(!directory.exists())
                    directory.mkdir();
                File image = new File(imagesDir, SALT + timeMillis + ".jpg");
                fos = new FileOutputStream(image);

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(image);

                mediaScanIntent.setData(contentUri);
                context.sendBroadcast(mediaScanIntent);
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            Objects.requireNonNull(fos).close();

        }catch (Exception ignore){
            ignore.printStackTrace();
            return false;
        }
        return true;
    }

}
