package com.example.galleryapplication.classes;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.example.galleryapplication.BuildConfig;
import com.example.galleryapplication.R;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class MediaFile {
    public static final String FILE_ID = "file_id";
    public static final String FILE_PATH = "file_path";
    public static final String FILE_ALBUM_NAME = "file_album_name";
    public static final String FILE_DATE = "file_date";
    public static final String FILE_RESOLUTION = "file_resolution";
    public static final String FILE_SIZE = "file_size";
    public static final String FILE_FAVOURITE = "file_favourite";
    public static final String FILE_MEDIA_TYPE = "file_media_type";

    public static final int THUMBNAIL_SIZE_STANDARD = 256;
    public static final int THUMBNAIL_SIZE_SMALL = 128;

    public String id;
    public int mediaType;
    public String fileUrl;
    public String albumName;
    public String datetime;
    public String fileSize;
    public String resolution;
    public boolean isFavourite;

    public MediaFile(String id, int mediaType, String fileUrl, String datetime, String fileSize, String resolution, String albumName, boolean isFavourite){
        this.id = id;
        this.mediaType = mediaType;
        this.fileUrl = fileUrl;
        this.datetime = datetime;
        this.fileSize = fileSize;
        this.resolution = resolution;
        this.albumName = albumName;
        this.isFavourite = isFavourite;
    }

    public static String SecondsToDateString(long seconds){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(new Date(seconds * 1000L));
    }
    public static String SecondsToDatetimeString(long seconds){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return formatter.format(new Date(seconds * 1000L));
    }
    public static String MillisecondToDatetimeString(long millis){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return formatter.format(new Date(millis));
    }
    public static String ByteToMegaByte(long b){
        return Long.valueOf(b/1048576).toString();
    }
    public static String FormatFileSize(long b) {
        String suffix = null;

        if (b >= 1024) {
            suffix = " KB";
            b /= 1024;
            if (b >= 1024) {
                suffix = " MB";
                b /= 1024;
            }
        }
        StringBuilder resultBuffer = new StringBuilder(Long.toString(b));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }
        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
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

    public static void UpdateImage(String imageFilePath, Bitmap bitmap){
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(new File(imageFilePath));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static boolean DeleteMediaFile(@NonNull final Context context, String filePath) {
        // Set up the projection (we only need the ID)
        String[] projection = {MediaStore.Images.Media._ID};
        boolean isSuccess;
        // Match on the file path
        String selection = MediaStore.Images.Media.DATA + " = ?";
        String[] selectionArgs = new String[]{filePath};


        // Query for the ID of the media matching the file path
        Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = context.getContentResolver();
        Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
        if (c.moveToFirst()) {
            // We found the ID. Deleting the item via the content provider will also remove the file
            long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
            Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            contentResolver.delete(deleteUri, null, null);
            isSuccess = true;
        } else {
            // File not found in media store DB
            Log.e("Nothing", "File not found");
            isSuccess = false;
        }
        c.close();
        return isSuccess;
    }

    @SuppressLint("SetWorldReadable")
    public static void ShareMediaFile(@NonNull final Context context,MediaFile mediaFile){
        try {
            // Create an image file name
            File file = null;
            if(mediaFile.mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE){
                file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), File.separator +"share.jpg");
                FileOutputStream fOut = new FileOutputStream(file);
                Bitmap bitmap = GetBitMap(mediaFile.fileUrl);
                Log.d("Nothing", "" + bitmap);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.flush();
                fOut.close();
            }else{

            }
            file.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri photoURI = FileProvider.getUriForFile(context.getApplicationContext(), "com.example.galleryapplication.fileprovider", file);

            intent.putExtra(Intent.EXTRA_STREAM, photoURI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/jpg");
            context.startActivity(Intent.createChooser(intent, "Share image via"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Uri GetUriContentFromImageFile(@NonNull Context context, MediaFile mediaFile, String fileName) throws IOException {
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), File.separator + fileName);
        FileOutputStream fOut = new FileOutputStream(file);
        Bitmap bitmap = GetBitMap(mediaFile.fileUrl);
        Log.d("Nothing", "" + bitmap);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        fOut.flush();
        fOut.close();
        file.setReadable(true, false);
        return FileProvider.getUriForFile(context, "com.example.galleryapplication.fileprovider", file);
    }
}
