package com.example.galleryapplication.fragments.mainviews;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galleryapplication.R;
import com.example.galleryapplication.adapters.AlbumAdapter;
import com.example.galleryapplication.classes.DataHandler;
import com.example.galleryapplication.classes.Observer;
import com.example.galleryapplication.interfaces.IOnBackPressed;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AlbumFragment extends Fragment implements IOnBackPressed {

    private RecyclerView recyclerView;

    public AlbumFragment() {
        // Required empty public constructor
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
                R.layout.fragment_album, container, false
        );
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        this.recyclerView = view.findViewById(R.id.albumRecyclerView);
        loadAllAlbum(view);
    }

    private void loadAllAlbum(View thisView) {
        ArrayList<String> albums = DataHandler.GetListAlbumName();

        if (albums != null) {
            AlbumAdapter albumAdapter =
                    new AlbumAdapter(
                            thisView.getContext(),
                            albums
                    );

            Observer.AddEventListener(
                    Observer.ObserverCode.TRIGGER_ADAPTER_CHANGE, () -> {
                        albumAdapter.SetNewListAlbum(DataHandler.GetListAlbumName());
                        albumAdapter.notifyDataSetChanged();
                    }
            );

            this.recyclerView.setAdapter(albumAdapter);
            this.recyclerView.setLayoutManager(
                    new GridLayoutManager(
                            thisView.getContext(), 2
                    )
            );
        }
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}