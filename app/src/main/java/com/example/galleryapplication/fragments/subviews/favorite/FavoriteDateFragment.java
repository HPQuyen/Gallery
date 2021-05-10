package com.example.galleryapplication.fragments.subviews.favorite;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galleryapplication.R;
import com.example.galleryapplication.activities.AlbumDetailActivity;
import com.example.galleryapplication.adapters.DateGridMediaFileAdapter;
import com.example.galleryapplication.classes.DataHandler;
import com.example.galleryapplication.classes.Observer;
import com.example.galleryapplication.enumerators._VIEW;
import com.example.galleryapplication.interfaces.IOnBackPressed;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FavoriteDateFragment extends Fragment implements IOnBackPressed {

    private RecyclerView recyclerView;

    public FavoriteDateFragment() {

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

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        this.recyclerView = view.findViewById(R.id.parentDateRecyclerView);
        loadAllAlbumImages(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void loadAllAlbumImages(View thisView) {
        ArrayList<String> listDate = DataHandler.GetDateByFavourite();

        DateGridMediaFileAdapter dateGridMediaFileAdapter =
                new DateGridMediaFileAdapter(
                        thisView.getContext(),
                        listDate,
                        null,
                        _VIEW._FAVORITE
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