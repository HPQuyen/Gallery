package com.example.galleryapplication.adapters;

import android.content.Context;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.galleryapplication.R;
import com.example.galleryapplication.classes.MediaFile;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;

public class PhotoSelectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;

    private final ArrayList<MediaFile> mediaFiles;
    private final HashSet<String> selectedMediaFiles;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder_Grid).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ConstraintLayout constraintLayout;

        private final ImageView photoSelectImage;
        private final ImageView photoSelectOverlay;
        private final CheckBox photoSelectCheckbox;

        public ViewHolder(View view) {
            super(view);

            this.constraintLayout = view.findViewById(R.id.itemGridSelectPhotosConstraintLayout);

            this.photoSelectImage = view.findViewById(R.id.itemGridSelectPhotosImageView);
            this.photoSelectOverlay = view.findViewById(R.id.itemGridSelectPhotosOverlay);
            this.photoSelectCheckbox = view.findViewById(R.id.itemGridSelectPhotosCheckbox);
        }

        public ConstraintLayout getConstraintLayout() {
            return constraintLayout;
        }

        public ImageView getPhotoSelectImage() {
            return this.photoSelectImage;
        }

        public ImageView getPhotoSelectOverlay() {
            return this.photoSelectOverlay;
        }

        public CheckBox getPhotoSelectCheckbox() {
            return this.photoSelectCheckbox;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     */
    public PhotoSelectAdapter(
            Context context, ArrayList<MediaFile> mediaFiles, ArrayList<String> selectedMediaFiles
    ) {
        this.context = context;

        this.mediaFiles = mediaFiles;
        this.selectedMediaFiles = new HashSet<>(selectedMediaFiles);
    }

    // Create new views (invoked by the layout manager)
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View view =
                inflater.inflate(R.layout.activity_photo_select_item, parent, false);
        return new PhotoSelectAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MediaFile mediaFile = this.mediaFiles.get(position);

        Glide
                .with(this.context)
                .load(mediaFile.fileUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(((ViewHolder) holder).getPhotoSelectImage());

        if (mediaFile.mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
            ((ViewHolder) holder).getPhotoSelectOverlay().setVisibility(View.VISIBLE);
            Glide
                    .with(this.context)
                    .load(R.drawable.ic_play_video)
                    .into(((ViewHolder) holder).getPhotoSelectOverlay());
        } else {
            ((ViewHolder) holder).getPhotoSelectOverlay().setVisibility(View.GONE);
        }

        ((ViewHolder) holder).getPhotoSelectCheckbox()
                .setChecked(selectedMediaFiles.contains(mediaFile.id));

        ((ViewHolder) holder).getConstraintLayout().setOnClickListener(
                v -> {
                    if (((ViewHolder) holder).getPhotoSelectCheckbox().isChecked()) {
                        ((ViewHolder) holder).getPhotoSelectCheckbox().setChecked(false);

                        selectedMediaFiles.remove(mediaFile.id);
                    } else {
                        ((ViewHolder) holder).getPhotoSelectCheckbox().setChecked(true);

                        selectedMediaFiles.add(mediaFile.id);
                    }
                }
        );

        ((ViewHolder) holder).getPhotoSelectCheckbox().setOnClickListener(
                v -> {
                    if (((ViewHolder) holder).getPhotoSelectCheckbox().isChecked()) {
                        selectedMediaFiles.add(mediaFile.id);
                    } else {
                        selectedMediaFiles.remove(mediaFile.id);
                    }

                    for (String string : this.selectedMediaFiles) {
                        Log.d("CHECKBOXES", string);
                    }
                    Log.d("CHECKBOXES", "------------------------");
                }
        );
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.mediaFiles.size();
    }

    public ArrayList<String> getSelectedPhotos() {
        return new ArrayList<>(this.selectedMediaFiles);
    }
}
