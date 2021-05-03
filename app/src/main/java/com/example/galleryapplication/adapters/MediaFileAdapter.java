package com.example.galleryapplication.adapters;

import android.content.Context;
import android.os.Build;
import android.provider.MediaStore;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.galleryapplication.R;
import com.example.galleryapplication.activities.GalleryViewActivity;
import com.example.galleryapplication.classes.MediaFile;
import com.example.galleryapplication.enumerators._LAYOUT;
import com.example.galleryapplication.enumerators._VIEW;

import java.util.List;

public class MediaFileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final List<MediaFile> mediaFiles;

    private final _LAYOUT layout;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder_Grid).
     */
    public static class ViewHolder_Grid extends RecyclerView.ViewHolder {

        private final ConstraintLayout constraintLayout;

        private final ImageView imageView;
        private final ImageView imageOverlay;

        @RequiresApi(api = Build.VERSION_CODES.R)
        public ViewHolder_Grid(View view) {
            super(view);

            this.constraintLayout = view.findViewById(R.id.itemGridConstraintLayout);

            this.imageView = view.findViewById(R.id.itemGridImageView);
            this.imageOverlay = view.findViewById(R.id.itemGridOverlay);
        }

        public ConstraintLayout getConstraintLayout() {
            return constraintLayout;
        }

        public ImageView getImageView() {
            return this.imageView;
        }

        public ImageView getImageOverlay() {
            return this.imageOverlay;
        }
    }

    public static class ViewHolder_Details extends RecyclerView.ViewHolder {

        private final ConstraintLayout constraintLayout;

        private final ImageView imageView;
        private final ImageView imageOverlay;

        private final TextView mediaName;
        private final TextView mediaDate;
        private final TextView mediaSize;

        @RequiresApi(api = Build.VERSION_CODES.R)
        public ViewHolder_Details(View view) {
            super(view);

            this.constraintLayout = view.findViewById(R.id.itemDetailsConstraintLayout);

            this.imageView = view.findViewById(R.id.itemDetailsImageView);
            this.imageOverlay = view.findViewById(R.id.itemDetailsOverlay);

            this.mediaName = view.findViewById(R.id.mediaName);
            this.mediaDate = view.findViewById(R.id.mediaDate);
            this.mediaSize = view.findViewById(R.id.mediaSize);
        }

        public ConstraintLayout getConstraintLayout() {
            return this.constraintLayout;
        }

        public ImageView getImageView() {
            return this.imageView;
        }

        public ImageView getImageOverlay() {
            return this.imageOverlay;
        }

        public TextView getMediaName() {
            return this.mediaName;
        }

        public TextView getMediaDate() {
            return this.mediaDate;
        }

        public TextView getMediaSize() {
            return this.mediaSize;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     */
    public MediaFileAdapter(Context context, List<MediaFile> mediaFiles, _LAYOUT layout) {
        this.context = context;
        this.mediaFiles = mediaFiles;
        this.layout = layout;
    }

    // Create new views (invoked by the layout manager)
    @RequiresApi(api = Build.VERSION_CODES.R)
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);

        switch (viewType) {
            case 2:
                View listView =
                        inflater.inflate(R.layout.fragment_viewall_details_item, parent, false);

                return new ViewHolder_Details(listView);
            case 0:
            default:
                View gridView =
                        inflater.inflate(R.layout.fragment_viewall_grid_item, parent, false);

                return new ViewHolder_Grid(gridView);
        }

    }

    @Override
    public int getItemViewType(int position) {
        switch (this.layout) {
            case _DATE:
                return 1;
            case _DETAILS:
                return 2;
            case _GRID:
            default:
                return 0;
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final int viewType = getItemViewType(position);

        switch (viewType) {
            case 0:
                MediaFile mediaFile = this.mediaFiles.get(position);

                Glide
                        .with(this.context)
                        .load(mediaFile.fileUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(((ViewHolder_Grid)holder).getImageView());

                if (mediaFile.mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                    ((ViewHolder_Grid)holder).getImageOverlay().setVisibility(View.VISIBLE);
                    Glide
                            .with(this.context)
                            .load(R.drawable.ic_play_video)
                            .into(((ViewHolder_Grid)holder).getImageOverlay());
                } else {
                    ((ViewHolder_Grid)holder).getImageOverlay().setVisibility(View.GONE);
                }

                ((ViewHolder_Grid)holder).getConstraintLayout().setOnClickListener(
                        v -> ((GalleryViewActivity) this.context
                        ).TransitionViewDetail(mediaFile));

                break;
            case 1:
            case 2:
                mediaFile = this.mediaFiles.get(position);

                Glide
                        .with(this.context)
                        .load(mediaFile.fileUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(((ViewHolder_Details)holder).getImageView());

                if (mediaFile.mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                    ((ViewHolder_Details)holder).getImageOverlay().setVisibility(View.VISIBLE);
                    Glide
                            .with(this.context)
                            .load(R.drawable.ic_play_video)
                            .into(((ViewHolder_Details)holder).getImageOverlay());
                } else {
                    ((ViewHolder_Details)holder).getImageOverlay().setVisibility(View.GONE);
                }

                ((ViewHolder_Details)holder).getMediaName().setText(mediaFile.fileUrl);
                ((ViewHolder_Details)holder).getMediaDate().setText(mediaFile.datetime);
                ((ViewHolder_Details)holder).getMediaSize().setText(mediaFile.fileSize);

                ((ViewHolder_Details)holder).getConstraintLayout().setOnClickListener(
                        v -> ((GalleryViewActivity) this.context
                        ).TransitionViewDetail(mediaFile));

                break;
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return this.mediaFiles.size();
    }

}
