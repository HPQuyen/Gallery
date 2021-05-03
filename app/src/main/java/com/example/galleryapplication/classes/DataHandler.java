package com.example.galleryapplication.classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class DataHandler {

    public static final String STORE_FILE_NAME = "store_file_name";
    public static final String FAVOURITE_FILE_KEY = "favourite_file_key";
    public static final int ONE = 1;
    public static final int ALL = Integer.MAX_VALUE;

    private static ArrayList<MediaFile> mediaFileArrayList = new ArrayList<>();
    private static ArrayList<String> albumNameArrayList = new ArrayList<>( Arrays.asList("Favourite") );
    private static ArrayList<String> dateArrayList = new ArrayList<>();
    private static HashSet<String> mediaFileHashSet = new HashSet<>();
    private static ArrayList<String> favouriteIdArrayList = new ArrayList<>( Arrays.asList("1618672930968") );


    // *********************************************************************************
    // **************************  GET DATA IN MOBILE DEVICES  *************************
    // *********************************************************************************

    // Load media files once when create MainActivity
    // In order to get media file call GetListMediaFiles
    @RequiresApi(api = Build.VERSION_CODES.R)
    public static void LoadAllMediaFiles(@NonNull Context context){
        Uri queryUri = MediaStore.Files.getContentUri("external");
        String[] projections = new String[]{
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.BUCKET_ID,
                MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.HEIGHT,
                MediaStore.Files.FileColumns.WIDTH };

        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + " = ?" + " OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + " = ?";
        String []selectionArgs = new String[]{
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) };
        String sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";
        @SuppressLint("Recycle")
        Cursor resultSet = context.getContentResolver().query(queryUri, projections, selection, selectionArgs, sortOrder);
        if(resultSet != null){
            LoadFavouriteMediaFiles(context);
            while (resultSet.moveToNext()){
                String albumName = resultSet.getString(
                        resultSet.getColumnIndex(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME)
                );
                if(!albumNameArrayList.contains(albumName)){
                    albumNameArrayList.add(albumName);
                }
                // Get epochtime in seconds
                long epochTime = resultSet.getLong(
                        resultSet.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED)
                );
                String date = MediaFile.SecondsToDateString(epochTime);
                if(!dateArrayList.contains(date)){
                    dateArrayList.add(date);
                }

                String id = resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns._ID));
                int mediaType = resultSet.getInt(resultSet.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE));
                String fileUrl = resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                long fileSize = resultSet.getLong(resultSet.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                String resolution = "Undefined";
                if(resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.HEIGHT)) != null && resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.WIDTH)) != null){
                    resolution = resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.HEIGHT)) + " x " + resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.WIDTH));
                }

                assert mediaFileArrayList != null;
                mediaFileArrayList.add(new MediaFile(id, mediaType, fileUrl, MediaFile.SecondsToDatetimeString(epochTime), MediaFile.FormatFileSize(fileSize), resolution, albumName, favouriteIdArrayList.contains(id)));
                mediaFileHashSet.add(id);
            }
        }else{
            Toast.makeText(context,"No such file", Toast.LENGTH_LONG).show();
        }
    }

    // Update media file when event change
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void UpdateMediaFiles(@NonNull Context context){
        Uri queryUri = MediaStore.Files.getContentUri("external");
        String[] projections = new String[]{
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.BUCKET_ID,
                MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.HEIGHT,
                MediaStore.Files.FileColumns.WIDTH };

        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + " = ?" + " OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + " = ?";
        String []selectionArgs = new String[]{
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) };
        String sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";
        @SuppressLint("Recycle")
        Cursor resultSet = context.getContentResolver().query(queryUri, projections, selection, selectionArgs, sortOrder);
        if(resultSet != null){
            while (resultSet.moveToNext()){
                String id = resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns._ID));
                if(mediaFileHashSet.contains(id))
                    continue;
                String albumName = resultSet.getString(
                        resultSet.getColumnIndex(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME)
                );
                if(!albumNameArrayList.contains(albumName)){
                    albumNameArrayList.add(albumName);
                }
                // Get epochtime in seconds
                long epochTime = resultSet.getLong(
                        resultSet.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED)
                );
                String date = MediaFile.SecondsToDateString(epochTime);
                if(!dateArrayList.contains(date)){
                    dateArrayList.add(date);
                }

                int mediaType = resultSet.getInt(resultSet.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE));
                String fileUrl = resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                long fileSize = resultSet.getLong(resultSet.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                String resolution = "Undefined";
                if(resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.HEIGHT)) != null && resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.WIDTH)) != null){
                    resolution = resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.HEIGHT)) + " x " + resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.WIDTH));
                }

                assert mediaFileArrayList != null;
                mediaFileArrayList.add(new MediaFile(id, mediaType, fileUrl, MediaFile.SecondsToDatetimeString(epochTime), MediaFile.FormatFileSize(fileSize), resolution, albumName, favouriteIdArrayList.contains(id)));
                mediaFileHashSet.add(id);
                break;
            }
        }else{
            Toast.makeText(context,"No such file", Toast.LENGTH_LONG).show();
        }
    }

    // Get array list media files
    public static ArrayList<MediaFile> GetListMediaFiles(){
        return mediaFileArrayList;
    }

    // Get array list album name
    public static ArrayList<String> GetListAlbumName() { return albumNameArrayList; }

    // Get array list date array list
    public static ArrayList<String> GetListDate() { return dateArrayList; }

    /*
    Get array list media files group by album name.
    <param>
        NUMBER_OF_FILE: ONE or ALL
    </param>
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static ArrayList<MediaFile> GetMediaFilesByAlbum(@NonNull Context context, String albumName, int NUMBER_OF_FILE){
        ArrayList<MediaFile> mediaFileArrayList;
        Uri queryUri = MediaStore.Files.getContentUri("external");
        String[] projections = new String[]{
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.BUCKET_ID,
                MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.HEIGHT,
                MediaStore.Files.FileColumns.WIDTH };

        String selection = null;
        String []selectionArgs = null;

        if(albumName.equals("Favourite")){
            selection = MediaStore.Files.FileColumns.MEDIA_TYPE + " = ?" + " OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + " = ?";
            selectionArgs = new String[]{
                    String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                    String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)};
        }else{
            selection = MediaStore.Files.FileColumns.MEDIA_TYPE + " = ?" + " OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + " = ?" + " AND " + MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME + " = ?";
            selectionArgs = new String[]{
                    String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                    String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
                    albumName };
        }
        String sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";

        @SuppressLint("Recycle")
        Cursor resultSet = context.getContentResolver().query(queryUri, projections, selection, selectionArgs, sortOrder);
        if(resultSet != null){
            mediaFileArrayList = new ArrayList<>();
            while (resultSet.moveToNext()){
                String id = resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns._ID));
                if(albumName.equals("Favourite") && !favouriteIdArrayList.contains(id)){
                    continue;
                }
                // Get epochtime in seconds
                long epochTime = resultSet.getLong(resultSet.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED));
                int mediaType = resultSet.getInt(resultSet.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE));
                String fileUrl = resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                long fileSize = resultSet.getLong(resultSet.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                String resolution = "Undefined";
                if(resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.HEIGHT)) != null && resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.WIDTH)) != null){
                    resolution = resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.HEIGHT)) + " x " + resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.WIDTH));
                }
                mediaFileArrayList.add(new MediaFile(id, mediaType, fileUrl, MediaFile.SecondsToDatetimeString(epochTime), MediaFile.FormatFileSize(fileSize), resolution, albumName, favouriteIdArrayList.contains(id)));
                if(NUMBER_OF_FILE == ONE)
                    break;
            }
            return mediaFileArrayList;
        }
        Toast.makeText(context,"No such file", Toast.LENGTH_LONG).show();
        return null;
    }

    // Get array list media files group by date.
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static ArrayList<MediaFile> GetMediaFilesByDate(@NonNull Context context, String date){
        ArrayList<MediaFile> mediaFileArrayList;
        Uri queryUri = MediaStore.Files.getContentUri("external");
        String[] projections = new String[]{
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.BUCKET_ID,
                MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.HEIGHT,
                MediaStore.Files.FileColumns.WIDTH };

        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + " = ?" + " OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + " = ?";
        String []selectionArgs = new String[]{
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) };
        String sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";
        @SuppressLint("Recycle")
        Cursor resultSet = context.getContentResolver().query(queryUri, projections, selection, selectionArgs, sortOrder);
        if(resultSet != null){
            mediaFileArrayList = new ArrayList<>();
            while (resultSet.moveToNext()){
                // Get epochtime in seconds
                long epochTime = resultSet.getLong(
                        resultSet.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED)
                );
                if(!date.equals(MediaFile.SecondsToDateString(epochTime))){
                    continue;
                }
                String albumName = resultSet.getString(
                        resultSet.getColumnIndex(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME)
                );
                String id = resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns._ID));
                int mediaType = resultSet.getInt(resultSet.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE));
                String fileUrl = resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                long fileSize = resultSet.getLong(resultSet.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                String resolution = "Undefined";
                if(resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.HEIGHT)) != null && resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.WIDTH)) != null){
                    resolution = resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.HEIGHT)) + " x " + resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.WIDTH));
                }
                mediaFileArrayList.add(new MediaFile(id, mediaType, fileUrl, MediaFile.SecondsToDatetimeString(epochTime), MediaFile.FormatFileSize(fileSize), resolution, albumName, favouriteIdArrayList.contains(id)));
            }
            return mediaFileArrayList;
        }
        Toast.makeText(context,"No such file", Toast.LENGTH_LONG).show();
        return null;

    }


    public static void AddToFavourite(@NonNull Context context, String id){
        if(!favouriteIdArrayList.contains(id)){
            favouriteIdArrayList.add(id);
            for (MediaFile mediaFile : mediaFileArrayList) {
                if(mediaFile.id.equals(id)){
                    mediaFile.isFavourite = true;
                    break;
                }
            }
            SaveFavouriteMediaFiles(context);
        }
    }

    public static void RemoveFromFavourite(@NonNull Context context, String id){
        if(favouriteIdArrayList.contains(id)){
            favouriteIdArrayList.remove(id);
            for (MediaFile mediaFile : mediaFileArrayList) {
                if (mediaFile.id.equals(id)){
                    mediaFile.isFavourite = false;
                    break;
                }
            }
            SaveFavouriteMediaFiles(context);
        }
    }
    //#region Private Methods

    //Load array list media files group by favourite. Call once when LoadAllMediaFiles
    private static void LoadFavouriteMediaFiles(@NonNull Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(STORE_FILE_NAME, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(FAVOURITE_FILE_KEY, null);
        if(json != null){
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            Gson gson = new Gson();
            favouriteIdArrayList = gson.fromJson(json, type);
        }
    }
    private static void SaveFavouriteMediaFiles(@NonNull Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(STORE_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(favouriteIdArrayList);
        editor.putString(FAVOURITE_FILE_KEY, json);
        editor.apply();
    }
    //#endregion

}
