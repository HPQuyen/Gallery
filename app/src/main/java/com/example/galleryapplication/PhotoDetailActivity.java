package com.example.galleryapplication;



import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.impl.utils.Exif;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;


import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;

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
import ly.img.android.pesdk.ui.activity.PhotoEditorActivity;
import ly.img.android.pesdk.ui.model.state.UiConfigFilter;
import ly.img.android.pesdk.ui.model.state.UiConfigFrame;
import ly.img.android.pesdk.ui.model.state.UiConfigOverlay;
import ly.img.android.pesdk.ui.model.state.UiConfigSticker;
import ly.img.android.pesdk.ui.model.state.UiConfigText;
import ly.img.android.pesdk.ui.utils.PermissionRequest;
import ly.img.android.serializer._3.IMGLYFileWriter;


public class PhotoDetailActivity extends AppCompatActivity implements PermissionRequest.Response {

    //#region Fields

    //#region Layout Components
    ImageView photoDetailPreview = null;
    ConstraintLayout topToolbarLayout,bottomToolbarLayout;
    PopupMenu popupMenu = null;
    Uri selectedImage = null;
    ExifInterface imageExif = null;
    //#endregion
    public static int PESDK_RESULT = 6968;
    public static int GALLERY_RESULT = 6969;
    //#endregion

    // Important permission request for Android 6.0 and above, don't forget to add this!
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionRequest.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void permissionGranted() {}

    @Override
    public void permissionDenied() {
        /* TODO: The Permission was rejected by the user. The Editor was not opened,
         * Show a hint to the user and try again. */

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TurnOffTitle();
        setContentView(R.layout.activity_photo_detail);
        Init();
        OpenSystemGalleryToSelectAnImage();

//        Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        String[] projection = { MediaStore.MediaColumns.DATA,
//                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };
//        Cursor cursor = getContentResolver().query(uri, projection, null,
//                null, null);
    }
    private void Init(){
        final int infoOrder = 0;
        final int copyOrder = 1;
        final int setPictureOrder = 2;

        ImageButton moreOptionsBtn = findViewById(R.id.more_option_dropdown);
        popupMenu = new PopupMenu(getApplicationContext(), moreOptionsBtn);
        Menu menu = popupMenu.getMenu();
        menu.add(Menu.NONE, Menu.NONE, infoOrder, MenuIconWithText(R.drawable.ic_baseline_info_24, R.string.info_dropdown_item_2));
        menu.add(Menu.NONE, Menu.NONE, copyOrder, MenuIconWithText(R.drawable.ic_baseline_file_copy_24, R.string.copy_dropdown_item_2));
        menu.add(Menu.NONE, Menu.NONE, setPictureOrder, MenuIconWithText(R.drawable.ic_baseline_add_to_home_screen_24, R.string.set_picture_as_dropdown_item_2));

        popupMenu.getMenuInflater().inflate(R.menu.dropdown_menu_2, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            int order = item.getOrder();
            switch (order){
                case infoOrder:
                    OnClickInfoDetail();
                    break;
                case copyOrder:

                    break;
                case setPictureOrder:

                    break;
                default:
                    return false;
            }
            return true;
        });


//        photoDetailPreview.setImageResource(R.drawable._28195615_136128921308288_9046592716708631099_n);

        topToolbarLayout = findViewById(R.id.top_toolbar_layout);
        bottomToolbarLayout = findViewById(R.id.bottom_toolbar_layout);


    }
    private CharSequence MenuIconWithText(int icon, int title) {
        Drawable drawable = getResources().getDrawable(icon);
        String titleString = getResources().getString(title);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        SpannableString sb = new SpannableString("    " + titleString);
        ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }
    private void TurnOffTitle(){
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
    }

    private void OpenSystemGalleryToSelectAnImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, GALLERY_RESULT);
        } else {
            Toast.makeText(
                    this,
                    "No Gallery APP installed",
                    Toast.LENGTH_LONG
            ).show();
        }
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


    private ExifInterface UriToExifInterface(Uri uri){
        ExifInterface exifInterface = null;
        InputStream in = null;
        try {
            in = getContentResolver().openInputStream(uri);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                exifInterface = new ExifInterface(in);
            }else{
                exifInterface = new ExifInterface(uri.getPath());
            }
        } catch (IOException e) {
            // Handle any errors
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {}
            }
            return exifInterface;
        }
    }

    //#region On Click Event
    private void OnClickInfoDetail(){
        if(getSupportFragmentManager().findFragmentById(R.id.info_detail_fragment_view) == null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().setReorderingAllowed(true).add(R.id.info_detail_fragment_view, InfoDetail.newInstance("0","2"), null);
            fragmentTransaction.commit();
        }
    }
    public void OnClickMoreOptions(View view){
        popupMenu.show();
    }
    public void ToggleToolbars(View view){
        if(topToolbarLayout.getVisibility() == View.VISIBLE){
            topToolbarLayout.setVisibility(View.INVISIBLE);
            bottomToolbarLayout.setVisibility(View.INVISIBLE);
        }else {
            topToolbarLayout.setVisibility(View.VISIBLE);
            bottomToolbarLayout.setVisibility(View.VISIBLE);
        }
    }
    public void OnClickFavorite(View view){

    }
    public void OnClickEditPhoto(View view){
        OpenEditor(selectedImage);
    }
    //#endregion

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_OK && requestCode == GALLERY_RESULT) {
            // Open Editor with some uri in this case with an image selected from the system gallery.
            selectedImage = intent.getData();

            imageExif = UriToExifInterface(selectedImage);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                if(imageExif.hasAttribute(ExifInterface.TAG_DEFAULT_CROP_SIZE))
//                    Log.d("Nothing",imageExif.getAttribute(ExifInterface.TAG_DEFAULT_CROP_SIZE));
//                els
//                if(imageExif.hasAttribute(ExifInterface.TAG_DATETIME_DIGITIZED))
//                    Log.d("Nothing",imageExif.getAttribute(ExifInterface.TAG_DATETIME_DIGITIZED));
//            }
            try {
                Bitmap selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                photoDetailPreview = findViewById(R.id.photoDetailPreview);
                photoDetailPreview.setImageBitmap(selectedImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (resultCode == RESULT_OK && requestCode == PESDK_RESULT) {
            // Editor has saved an Image.
            EditorSDKResult data = new EditorSDKResult(intent);

            // This adds the result and source image to Android's gallery
            data.notifyGallery(EditorSDKResult.UPDATE_RESULT & EditorSDKResult.UPDATE_SOURCE);

            Log.i("PESDK", "Source image is located here " + data.getSourceUri());
            Log.i("PESDK", "Result image is located here " + data.getResultUri());

            // TODO: Do something with the result image

//            // OPTIONAL: read the latest state to save it as a serialisation
//            SettingsList lastState = data.getSettingsList();
//            try {
//                new IMGLYFileWriter(lastState).writeJson(new File(
//                        Environment.getExternalStorageDirectory(),
//                        "serialisationReadyToReadWithPESDKFileReader.json"
//                ));
//            } catch (Exception e) { e.printStackTrace(); }

        } else if (resultCode == RESULT_CANCELED && requestCode == PESDK_RESULT) {
            // Editor was canceled
            EditorSDKResult data = new EditorSDKResult(intent);

            Uri sourceURI = data.getSourceUri();
            // TODO: Do something with the source...
        }
    }
}