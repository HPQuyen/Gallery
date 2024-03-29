package com.example.galleryapplication.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galleryapplication.R;
import com.example.galleryapplication.classes.DataHandler;
import com.example.galleryapplication.classes.MediaFile;
import com.example.galleryapplication.classes.Observer;
import com.example.galleryapplication.enumerators.VIEW_DETAIL_MODE;
import com.example.galleryapplication.enumerators._LAYOUT;
import com.example.galleryapplication.enumerators._VIEW;

import java.util.ArrayList;
import java.util.List;

import ly.img.android.events.$EventCall_BrushSettings_STATE_REVERTED;

public class DateGridMediaFileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private List<String> dateList;
    private final String albumName;
    private final _VIEW mode;


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder_Grid).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ConstraintLayout constraintLayout;

        private final TextView textView;
        private final RecyclerView recyclerView;

        public ViewHolder(View view) {
            super(view);

            this.constraintLayout = view.findViewById(R.id.viewall_date_parents_ConstraintLayout);

            this.textView = view.findViewById(R.id.dateCategories);
            this.recyclerView = view.findViewById(R.id.childDateRecyclerView);
        }

        public ConstraintLayout getConstraintLayout() {
            return constraintLayout;
        }

        public TextView getTextView() {
            return this.textView;
        }

        public RecyclerView getRecyclerView() {
            return this.recyclerView;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     */
    public DateGridMediaFileAdapter(
            Context context,
            List<String> dateList,
            String albumName,
            _VIEW mode
    ) {
        this.context = context;
        this.dateList = dateList;
        this.albumName = albumName;
        this.mode = mode;
    }

    public void UpdateNewListDate(ArrayList<String> dateList){
        this.dateList = dateList;
    }

    // Create new views (invoked by the layout manager)
    @RequiresApi(api = Build.VERSION_CODES.R)
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View dateView =
                inflater.inflate(R.layout.fragment_viewall_date_item_parents, parent, false);
        return new ViewHolder(dateView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String date = this.dateList.get(position);

        ((ViewHolder) holder).getTextView().setText(date);

        ArrayList<MediaFile> mediaFiles;
        switch (mode) {
            case _ALL:
                mediaFiles =  DataHandler.GetMediaFilesByDate(this.context, date);
                break;
            case _ALBUMS:
                mediaFiles = DataHandler.GetMediaFilesByAlbumDate(this.context, albumName, date);
                break;
            case _FAVORITE:
                mediaFiles =  DataHandler.GetMediaFileByFavouriteDate(date);
                break;
            default:
                mediaFiles = new ArrayList<>();
                break;
        }


        MediaFileAdapter mediaFileAdapter =
                new MediaFileAdapter(
                        ((ViewHolder) holder).getRecyclerView().getContext(),
                        mediaFiles,
                        _LAYOUT._DATE,
                        VIEW_DETAIL_MODE.NORMAL
                );

        switch(mode){
            case _ALL:
                Observer.AddEventListener(
                        Observer.ObserverCode.TRIGGER_ADAPTER_CHANGE, () -> {
                            mediaFileAdapter.UpdateNewListMediaFile(DataHandler.GetMediaFilesByDate(this.context, date));
                            mediaFileAdapter.notifyDataSetChanged();
                        });
                break;
            case _ALBUMS:
                Observer.AddEventListener(
                        Observer.ObserverCode.TRIGGER_ADAPTER_ALBUM_CHANGE, () -> {
                            mediaFileAdapter.UpdateNewListMediaFile(DataHandler.GetMediaFilesByAlbumDate(this.context, albumName, date));
                            mediaFileAdapter.notifyDataSetChanged();
                        });
                break;
            case _FAVORITE:
                Observer.AddEventListener(
                        Observer.ObserverCode.TRIGGER_ADAPTER_FAVOURITE_CHANGE, () -> {
                            mediaFileAdapter.UpdateNewListMediaFile(DataHandler.GetMediaFileByFavouriteDate(date));
                            mediaFileAdapter.notifyDataSetChanged();
                        });

                break;
        };


        ((ViewHolder) holder).getRecyclerView().setAdapter(mediaFileAdapter);
        ((ViewHolder) holder).getRecyclerView().setLayoutManager(
                new GridLayoutManager(((ViewHolder) holder).getRecyclerView().getContext(), 4)
        );
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.dateList.size();
    }

}
