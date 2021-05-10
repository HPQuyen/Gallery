package com.example.galleryapplication.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.example.galleryapplication.R;
import com.example.galleryapplication.activities.GalleryViewActivity;
import com.example.galleryapplication.classes.DataHandler;
import com.example.galleryapplication.classes.MediaFile;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private List<String> albums;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder_Grid).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ConstraintLayout constraintLayout;

        private final ImageView albumImage;
        private final TextView albumName;
        private final TextView albumMembersNumber;

        public ViewHolder(View view) {
            super(view);

            this.constraintLayout = view.findViewById(R.id.albumItemConstraintLayout);

            this.albumImage = view.findViewById(R.id.imageView);
            this.albumName = view.findViewById(R.id.albumName);
            this.albumMembersNumber = view.findViewById(R.id.albumMembersNumber);
        }

        public ConstraintLayout getConstraintLayout() {
            return constraintLayout;
        }

        public ImageView getAlbumImage() {
            return this.albumImage;
        }

        public TextView getAlbumName() {
            return this.albumName;
        }

        public TextView getAlbumMembersNumber() {
            return this.albumMembersNumber;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     */
    public AlbumAdapter(Context context, List<String> albumList) {
        this.context = context;
        this.albums = albumList;
    }

    public void UpdateNewListAlbum(List<String> albumList){
        this.albums = albumList;
    }

    // Create new views (invoked by the layout manager)
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View dateView =
                inflater.inflate(R.layout.fragment_album_item, parent, false);
        return new ViewHolder(dateView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).getAlbumName().setText(this.albums.get(position));

        // Get photo
        ArrayList<MediaFile> mediaFiles = DataHandler.GetMediaFileByAlbum(
                this.context,
                this.albums.get(position),
                DataHandler.ONE
        );

        assert mediaFiles != null;
        Glide
                .with(context)
                .load(mediaFiles.get(0).fileUrl)
                .signature(new ObjectKey(System.currentTimeMillis()))
                .into(((ViewHolder) holder).getAlbumImage());

        // Get photos
        mediaFiles = DataHandler.GetMediaFileByAlbum(
                this.context,
                this.albums.get(position),
                DataHandler.ALL
        );

        assert mediaFiles != null;
        ((ViewHolder) holder).getAlbumMembersNumber().setText(mediaFiles.size() + " photo(s)");

        ((ViewHolder) holder).getConstraintLayout().setOnClickListener(v ->
                ((GalleryViewActivity) this.context).TransitionAlbumDetail(
                        this.albums.get(position)
                )
        );
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.albums.size();
    }
}

