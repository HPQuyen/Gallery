package com.example.galleryapplication.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.galleryapplication.R;
import com.example.galleryapplication.classes.DataHandler;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;
public class FolderAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final List<String> listAlbum;
    private MaterialCardView prevCardView;
    private final List<String> listImageCoverUrl = new ArrayList<>();
    private int position = -1;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public FolderAdapter(@NonNull Context context, @NonNull List<String> objects) {
        super(context, R.layout.album_card_item, objects);
        this.listAlbum = objects;
        this.context = context;
        for (String album : listAlbum) {
            listImageCoverUrl.add(DataHandler.GetMediaFilesByFolder(context, album, DataHandler.ONE).get(0).fileUrl);
        }
    }

    @Override
    public int getCount() {
        return listAlbum.size();
    }

    @Override
    public String getItem(int position) {
        return listAlbum.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(R.layout.album_card_item, parent, false);

        // Set event for click card
        MaterialCardView cardView = convertView.findViewById(R.id.card);
        cardView.setOnClickListener(view -> {
            if(cardView.isChecked())
                return;
            cardView.setChecked(true);
            if(prevCardView != null)
                prevCardView.setChecked(false);
            prevCardView = cardView;
            this.position = position;
        });

        Chip albumName = convertView.findViewById(R.id.album_name_chip);
        albumName.setText(listAlbum.get(position));
        ImageView imageView = convertView.findViewById(R.id.image_card);
        Glide
                .with(context)
                .load(listImageCoverUrl.get(position))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
        return convertView;
    }

    public String GetAlbumPicked(){
        return position == -1 ? null : listAlbum.get(this.position);
    }
}
