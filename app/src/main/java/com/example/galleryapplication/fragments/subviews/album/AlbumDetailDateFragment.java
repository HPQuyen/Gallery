package com.example.galleryapplication.fragments.subviews.album;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galleryapplication.R;
import com.example.galleryapplication.adapters.DateGridMediaFileAdapter;
import com.example.galleryapplication.classes.DataHandler;
import com.example.galleryapplication.classes.Observer;
import com.example.galleryapplication.interfaces.IOnBackPressed;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AlbumDetailDateFragment extends Fragment implements IOnBackPressed {

    private RecyclerView recyclerView;

    public AlbumDetailDateFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(
                R.layout.fragment_viewall_date, container, false
        );
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        this.recyclerView = view.findViewById(R.id.parentDateRecyclerView);
        loadAllAlbumImages(view);
    }

    private void loadAllAlbumImages(View thisView) {
        ArrayList<String> listDate = DataHandler.GetListDate();

        DateGridMediaFileAdapter dateGridMediaFileAdapter =
                new DateGridMediaFileAdapter(
                        thisView.getContext(),
                        listDate
                );

        Observer.AddEventListener(
                Observer.ObserverCode.TRIGGER_ADAPTER_CHANGE,
                dateGridMediaFileAdapter::notifyDataSetChanged
        );

        this.recyclerView.setAdapter(dateGridMediaFileAdapter);
        this.recyclerView.setLayoutManager(
                new LinearLayoutManager(
                        thisView.getContext(), LinearLayoutManager.VERTICAL, false
                )
        );
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}