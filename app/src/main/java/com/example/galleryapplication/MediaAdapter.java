package com.example.galleryapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.List;

public class MediaAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final LayoutInflater layoutInflater;
    private final List<String> imgURLs;

    public MediaAdapter(@NonNull Context context, int resource, @NonNull List<String> imgURLs) {
        super(context, resource, imgURLs);

        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.imgURLs = imgURLs;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = this.layoutInflater.inflate(
                    R.layout.fragment_viewall_grid_item, parent, false
            );
        }

        Glide
                .with(this.context)
                .load(imgURLs.get(position))
                .into((ImageView) convertView);

        return convertView;
    }
}
