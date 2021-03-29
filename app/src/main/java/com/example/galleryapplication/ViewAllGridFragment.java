package com.example.galleryapplication;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewAllGridFragment extends Fragment {

    private GridView gridView;

    public ViewAllGridFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(
                R.layout.fragment_viewall_grid, container, false
        );

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        gridView = requireView().findViewById(R.id.viewAll_Grid_GridView);
        loadAllImage(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void loadAllImage(View thisView) {
        HashMap<String, ArrayList<String>> dictMediaFiles =
                ((GalleryViewActivity)requireActivity()).getMediaCollections();

        ArrayList<String> mediaEntries = new ArrayList<>();
        for (Map.Entry<String, ArrayList<String>> item : dictMediaFiles.entrySet()) {
            mediaEntries.addAll(item.getValue());
        }

        gridView.setAdapter(new MediaAdapter(
                thisView.getContext(),
                R.layout.fragment_viewall_grid_item,
                mediaEntries
        ));

        gridView.setOnItemClickListener(
                (parent, view, position, id) -> {
                    CharSequence realMsg = "Image " + id + "is clicked";
                    Toast.makeText(view.getContext(), realMsg, Toast.LENGTH_SHORT).show();
                }
        );
    }
}