package com.example.galleryapplication.adapters;

import android.content.Context;
import android.os.Build;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.galleryapplication.R;
import com.example.galleryapplication.activities.GalleryViewActivity;
import com.example.galleryapplication.classes.MediaFile;

import java.util.List;

public class MediaGridAdapter extends RecyclerView.Adapter<MediaGridAdapter.ViewHolder> {

    private final Context context;
    private final List<MediaFile> mediaFiles;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final ImageView imageOverlay;

        @RequiresApi(api = Build.VERSION_CODES.R)
        public ViewHolder(View view) {
            super(view);

            this.imageView = view.findViewById(R.id.itemGridImageView);
            this.imageOverlay = view.findViewById(R.id.itemGridOverlay);
        }

        public ImageView getImageView() {
            return this.imageView;
        }

        public ImageView getImageOverlay() {
            return this.imageOverlay;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     */
    public MediaGridAdapter(Context context, List<MediaFile> mediaFiles) {
        this.context = context;
        this.mediaFiles = mediaFiles;
    }

    // Create new views (invoked by the layout manager)
    @RequiresApi(api = Build.VERSION_CODES.R)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View imageView =
                inflater.inflate(R.layout.fragment_viewall_grid_item, parent, false);

        return new ViewHolder(imageView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MediaFile mediaFile = this.mediaFiles.get(position);

        Glide
                .with(this.context)
                .load(mediaFile.fileUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.getImageView());

        if (mediaFile.mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
            holder.getImageOverlay().setVisibility(View.VISIBLE);
            Glide
                    .with(this.context)
                    .load(R.drawable.ic_play_video)
                    .into(holder.getImageOverlay());
        } else {
            holder.getImageOverlay().setVisibility(View.GONE);
        }

        holder.getImageView().setOnClickListener(v -> {
            ((GalleryViewActivity) this.context)
                    .TransitionViewDetail(mediaFile);
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.mediaFiles.size();
    }


}
