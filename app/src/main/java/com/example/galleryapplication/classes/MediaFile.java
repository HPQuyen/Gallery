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
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import com.example.galleryapplication.R;

import com.example.galleryapplication.R;
import com.example.galleryapplication.enumerators.VIEW_DETAIL_MODE;
import com.example.galleryapplication.interfaces.IAction;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

public class MediaFile {
    public static final String FILE_ID = "file_id";
    public static final String FILE_PATH = "file_path";
    public static final String FILE_FOLDER_NAME = "file_album_name";
    public static final String FILE_DATE = "file_date";
    public static final String FILE_RESOLUTION = "file_resolution";
    public static final String FILE_SIZE = "file_size";
    public static final String FILE_FAVOURITE = "file_favourite";
    public static final String FILE_MEDIA_TYPE = "file_media_type";
    public static final String FILE_VIEW_MODE = "file_view_mode";
    public static final String FILE_ADAPTER_POSITION = "file_adapter_position";

    public static final int THUMBNAIL_SIZE_STANDARD = 256;
    public static final int THUMBNAIL_SIZE_SMALL = 128;

    public String id;
    public int mediaType;
    public String fileUrl;
    public String folderName;
    public String datetime;
    public String fileSize;
    public String resolution;
    public boolean isFavourite;
    public String location;
    public long lastTimeModified;

    public MediaFile(String id, int mediaType, String fileUrl, String datetime, String fileSize, String resolution, String folderName, boolean isFavourite,long lastTimeModified){
        this.id = id;
        this.mediaType = mediaType;
        this.fileUrl = fileUrl;
        this.datetime = datetime;
        this.fileSize = fileSize;
        this.resolution = resolution;
        this.folderName = folderName;
        this.isFavourite = isFavourite;
        this.lastTimeModified = lastTimeModified;
    }
    public MediaFile Clone(){
        return new MediaFile(id, mediaType, fileUrl, datetime, fileSize, resolution, folderName, isFavourite, lastTimeModified);
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
    public static String GetMediaFileLocation(@NonNull Context context, MediaFile mediaFile) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        double[] latLong = new double[2];
        if(mediaFile.mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE){
            Uri photoUri = Uri.fromFile(new File(mediaFile.fileUrl));

            try {
                // Get location data using the Exifinterface library.
                // Exception occurs if ACCESS_MEDIA_LOCATION permission isn't granted.
                InputStream stream = context.getContentResolver().openInputStream(photoUri);
                if (stream != null) {
                    ExifInterface exifInterface = new ExifInterface(stream);
                    double[] returnedLatLong = exifInterface.getLatLong();

                    // If lat/long is null, fall back to the coordinates (0, 0).
                    latLong = returnedLatLong != null ? returnedLatLong : new double[2];

                    // Don't reuse the stream associated with
                    // the instance of "ExifInterface".
                    stream.close();
                }
            }catch (Exception e){
                Log.e("Nothing", "Cannot retrieve video file", e);
            }

        }else{
            Uri videoUri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, Long.parseLong(mediaFile.id));
            try {
                retriever.setDataSource(context, videoUri);
            } catch (RuntimeException e) {
                Log.e("Nothing", "Cannot retrieve video file", e);
            }
            // Metadata should use a standardized format.
            String locationMetadata = retriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_LOCATION);
            return locationMetadata;
        }

        if(latLong[0] == 0 && latLong[1] == 0){
            return "Undefined";
        }
        try {
            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(context, Locale.getDefault());
            addresses = geocoder.getFromLocation(latLong[0], latLong[1], 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return addresses.get(0).getLocality() + " " + addresses.get(0).getCountryName();
        } catch (IOException e) {
            e.printStackTrace();
            return "Undefined";
        }

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

    public static boolean SaveVideo(@NonNull Context context, @NonNull MediaFile mediaFile, String folderName, VIEW_DETAIL_MODE viewDetailMode){
        final String SALT = context.getString(R.string.app_name);
        final String relativeLocation = Environment.DIRECTORY_DCIM + File.separator + folderName;
        long timeMillis = System.currentTimeMillis();
        try {
            String imagesDir = Environment.getExternalStoragePublicDirectory(relativeLocation).toString();
            File directory = new File(imagesDir);
            if(!directory.exists())
                directory.mkdir();
            File video = new File(imagesDir, SALT + timeMillis + ".mp4");
            OutputStream fos = new FileOutputStream(video);

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(video);

            mediaScanIntent.setData(contentUri);
            context.sendBroadcast(mediaScanIntent);

            FileInputStream fIn;
            if(viewDetailMode == VIEW_DETAIL_MODE.INCOGNITO)
                fIn = context.openFileInput(mediaFile.id + ".mp4");
            else
                fIn = new FileInputStream(new File(mediaFile.fileUrl));
            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = fIn.read(buf)) > 0) {
                fos.write(buf, 0, len);
            }
            fIn.close();
            fos.flush();
            fos.close();

        }catch (Exception ignore){
            ignore.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean DeleteMediaFile(@NonNull final Context context, MediaFile mediaFile) {
        // Set up the projection (we only need the ID)
        String[] projection = { MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns._ID};
        boolean isSuccess;
        // Match on the file path
        String selection = MediaStore.Files.FileColumns.DATA + " = ?";
        String[] selectionArgs = new String[]{mediaFile.fileUrl};


        // Query for the ID of the media matching the file path
        Uri queryUri = MediaStore.Files.getContentUri("external");
        ContentResolver contentResolver = context.getContentResolver();
        Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
        if (c.moveToFirst()) {
            // We found the ID. Deleting the item via the content provider will also remove the file
            long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID));
            Uri deleteUri = ContentUris.withAppendedId(MediaStore.Files.getContentUri("external"), id);
            contentResolver.delete(deleteUri, null, null);
            DataHandler.DeleteMediaFile(context, String.valueOf(id));
            isSuccess = true;
        } else {
            // File not found in media store DB
            File file = new File(mediaFile.fileUrl);
            if(file.exists() && file.delete()){
                DataHandler.RemoveFromIncognitoFolder(context, mediaFile.id);
                isSuccess = true;
            }else{
                Log.e("Nothing", "File not found or delete failed");
                isSuccess = false;
            }
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
                file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), File.separator +"share.mp4");
                FileOutputStream fOut = new FileOutputStream(file);
                FileInputStream fIn = new FileInputStream(new File(mediaFile.fileUrl));
                // Copy the bits from instream to outstream
                byte[] buf = new byte[1024];
                int len;
                while ((len = fIn.read(buf)) > 0) {
                    fOut.write(buf, 0, len);
                }
                fIn.close();
                fOut.flush();
                fOut.close();
            }
            file.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri fileUri = FileProvider.getUriForFile(context.getApplicationContext(), "com.example.galleryapplication.fileprovider", file);

            intent.putExtra(Intent.EXTRA_STREAM, fileUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/jpg");
            context.startActivity(Intent.createChooser(intent, "Share image via"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetWorldReadable")
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void HideMediaFile(@NonNull Context context, MediaFile mediaFile, Consumer<Boolean> callback) {
        String filename = null;
        FileOutputStream fos;
        try{
            FileInputStream fIn = new FileInputStream(new File(mediaFile.fileUrl));

            if(mediaFile.mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE){
                filename = mediaFile.id + ".jpg";
                fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
                Bitmap bitmap = GetBitMap(mediaFile.fileUrl);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            }else{
                filename = mediaFile.id + ".mp4";
                fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
                // Copy the bits from instream to outstream
                byte[] buf = new byte[1024];
                int len;
                while ((len = fIn.read(buf)) > 0) {
                    fos.write(buf, 0, len);
                }
            }
            fIn.close();
            fos.flush();
            fos.close();
            MediaFile.DeleteMediaFile(context, mediaFile);
            mediaFile.fileUrl = new File(context.getFilesDir(), filename).getAbsolutePath();
            Log.d("Nothing", mediaFile.fileUrl);
            DataHandler.MoveToIncognitoFolder(context, mediaFile);
            callback.accept(true);
        }catch (Exception ignore){
            callback.accept(false);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static boolean RevealMediaFile(@NonNull Context context, MediaFile mediaFile){
        try {
            boolean isSuccess = false;
            if(mediaFile.mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE){
                Bitmap bitmap = GetBitMap(mediaFile.fileUrl);
                DeleteMediaFile(context, mediaFile);
                isSuccess = SaveImage(context, bitmap, mediaFile.folderName);
            }else{
                SaveVideo(context, mediaFile, mediaFile.folderName, VIEW_DETAIL_MODE.INCOGNITO);
                isSuccess = DeleteMediaFile(context, mediaFile);
            }
            return isSuccess;
        }catch (Exception e){
            return false;
        }
    }
}
