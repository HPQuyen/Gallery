package com.example.galleryapplication.fragments.subviews.album;

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
import com.example.galleryapplication.adapters.MediaFileAdapter;
import com.example.galleryapplication.classes.DataHandler;
import com.example.galleryapplication.classes.MediaFile;
import com.example.galleryapplication.classes.Observer;
import com.example.galleryapplication.enumerators.VIEW_DETAIL_MODE;
import com.example.galleryapplication.enumerators._LAYOUT;
import com.example.galleryapplication.interfaces.IOnBackPressed;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AlbumDetailDetailsFragment extends Fragment implements IOnBackPressed {

    private RecyclerView recyclerView;

    public AlbumDetailDetailsFragment() {

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
                R.layout.fragment_viewall_details, container, false
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        this.recyclerView = view.findViewById(R.id.recyclerView);
        loadAllAlbumImages(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void loadAllAlbumImages(View thisView) {
        String albumName = ((AlbumDetailActivity) thisView.getContext()).getAlbumName();

        ArrayList<MediaFile> mediaFiles = DataHandler.GetMediaFileByAlbum(
                thisView.getContext(),
                albumName,
                DataHandler.ALL
        );

        //Observer.SubscribeCurrentMediaFiles(mediaFiles);
        MediaFileAdapter mediaFileAdapter =
                new MediaFileAdapter(
                        thisView.getContext(),
                        mediaFiles,
                        _LAYOUT._DETAILS,
                        VIEW_DETAIL_MODE.NORMAL
                );

        Observer.AddEventListener(
                Observer.ObserverCode.TRIGGER_ADAPTER_ALBUM_CHANGE,(newAlbumName) -> {
                    mediaFileAdapter.UpdateNewListMediaFile(DataHandler.GetMediaFileByAlbum(
                            thisView.getContext(),
                            (String) newAlbumName,
                            DataHandler.ALL
                    ));
                    mediaFileAdapter.notifyDataSetChanged();
                });

        this.recyclerView.setAdapter(mediaFileAdapter);
        this.recyclerView.setLayoutManager(
                new LinearLayoutManager(
                        thisView.getContext(), RecyclerView.VERTICAL, false
                )
        );
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}