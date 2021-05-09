package com.example.galleryapplication.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
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
import com.example.galleryapplication.classes.MediaFile;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;
public class FolderAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final List<String> listFolder;
    private final List<String> listImageCoverUrl = new ArrayList<>();
    private int position = -1;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public FolderAdapter(@NonNull Context context, @NonNull List<String> objects) {
        super(context, R.layout.folder_card_item, objects);
        this.listFolder = new ArrayList<>();
        this.context = context;

        for (int i = 0; i < objects.size(); i++) {
            ArrayList<MediaFile> mediaFiles = DataHandler.GetMediaFilesByFolder(context, objects.get(i), DataHandler.ONE);
            if(mediaFiles != null){
                listFolder.add(objects.get(i));
                listImageCoverUrl.add(mediaFiles.get(0).fileUrl);
            }
        }
    }

    @Override
    public int getCount() {
        return listFolder.size();
    }

    @Override
    public String getItem(int position) {
        return listFolder.get(position);
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
        convertView = layoutInflater.inflate(R.layout.folder_card_item, parent, false);

        // Set event for click card
        MaterialCardView cardView = convertView.findViewById(R.id.card);
        cardView.setChecked(position == this.position);

        cardView.setOnClickListener(view -> {
            Log.d("Nothing", "prev position " + this.position);
            Log.d("Nothing", "current position " + position);
            if(cardView.isChecked())
                return;
            cardView.setChecked(true);
            this.position = position;
            this.notifyDataSetChanged();
        });

        Chip folderName = convertView.findViewById(R.id.album_name_chip);
        folderName.setText(listFolder.get(position));
        ImageView imageView = convertView.findViewById(R.id.image_card);
        Glide
                .with(context)
                .load(listImageCoverUrl.get(position))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
        return convertView;
    }

    public String GetAlbumPicked(){
        return position == -1 ? null : listFolder.get(this.position);
    }
}
