package com.example.galleryapplication.activities;



import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.PopupMenu;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;


import com.example.galleryapplication.classes.DataHandler;
import com.example.galleryapplication.classes.MediaFile;
import com.example.galleryapplication.R;
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
    private BottomNavigationView bottomToolbar;
    private BottomSheetDialog infoBottomSheetDialog = null;
    private PopupMenu popupMenu = null;

    private FloatingActionButton menuFab = null;
    private FloatingActionButton favouriteFab = null;
    private FloatingActionButton shareFab = null;
    private FloatingActionButton deleteFab = null;
    private FloatingActionButton moreFab = null;

    private Animation rotateOpen;
    private Animation rotateClose;
    private Animation fromBotton;
    private Animation toBottom;
    //#endregion
    public final static int PESDK_RESULT = 6968;
    public final static int PICK_FOLDER_REQUEST_CODE = 9999;
    private boolean isChange = false;
    private Uri selectedImage = null;
    private MediaFile selectedMediaFile;

    private boolean menuFabClick = false;
    //#endregion

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_photo);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        // In Activity's onCreate() for instance
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window w = getWindow();
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }
        Init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint({"NonConstantResourceId", "UseCompatLoadingForDrawables", "RestrictedApi"})
    private void Init(){

        // Set up animation
        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        fromBotton = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);

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

        // Load data from previous intent
        Intent intent = getIntent();
        if(intent != null){
            selectedMediaFile = new MediaFile(
                    intent.getStringExtra(MediaFile.FILE_ID),
                    intent.getIntExtra(MediaFile.FILE_MEDIA_TYPE, MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                    intent.getStringExtra(MediaFile.FILE_PATH),
                    intent.getStringExtra(MediaFile.FILE_DATE),
                    intent.getStringExtra(MediaFile.FILE_SIZE),
                    intent.getStringExtra(MediaFile.FILE_RESOLUTION),
                    intent.getStringExtra(MediaFile.FILE_FOLDER_NAME),
                    intent.getBooleanExtra(MediaFile.FILE_FAVOURITE, false));
        }
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
        TextInputEditText infoFolderName = infoBottomSheetView.findViewById(R.id.info_folder_name_tv);
        infoFolderName.setText(selectedMediaFile.folderName);
        TextInputEditText infoFilePath = infoBottomSheetView.findViewById(R.id.info_file_path_tv);
        infoFilePath.setText(selectedMediaFile.fileUrl);
        infoBottomSheetDialog.setContentView(infoBottomSheetView);


        // Set up menu float action button
        menuFab = findViewById(R.id.fab_menu);
        menuFab.setOnClickListener(this::OnClickFabMenu);
        // Set up favourite fab
        favouriteFab = findViewById(R.id.favourite_fab);
        favouriteFab.setImageDrawable(selectedMediaFile.isFavourite?getDrawable(R.drawable.ic_baseline_favorite_32):getDrawable(R.drawable.ic_baseline_favorite_border_24));
        favouriteFab.setOnClickListener(this::OnClickFavorite);
        // Set up share fab
        shareFab = findViewById(R.id.share_fab);
        shareFab.setOnClickListener(this::OnClickShare);
        // Set up delete fab
        deleteFab = findViewById(R.id.delete_fab);
        deleteFab.setOnClickListener(this::OnClickDelete);
        // Set up more fab
        moreFab = findViewById(R.id.more_fab);
        moreFab.setOnClickListener(this::OnClickMore);

        popupMenu = new PopupMenu(this, moreFab);
        popupMenu.inflate(R.menu.top_app_bar_menu_2);
        MenuBuilder menu = (MenuBuilder) popupMenu.getMenu();
        menu.setOptionalIconsVisible(true);
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.info:
                    Log.d("Nothing", "Info Click");
                    OnClickInfoDetail();
                    return true;
                case R.id.copy:
                    Log.d("Nothing", "Copy Click");
                    OnClickCopy();
                    return true;
                case R.id.set_picture_as:
                    Log.d("Nothing", "Set Picture As Click");
                    OnClickSetPictureAs();
                    return true;
                default:
                    return false;
            }
        });
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
        if(bottomToolbar.getVisibility() == View.VISIBLE){
            bottomToolbar.setVisibility(View.GONE);
        }else {
            bottomToolbar.setVisibility(View.VISIBLE);
        }
    }
    private void OnClickMore(View view){
        popupMenu.show();
    }
    private void OnClickFabMenu(View view){
        menuFabClick = !menuFabClick;
        if(menuFabClick){
            favouriteFab.setVisibility(View.VISIBLE);
            shareFab.setVisibility(View.VISIBLE);
            deleteFab.setVisibility(View.VISIBLE);
            moreFab.setVisibility(View.VISIBLE);

            menuFab.startAnimation(rotateOpen);
            favouriteFab.startAnimation(fromBotton);
            shareFab.startAnimation(fromBotton);
            deleteFab.startAnimation(fromBotton);
            moreFab.startAnimation(fromBotton);

            favouriteFab.setClickable(true);
            shareFab.setClickable(true);
            deleteFab.setClickable(true);
            moreFab.setClickable(true);
        }else {
            favouriteFab.setVisibility(View.INVISIBLE);
            shareFab.setVisibility(View.INVISIBLE);
            deleteFab.setVisibility(View.INVISIBLE);
            moreFab.setVisibility(View.INVISIBLE);

            menuFab.startAnimation(rotateClose);
            favouriteFab.startAnimation(toBottom);
            shareFab.startAnimation(toBottom);
            deleteFab.startAnimation(toBottom);
            moreFab.startAnimation(toBottom);

            favouriteFab.setClickable(false);
            shareFab.setClickable(false);
            deleteFab.setClickable(false);
            moreFab.setClickable(false);
        }
    }
    private void OnClickDelete(View view){
        MaterialAlertDialogBuilder deleteAlert = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.title_delete_confirm)
                .setMessage(R.string.sub_text_delete_confirm)
                .setNegativeButton(R.string.cancel_btn, (dialog, which) -> {
                    Snackbar.make(findViewById(R.id.constraintLayout), R.string.title_delete_cancel, Snackbar.LENGTH_SHORT)
                            .show();
                })
                .setPositiveButton(R.string.delete_btn, (dialog, which) -> {
                    if(MediaFile.DeleteMediaFile(this, selectedMediaFile.fileUrl)){
                        isChange = true;
                        onBackPressed();
                    }
                });
        deleteAlert.show();
    }
    private void OnClickShare(View view){
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
        Intent intent = new Intent(this, PickFolderActivity.class);
        startActivityForResult(intent, PICK_FOLDER_REQUEST_CODE);
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
        isChange = true;

    }
    private void OnClickRotateRight(){
        Log.d("Nothing", "Rotate Right Click");
        Bitmap selectedImageBitmap = MediaFile.GetBitMap(selectedMediaFile.fileUrl);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        selectedImageBitmap = Bitmap.createBitmap(selectedImageBitmap, 0, 0, selectedImageBitmap.getWidth(), selectedImageBitmap.getHeight(), matrix, true);
        MediaFile.UpdateImage(selectedMediaFile.fileUrl, selectedImageBitmap);
        DisplayImage(selectedImageBitmap);
        isChange = true;
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void OnClickFavorite(View view){
        Log.d("Nothing", "Favourite Click");
        if(selectedMediaFile.isFavourite){
            Snackbar.make(findViewById(R.id.constraintLayout), R.string.title_remove_from_favourite, Snackbar.LENGTH_SHORT)
                    .show();
            DataHandler.RemoveFromFavourite(this, selectedMediaFile.id);
        }else{
            DataHandler.AddToFavourite(this, selectedMediaFile.id);
            Snackbar.make(findViewById(R.id.constraintLayout), R.string.title_add_to_favourite, Snackbar.LENGTH_SHORT)
                    .show();
        }
        selectedMediaFile.isFavourite = !selectedMediaFile.isFavourite;
        favouriteFab.setImageDrawable(selectedMediaFile.isFavourite?getDrawable(R.drawable.ic_baseline_favorite_32):getDrawable(R.drawable.ic_baseline_favorite_border_24));
        isChange = true;
    }
    public void OnClickEditPhoto(){
        OpenEditor(selectedImage);
    }
    //#endregion



    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case PESDK_RESULT:
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
                            isChange = true;
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case PICK_FOLDER_REQUEST_CODE:
                    if(intent != null){
                        String folderName = intent.getStringExtra(MediaFile.FILE_FOLDER_NAME);
                        if(MediaFile.SaveImage(this, MediaFile.GetBitMap(selectedMediaFile.fileUrl), folderName)){
                            isChange = true;
                            Log.d("Nothing","Copy and save image successfully");
                        }

                    }
                    break;
                default:
                    break;
            }
        }
    }


    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("CHANGE", isChange);
        setResult(Activity.RESULT_OK, returnIntent);
        super.onBackPressed();
    }
}