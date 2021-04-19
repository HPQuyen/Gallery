package com.example.galleryapplication.fragments.mainviews;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.galleryapplication.R;
import com.example.galleryapplication.interfaces.IOnBackPressed;

public class ViewAllDateFragment extends Fragment implements IOnBackPressed {

    public ViewAllDateFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_viewall_date, container, false);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}