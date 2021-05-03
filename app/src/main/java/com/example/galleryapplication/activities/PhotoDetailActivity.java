package com.example.galleryapplication.activities;



import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;


import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;


import com.example.galleryapplication.classes.DataHandler;
import com.example.galleryapplication.classes.MediaFile;
import com.example.galleryapplication.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import ly.img.android.pesdk.PhotoEditorSettingsList;
import ly.img.android.pesdk.assets.filter.basic.FilterPackBasic;
import ly.img.android.pesdk.assets.font.basic.FontPackBasic;
import ly.img.android.pesdk.assets.frame.basic.FramePackBasic;
import ly.img.android.pesdk.assets.overlay.basic.OverlayPackBasic;
import ly.img.android.pesdk.assets.sticker.emoticons.StickerPackEmoticons;
import ly.img.android.pesdk.assets.sticker.shapes.StickerPackShapes;
import ly.img.android.pesdk.backend.model.EditorSDKResult;
import ly.img.android.pesdk.backend.model.state.LoadSettings;
import ly.img.android.pesdk.backend.model.state.PhotoEditorSaveSettings;
import ly.img.android.pesdk.backend.model.state.manager.SettingsList;
import ly.img.android.pesdk.ui.activity.EditorBuilder;
import ly.img.android.pesdk.ui.model.state.UiConfigFilter;
import ly.img.android.pesdk.ui.model.state.UiConfigFrame;
import ly.img.android.pesdk.ui.model.state.UiConfigOverlay;
import ly.img.android.pesdk.ui.model.state.UiConfigSticker;
import ly.img.android.pesdk.ui.model.state.UiConfigText;


public class PhotoDetailActivity extends AppCompatActivity {

    //#region Fields

    //#region Layout Components
    private ImageView photoDetailPreview = null;
    private MaterialToolbar topToolbar;
    private BottomNavigationView bottomToolbar;
    private BottomSheetDialog infoBottomSheetDialog = null;
    //#endregion
    public static int PESDK_RESULT = 6968;
    private Uri selectedImage = null;
    private MediaFile selectedMediaFile;
    private FloatingActionButton favouriteFab = null;
    //#endregion

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_photo);
        Init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint({"NonConstantResourceId", "UseCompatLoadingForDrawables"})
    private void Init(){
        // Set up bottom app bar
        bottomToolbar = findViewById(R.id.bottom_navigation_view);
        bottomToolbar.setSelected(false);
        bottomToolbar.getMenu().findItem(bottomToolbar.getSelectedItemId()).setCheckable(false);
        bottomToolbar.setOnNavigationItemSelectedListener(item -> {
            item.setCheckable(false);
            switch (item.getItemId()){
                case  R.id.rotate_left_btn:
                    OnClickRotateLeft();
                    break;
                case  R.id.rotate_right_btn:
                    OnClickRotateRight();
                    break;
                case R.id.edit_btn:
                    OnClickEditPhoto();
                    break;
                default:
                    break;
            }
            return false;
        });
        // Set up top app bar
        topToolbar = findViewById(R.id.top_app_bar);
        setSupportActionBar(topToolbar);

        // Load data from previous intent
        Intent intent = getIntent();
        //String imageFilePath = intent.getStringExtra(MediaFile.FILE_PATH);
        selectedMediaFile = new MediaFile(
                intent.getStringExtra(MediaFile.FILE_ID),
                intent.getIntExtra(MediaFile.FILE_MEDIA_TYPE, MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                intent.getStringExtra(MediaFile.FILE_PATH),
                intent.getStringExtra(MediaFile.FILE_DATE),
                intent.getStringExtra(MediaFile.FILE_SIZE),
                intent.getStringExtra(MediaFile.FILE_RESOLUTION),
                intent.getStringExtra(MediaFile.FILE_ALBUM_NAME),
                intent.getBooleanExtra(MediaFile.FILE_FAVOURITE, false));
        photoDetailPreview = findViewById(R.id.photoDetailPreview);
        photoDetailPreview.setOnClickListener(this::ToggleToolbars);
        selectedImage = Uri.fromFile(new File(selectedMediaFile.fileUrl));
        DisplayImage(MediaFile.GetBitMap(selectedMediaFile.fileUrl));

        // Set up view detail info
        infoBottomSheetDialog = new BottomSheetDialog(this);
        View infoBottomSheetView = LayoutInflater.from(this).inflate(R.layout.fragment_info_bottom_sheet, findViewById(R.id.info_bottom_sheet_container));
        TextInputEditText infoDatetime = infoBottomSheetView.findViewById(R.id.info_datetime_tv);
        infoDatetime.setText(selectedMediaFile.datetime);
        TextInputEditText infoResolution = infoBottomSheetView.findViewById(R.id.info_resolution_tv);
        infoResolution.setText(selectedMediaFile.resolution);
        TextInputEditText infoFileSize = infoBottomSheetView.findViewById(R.id.info_file_size_tv);
        infoFileSize.setText(selectedMediaFile.fileSize);
        TextInputEditText infoAlbumName = infoBottomSheetView.findViewById(R.id.info_album_name_tv);
        infoAlbumName.setText(selectedMediaFile.albumName);
        TextInputEditText infoFilePath = infoBottomSheetView.findViewById(R.id.info_file_path_tv);
        infoFilePath.setText(selectedMediaFile.fileUrl);
        infoBottomSheetDialog.setContentView(infoBottomSheetView);

        // Set up favourite fab
        favouriteFab = findViewById(R.id.favourite_fab);
        favouriteFab.setImageDrawable(selectedMediaFile.isFavourite?getDrawable(R.drawable.ic_baseline_favorite_32):getDrawable(R.drawable.ic_baseline_favorite_border_24));
    }

    private void DisplayImage(Bitmap bitmap){
        photoDetailPreview.setImageBitmap(bitmap);
    }
//    private CharSequence MenuIconWithText(int icon, int title) {
//        @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(icon);
//        String titleString = getResources().getString(title);
//        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//        SpannableString sb = new SpannableString("    " + titleString);
//        ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
//        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        return sb;
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.top_app_bar_menu_2, menu);

        MenuItem deleteItem = menu.findItem(R.id.delete_btn);
        deleteItem.setOnMenuItemClickListener(item -> {
            Log.d("Nothing", "Delete Click");
            OnClickDelete();
            return true;
        });

        MenuItem shareItem = menu.findItem(R.id.share_btn);
        shareItem.setOnMenuItemClickListener(item -> {
            Log.d("Nothing", "Share Click");
            OnClickShare();
            return true;
        });

        MenuItem infoItem = menu.findItem(R.id.info);
        infoItem.setOnMenuItemClickListener(item -> {
            Log.d("Nothing", "Info Click");
            OnClickInfoDetail();
            return true;
        });

        MenuItem copyItem = menu.findItem(R.id.copy);
        copyItem.setOnMenuItemClickListener(item -> {
            Log.d("Nothing", "Copy Click");
            OnClickCopy();
            return true;
        });

        MenuItem setPictureItem = menu.findItem(R.id.set_picture_as);
        setPictureItem.setOnMenuItemClickListener(item -> {
            Log.d("Nothing", "Set Picture As Click");
            OnClickSetPictureAs();
            return true;
        });
        return true;
    }

    private SettingsList CreatePesdkSettingsList() {

        // Create a empty new SettingsList and apply the changes on this referance.
        PhotoEditorSettingsList settingsList = new PhotoEditorSettingsList();

        // If you include our asset Packs and you use our UI you also need to add them to the UI,
        // otherwise they are only available for the backend
        // See the specific feature sections of our guides if you want to know how to add our own Assets.

        settingsList.getSettingsModel(UiConfigFilter.class).setFilterList(
                FilterPackBasic.getFilterPack()
        );

        settingsList.getSettingsModel(UiConfigText.class).setFontList(
                FontPackBasic.getFontPack()
        );

        settingsList.getSettingsModel(UiConfigFrame.class).setFrameList(
                FramePackBasic.getFramePack()
        );

        settingsList.getSettingsModel(UiConfigOverlay.class).setOverlayList(
                OverlayPackBasic.getOverlayPack()
        );

        settingsList.getSettingsModel(UiConfigSticker.class).setStickerLists(
                StickerPackEmoticons.getStickerCategory(),
                StickerPackShapes.getStickerCategory()
        );

        return settingsList;
    }
    private void OpenEditor(Uri inputImage) {
        SettingsList settingsList = CreatePesdkSettingsList();

        // Set input image
        settingsList.getSettingsModel(LoadSettings.class).setSource(inputImage);

        settingsList.getSettingsModel(PhotoEditorSaveSettings.class).setOutputToGallery(Environment.DIRECTORY_DCIM);

        new EditorBuilder(this)
                .setSettingsList(settingsList)
                .startActivityForResult(this, PESDK_RESULT);
    }
//    private ExifInterface UriToExifInterface(Uri uri){
//        ExifInterface exifInterface = null;
//        InputStream in = null;
//        try {
//            in = getContentResolver().openInputStream(uri);
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//                exifInterface = new ExifInterface(in);
//            }else{
//                exifInterface = new ExifInterface(uri.getPath());
//            }
//        } catch (IOException e) {
//            // Handle any errors
//        } finally {
//            if (in != null) {
//                try {
//                    in.close();
//                } catch (IOException ignored) {}
//            }
//            return exifInterface;
//        }
//

    //#region On Click Event
    public void ToggleToolbars(View view){
        if(topToolbar.getVisibility() == View.VISIBLE){
            topToolbar.setVisibility(View.GONE);
            bottomToolbar.setVisibility(View.GONE);
        }else {
            topToolbar.setVisibility(View.VISIBLE);
            bottomToolbar.setVisibility(View.VISIBLE);
        }
    }
    private void OnClickDelete(){
        MaterialAlertDialogBuilder deleteAlert = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.title_delete_confirm)
                .setMessage(R.string.sub_text_delete_confirm)
                .setNegativeButton(R.string.cancel_btn, (dialog, which) -> {
                    Snackbar.make(findViewById(R.id.constraintLayout), R.string.title_delete_failed, Snackbar.LENGTH_SHORT)
                            .show();
                })
                .setPositiveButton(R.string.delete_btn, (dialog, which) -> {
                    if(MediaFile.DeleteMediaFile(this, selectedMediaFile.fileUrl)){
                        onBackPressed();
                    }
                });
        deleteAlert.show();
    }
    private void OnClickShare(){
        MediaFile.ShareMediaFile(this, selectedMediaFile);
    }
    private void OnClickInfoDetail(){
        if(infoBottomSheetDialog.isShowing()){
            infoBottomSheetDialog.dismiss();
        }else {
            infoBottomSheetDialog.show();
        }
    }
    private void OnClickCopy(){
        ArrayList<String> arr = DataHandler.GetListAlbumName();
        for (String a : arr) {
            Log.d("Nothing", a);
        }
    }
    private void OnClickSetPictureAs() {
        Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);

        Uri photoURI = null;
        try {
            photoURI = MediaFile.GetUriContentFromImageFile(this, selectedMediaFile, "temporary_file.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        intent.setDataAndType(photoURI,"image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("mimeType","image/*");
        startActivity(Intent.createChooser(intent,"Set Image As"));
    }
    private void OnClickRotateLeft(){
        Log.d("Nothing", "Rotate Left Click");
        Bitmap selectedImageBitmap = MediaFile.GetBitMap(selectedMediaFile.fileUrl);
        Matrix matrix = new Matrix();
        matrix.postRotate(-90);
        selectedImageBitmap = Bitmap.createBitmap(selectedImageBitmap, 0, 0, selectedImageBitmap.getWidth(), selectedImageBitmap.getHeight(), matrix, true);
        MediaFile.UpdateImage(selectedMediaFile.fileUrl, selectedImageBitmap);
        DisplayImage(selectedImageBitmap);

    }
    private void OnClickRotateRight(){
        Log.d("Nothing", "Rotate Right Click");
        Bitmap selectedImageBitmap = MediaFile.GetBitMap(selectedMediaFile.fileUrl);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        selectedImageBitmap = Bitmap.createBitmap(selectedImageBitmap, 0, 0, selectedImageBitmap.getWidth(), selectedImageBitmap.getHeight(), matrix, true);
        MediaFile.UpdateImage(selectedMediaFile.fileUrl, selectedImageBitmap);
        DisplayImage(selectedImageBitmap);
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void OnClickFavorite(View view){
        Log.d("Nothing", "Favourite Click");
        if(selectedMediaFile.isFavourite){
            DataHandler.RemoveFromFavourite(this, selectedMediaFile.id);
        }else{
            DataHandler.AddToFavourite(this, selectedMediaFile.id);
        }
        selectedMediaFile.isFavourite = !selectedMediaFile.isFavourite;
        favouriteFab.setImageDrawable(selectedMediaFile.isFavourite?getDrawable(R.drawable.ic_baseline_favorite_32):getDrawable(R.drawable.ic_baseline_favorite_border_24));
    }
    public void OnClickEditPhoto(){
        OpenEditor(selectedImage);
    }
    //#endregion



    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                if(imageExif.hasAttribute(ExifInterface.TAG_DEFAULT_CROP_SIZE))
//                    Log.d("Nothing",imageExif.getAttribute(ExifInterface.TAG_DEFAULT_CROP_SIZE));
//                els
//                if(imageExif.hasAttribute(ExifInterface.TAG_DATETIME_DIGITIZED))
//                    Log.d("Nothing",imageExif.getAttribute(ExifInterface.TAG_DATETIME_DIGITIZED));
//            }
        Log.d("Nothing", String.valueOf(requestCode));
        if (resultCode == RESULT_OK && requestCode == PESDK_RESULT) {
            // Editor has saved an Image.
            EditorSDKResult data = new EditorSDKResult(intent);

            // This adds the result and source image to Android's gallery
            data.notifyGallery(EditorSDKResult.UPDATE_RESULT & EditorSDKResult.UPDATE_SOURCE);

            Log.i("Nothing", "Source image is located here " + data.getSourceUri());
            Log.i("Nothing", "Result image is located here " + data.getResultUri());

            // TODO: Do something with the result image
            Uri resultImage = data.getResultUri();
            try {
                InputStream stream = getContentResolver().openInputStream(resultImage);
                Bitmap photo = BitmapFactory.decodeStream(stream);
                Log.d("Nothing", String.valueOf(photo.getWidth()));
                Log.d("Nothing", String.valueOf(photo.getHeight()));
                if (MediaFile.SaveImage(this, photo, "Photo & Video")){
                    Log.d("Nothing", "Save image success");
                    getContentResolver().delete(resultImage, null, null);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


        } else if (resultCode == RESULT_CANCELED && requestCode == PESDK_RESULT) {
            // Editor was canceled
            EditorSDKResult data = new EditorSDKResult(intent);

            Uri sourceURI = data.getSourceUri();
            // TODO: Do something with the source...
        }
    }
}