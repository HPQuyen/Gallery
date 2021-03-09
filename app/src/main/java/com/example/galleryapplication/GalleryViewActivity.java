package com.example.galleryapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class GalleryViewActivity extends AppCompatActivity {

    private ActionBar mainActionbar;
    private Menu optionsMenuActionBar;

    private _LAYOUT mainLayout = _LAYOUT._GRID;
    private _VIEW mainView = _VIEW._ALL;

    private Fragment viewAllGridFragment;
    private Fragment viewAllDateFragment;
    private Fragment viewAllDetailsFragment;

    private Fragment albumFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galleryviewactivity_main);

        mainActionbar = getSupportActionBar();

        mainActionbar.setDisplayShowTitleEnabled(true);

        viewAllGridFragment = new ViewAllGridFragment();
        viewAllDateFragment = new ViewAllDateFragment();
        viewAllDetailsFragment = new ViewAllDetailsFragment();

        albumFragment = new AlbumFragment();

        setCurrentFragment(viewAllGridFragment);

        BottomNavigationView bottomNavBar = findViewById(R.id.BottomNavigationView);
        bottomNavBar.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.ViewAll_Frag_Items:
                                mainView = _VIEW._ALL;
                                invalidateOptionsMenu();

                                switch (mainLayout) {
                                    case _GRID:
                                        setCurrentFragment(viewAllGridFragment);
                                        break;
                                    case _DATE:
                                        setCurrentFragment(viewAllDateFragment);
                                        break;
                                    case __DETAILS:
                                        setCurrentFragment(viewAllDetailsFragment);
                                        break;
                                }

                                mainActionbar.setTitle("Photos & Videos");
                                break;
                            case R.id.Album_Frag_Items:
                                mainView = _VIEW._ALBUMS;
                                invalidateOptionsMenu();

                                setCurrentFragment(albumFragment);

                                mainActionbar.setTitle("Albums");
                                break;
                        }
                        return true;
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (optionsMenuActionBar == null) optionsMenuActionBar = menu;

        MenuInflater inflater = getMenuInflater();

        switch (mainView) {
            case _ALL:
                inflater.inflate(R.menu.actionbar_viewall_menu, menu);
                return true;
            case _ALBUMS:
                inflater.inflate(R.menu.actionbar_album_menu, menu);
                return true;
        }

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.GridDate_ViewAll:
                if (mainLayout == _LAYOUT._DATE) break;
                mainLayout = _LAYOUT._DATE;

                setCurrentFragment(viewAllDateFragment);

                return true;

            case R.id.GridOnly_ViewAll:
                if (mainLayout == _LAYOUT._GRID) break;
                mainLayout = _LAYOUT._GRID;

                setCurrentFragment(viewAllGridFragment);

                return true;

            case R.id.DetailsList_ViewAll:
                if (mainLayout == _LAYOUT.__DETAILS) break;
                mainLayout = _LAYOUT.__DETAILS;

                setCurrentFragment(viewAllDetailsFragment);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

        return false;
    }

    private void setCurrentFragment (Fragment fragment) {
        FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.FrameLayout_Fragment, fragment);
        fragTransaction.commit();
    }

    public void settings(MenuItem item) {
        Intent intent = new Intent(GalleryViewActivity.this, SettingsActivity.class);
        startActivity(intent);
    }
}
