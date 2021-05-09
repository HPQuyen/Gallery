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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DataHandler {
    public static final int ONE = 1;
    public static final int ALL = Integer.MAX_VALUE;

    private static final ArrayList<MediaFile> mediaFileArrayList = new ArrayList<>();
    private static final ArrayList<String> folderNameArrayList = new ArrayList<>( );
    private static final ArrayList<String> dateArrayList = new ArrayList<>();
    private static final HashSet<String> mediaFileHashSet = new HashSet<>();
    private static ArrayList<String> favouriteIdArrayList = new ArrayList<>();
    private static HashMap<String, ArrayList<String>> albumHashMap = new HashMap<>();
    private static ArrayList<MediaFile> incognitoFileArrayList = new ArrayList<>();

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
            LoadAlbum(context);
            LoadFavouriteMediaFiles(context);
            LoadIncognitoFile(context);
            while (resultSet.moveToNext()){
                String albumName = resultSet.getString(
                        resultSet.getColumnIndex(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME)
                );
                if(!folderNameArrayList.contains(albumName)){
                    folderNameArrayList.add(albumName);
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

                MediaFile mediaFile = new MediaFile(id, mediaType, fileUrl, MediaFile.SecondsToDatetimeString(epochTime), MediaFile.FormatFileSize(fileSize), resolution, albumName, favouriteIdArrayList.contains(id), System.currentTimeMillis());
                mediaFileArrayList.add(mediaFile);
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
                if(!folderNameArrayList.contains(albumName)){
                    folderNameArrayList.add(albumName);
                }
                // Get epochtime in seconds
                long epochTime = resultSet.getLong(
                        resultSet.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED)
                );
                String date = MediaFile.SecondsToDateString(epochTime);
                if(!dateArrayList.contains(date)){
                    dateArrayList.add(0, date);
                }

                int mediaType = resultSet.getInt(resultSet.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE));
                String fileUrl = resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                long fileSize = resultSet.getLong(resultSet.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                String resolution = "Undefined";
                if(resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.HEIGHT)) != null && resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.WIDTH)) != null){
                    resolution = resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.HEIGHT)) + " x " + resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.WIDTH));
                }

                mediaFileArrayList.add(0, new MediaFile(id, mediaType, fileUrl, MediaFile.SecondsToDatetimeString(epochTime), MediaFile.FormatFileSize(fileSize), resolution, albumName, favouriteIdArrayList.contains(id), System.currentTimeMillis()));
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

    // Get array list folder name
    public static ArrayList<String> GetListFolderName() { return folderNameArrayList; }

    // Get array list date array list
    public static ArrayList<String> GetListDate() { return dateArrayList; }

    public static ArrayList<MediaFile> GetListIncognitoFile() { return incognitoFileArrayList; }

    /*
        Get array list album.
        <return>
            Return null if no album was created.
        </return>
     */
    public static ArrayList<String> GetListAlbumName() {
        if(albumHashMap.size() == 0)
            return null;
        ArrayList<String> albumName = new ArrayList<>();
        for(Map.Entry<String, ArrayList<String>> item : albumHashMap.entrySet()){
            albumName.add(item.getKey());
        }
        return albumName;
    }


    /**
     *   Get array list media files group by folder name.
     *
     *   @param NUMBER_OF_FILE ONE or ALL
     *   @return               Return null if no media file found.
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static ArrayList<MediaFile> GetMediaFilesByFolder(@NonNull Context context, String folderName, int NUMBER_OF_FILE){
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

        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + " = ?" + " AND " + MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME + " = ?" + " OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + " = ?" + " AND " + MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME + " = ?";
        String []selectionArgs = new String[]{
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                folderName,
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
                folderName };
        String sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";

        @SuppressLint("Recycle")
        Cursor resultSet = context.getContentResolver().query(queryUri, projections, selection, selectionArgs, sortOrder);
        if(resultSet != null){
            mediaFileArrayList = new ArrayList<>();
            while (resultSet.moveToNext()){
                String id = resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns._ID));
                // Get epochtime in seconds
                long epochTime = resultSet.getLong(resultSet.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED));
                int mediaType = resultSet.getInt(resultSet.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE));
                String fileUrl = resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                long fileSize = resultSet.getLong(resultSet.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                String resolution = "Undefined";
                if(resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.HEIGHT)) != null && resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.WIDTH)) != null){
                    resolution = resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.HEIGHT)) + " x " + resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.WIDTH));
                }
                mediaFileArrayList.add(new MediaFile(id, mediaType, fileUrl, MediaFile.SecondsToDatetimeString(epochTime), MediaFile.FormatFileSize(fileSize), resolution, folderName, favouriteIdArrayList.contains(id), System.currentTimeMillis()));
                if(NUMBER_OF_FILE == ONE)
                    break;
            }
            return mediaFileArrayList.size() == 0 ? null : mediaFileArrayList;
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
                mediaFileArrayList.add(new MediaFile(id, mediaType, fileUrl, MediaFile.SecondsToDatetimeString(epochTime), MediaFile.FormatFileSize(fileSize), resolution, albumName, favouriteIdArrayList.contains(id), System.currentTimeMillis()));
            }
            return mediaFileArrayList;
        }
        Toast.makeText(context,"No such file", Toast.LENGTH_LONG).show();
        return null;

    }

    /*
        Get array list media files group by album name.
        <param>
            NUMBER_OF_FILE: ONE or ALL
        </param>
        <return>
            Return null if no media file found.
        </return>
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static ArrayList<MediaFile> GetMediaFileByAlbum(@NonNull Context context, String albumName, int NUMBER_OF_FILE){
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
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)};
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
                if(!Objects.requireNonNull(albumHashMap.get(albumName)).contains(id))
                    continue;
                // Get epochtime in seconds
                long epochTime = resultSet.getLong(resultSet.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED));
                int mediaType = resultSet.getInt(resultSet.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE));
                String fileUrl = resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                long fileSize = resultSet.getLong(resultSet.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                String resolution = "Undefined";
                if(resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.HEIGHT)) != null && resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.WIDTH)) != null){
                    resolution = resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.HEIGHT)) + " x " + resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.WIDTH));
                }
                String folderName = resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME));
                mediaFileArrayList.add(new MediaFile(id, mediaType, fileUrl, MediaFile.SecondsToDatetimeString(epochTime), MediaFile.FormatFileSize(fileSize), resolution, folderName, favouriteIdArrayList.contains(id), System.currentTimeMillis()));
                if(NUMBER_OF_FILE == ONE)
                    break;
            }
            return mediaFileArrayList.size() == 0 ? null : mediaFileArrayList;
        }
        Toast.makeText(context,"No such file", Toast.LENGTH_LONG).show();
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static MediaFile GetMediaFileById(@NonNull Context context, String id){
        MediaFile mediaFile = null;
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

        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + " = ?" + " AND " + MediaStore.Files.FileColumns._ID + " = ?" + " OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + " = ?" + " AND " + MediaStore.Files.FileColumns._ID + " = ?";
        String []selectionArgs = new String[]{
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                id,
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
                id};
        String sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";

        @SuppressLint("Recycle")
        Cursor resultSet = context.getContentResolver().query(queryUri, projections, selection, selectionArgs, sortOrder);
        if(resultSet != null){
            if (resultSet.moveToNext()){
                // Get epochtime in seconds
                long epochTime = resultSet.getLong(resultSet.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED));
                int mediaType = resultSet.getInt(resultSet.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE));
                String fileUrl = resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                long fileSize = resultSet.getLong(resultSet.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                String resolution = "Undefined";
                if(resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.HEIGHT)) != null && resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.WIDTH)) != null){
                    resolution = resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.HEIGHT)) + " x " + resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.WIDTH));
                }
                String folderName = resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME));
                mediaFile = new MediaFile(id, mediaType, fileUrl, MediaFile.SecondsToDatetimeString(epochTime), MediaFile.FormatFileSize(fileSize), resolution, folderName, favouriteIdArrayList.contains(id), System.currentTimeMillis());
            }
            return mediaFile;
        }
        Toast.makeText(context,"No such file", Toast.LENGTH_LONG).show();
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static MediaFile GetMediaFileByUri(@NonNull Context context, Uri uri){
        MediaFile mediaFile = null;
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

        String sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";
        @SuppressLint("Recycle")
        Cursor resultSet = context.getContentResolver().query(uri, projections, null, null, sortOrder);
        if(resultSet != null){
            if (resultSet.moveToNext()){
                // Get epochtime in seconds
                String id = resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns._ID));
                long epochTime = resultSet.getLong(resultSet.getColumnIndex(MediaStore.Files.FileColumns.DATE_ADDED));
                int mediaType = resultSet.getInt(resultSet.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE));
                String fileUrl = resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.DATA));
                long fileSize = resultSet.getLong(resultSet.getColumnIndex(MediaStore.Files.FileColumns.SIZE));
                String resolution = "Undefined";
                if(resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.HEIGHT)) != null && resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.WIDTH)) != null){
                    resolution = resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.HEIGHT)) + " x " + resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.WIDTH));
                }
                String folderName = resultSet.getString(resultSet.getColumnIndex(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME));
                mediaFile = new MediaFile(id, mediaType, fileUrl, MediaFile.SecondsToDatetimeString(epochTime), MediaFile.FormatFileSize(fileSize), resolution, folderName, favouriteIdArrayList.contains(id), System.currentTimeMillis());
            }
            return mediaFile;
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

    public static void RemoveMediaFileFromFavourite(@NonNull Context context, String id){
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

    // Delete all media file from album
    // Call RemoveMediaFileFromAlbum to remove media file from specific album
    public static void DeleteMediaFileFromAlbum(@NonNull Context context, String id){
        if(albumHashMap.size() == 0)
            return;
        for (Map.Entry<String, ArrayList<String>> albumItem : albumHashMap.entrySet()) {
            for (int i1 = 0; i1 < albumItem.getValue().size(); i1++) {
                if(albumItem.getValue().get(i1).equals(id)){
                    albumItem.getValue().remove(i1);
                }
            }
        }
        SaveAlbum(context);
    }


    public static void DeleteMediaFile(@NonNull Context context, String id){
        for (int i = 0; i < mediaFileArrayList.size(); i++) {
            if(mediaFileArrayList.get(i).id.equals(id)){
                DeleteMediaFileFromAlbum(context, id);
                RemoveMediaFileFromFavourite(context, id);
                mediaFileArrayList.remove(i);
                break;
            }
        }
    }

    /*
        Add a new album.
        <param>
            albumName: Name of album
            listImage: Array list image that user pick
        </param>
        <return>
            Return true if add successfully
            Return false if album exist already.
        </return>
     */
    public static boolean AddNewAlbum(@NonNull Context context, String albumName, ArrayList<String> listImage){
        if(albumHashMap.containsKey(albumName))
            return false;
        albumHashMap.put(albumName, listImage);
        SaveAlbum(context);
        return true;
    }

    /*
        Update list image to existed album.
        <param>
            albumName: Name of album
            listImage: Array list image that user pick
        </param>
        <return>
            Return true if update successfully
            Return false if album doest not exist
        </return>
     */
    public static boolean UpdateAlbum(@NonNull Context context, String albumName, ArrayList<String> listImage){
        if(albumHashMap.containsKey(albumName))
            return false;
        for (String s : listImage) {
            albumHashMap.get(albumName).add(s);
        }
        SaveAlbum(context);
        return true;
    }

    /*
        Remove album.
        <param>
            albumName: Name of album
        </param>
        <return>
            Return true if update successfully
            Return false if album doest not exist
        </return>
     */
    public static boolean RemoveAlbum(@NonNull Context context, String albumName){
        if(albumHashMap.containsKey(albumName))
            return false;
        albumHashMap.clear();
        SaveAlbum(context);
        return true;
    }

    public static void MoveToIncognitoFolder(@NonNull Context context, MediaFile mediaFile){
        incognitoFileArrayList.add(mediaFile);
        SaveIncognitoFile(context);
    }

    public static void RemoveFromIncognitoFolder(@NonNull Context context, String id){
        for (int i = 0; i < incognitoFileArrayList.size(); i++) {
            if(incognitoFileArrayList.get(i).id.equals(id)){
                incognitoFileArrayList.remove(i);
                break;
            }
        }
        SaveIncognitoFile(context);
    }

    //#region Private Methods

    //Load array list media files group by favourite. Call once when LoadAllMediaFiles
    private static void LoadFavouriteMediaFiles(@NonNull Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.Value.STORE_FILE_NAME, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(Constants.Value.FAVOURITE_FILE_KEY, null);
        if(json != null){
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            Gson gson = new Gson();
            favouriteIdArrayList = gson.fromJson(json, type);
        }
    }
    private static void SaveFavouriteMediaFiles(@NonNull Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.Value.STORE_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(favouriteIdArrayList);
        editor.putString(Constants.Value.FAVOURITE_FILE_KEY, json);
        editor.apply();
    }

    //Load list album. Call once when LoadAllMediaFiles
    private static void LoadAlbum(@NonNull Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.Value.STORE_FILE_NAME, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(Constants.Value.ALBUM_NAME_FILE_KEY, null);
        if(json != null){
            Type type = new TypeToken<HashMap<String,ArrayList<String>>>(){}.getType();
            Gson gson = new Gson();
            albumHashMap = gson.fromJson(json, type);
        }
    }
    private static void SaveAlbum(@NonNull Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.Value.STORE_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(albumHashMap);
        editor.putString(Constants.Value.ALBUM_NAME_FILE_KEY, json);
        editor.apply();
    }

    // Load incognito folder. Call once when LoadAllMediaFiles
    private static void LoadIncognitoFile(@NonNull Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.Value.STORE_FILE_NAME, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(Constants.Value.INCOGNITO_FILE_KEY, null);
        if(json != null){
            Type type = new TypeToken<ArrayList<MediaFile>>(){}.getType();
            Gson gson = new Gson();
            incognitoFileArrayList = gson.fromJson(json, type);
        }
    }
    private static void SaveIncognitoFile(@NonNull Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.Value.STORE_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(incognitoFileArrayList);
        editor.putString(Constants.Value.INCOGNITO_FILE_KEY, json);
        editor.apply();
    }
    //#endregion

}
