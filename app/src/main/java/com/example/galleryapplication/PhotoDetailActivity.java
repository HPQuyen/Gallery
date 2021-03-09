package com.example.galleryapplication;



import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;


import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

public class PhotoDetailActivity extends AppCompatActivity {

    PhotoEditor mPhotoEditor = null;
    ConstraintLayout topToolbarLayout,bottomToolbarLayout;
    PopupMenu popupMenu = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TurnOffTitle();
        setContentView(R.layout.activity_photo_detail);
        Start();
    }
    private void Start(){
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
                    InfoDetail();
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

        PhotoEditorView mPhotoEditorView = findViewById(R.id.photoEditorView);
        mPhotoEditorView.getSource().setImageResource(R.drawable._28195615_136128921308288_9046592716708631099_n);
        // Load text font
        Typeface mTextRobotoTf = ResourcesCompat.getFont(this, R.font.roboto_medium);
        // Load emoji font
        //Typeface mEmojiTypeFace = Typeface.createFromAsset(getAssets(), "emojione-android.ttf");
        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true)
                .setDefaultTextTypeface(mTextRobotoTf)
                .setDefaultEmojiTypeface(null)
                .build();

        topToolbarLayout = findViewById(R.id.top_toolbar_layout);
        bottomToolbarLayout = findViewById(R.id.bottom_toolbar_layout);

        mPhotoEditorView.getSource().setOnClickListener(this::ToggleToolbars);
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



    private void InfoDetail(){
        if(getSupportFragmentManager().findFragmentById(R.id.info_detail_fragment_view) == null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().setReorderingAllowed(true).add(R.id.info_detail_fragment_view, InfoDetail.newInstance("0","2"), null);
            fragmentTransaction.commit();
        }
    }



    //#region On Click Event
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
    //#endregion
}