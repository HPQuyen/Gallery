package com.example.galleryapplication.adapters;

import android.content.Context;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.example.galleryapplication.R;
import com.example.galleryapplication.classes.MediaFile;
import com.example.galleryapplication.classes.Observer;
import com.example.galleryapplication.enumerators.SLIDER_MODE;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class SlideshowAdapter extends RecyclerView.Adapter<SlideshowAdapter.SliderViewHolder> {

    //#region Fields
    private Context context;
    private ViewPager2 viewPager;
    private List<MediaFile> listSliderMediaFile;
    private SLIDER_MODE sliderMode;
    //#endregion

    public SlideshowAdapter(Context context, List<MediaFile> listSliderMediaFile, ViewPager2 viewPager, SLIDER_MODE sliderMode){
        this.context = context;
        this.viewPager = viewPager;
        this.listSliderMediaFile = listSliderMediaFile;
        this.sliderMode = sliderMode;
    }

    public class SliderViewHolder extends RecyclerView.ViewHolder{

        private RoundedImageView roundedImageView;
        private FloatingActionButton imageOverlay;
        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            this.roundedImageView = itemView.findViewById(R.id.image_view_item);
            this.imageOverlay = itemView.findViewById(R.id.image_view_item_overlay);
        }

        public RoundedImageView GetRoundImageView(){ return roundedImageView; }

        public FloatingActionButton GetImageOverlay() { return imageOverlay; }
    }



    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(LayoutInflater.from(context).inflate(R.layout.image_view_item, parent, false));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        Log.d("Nothing", "slide show adapter bind position: " + position);
        MediaFile mediaFile = listSliderMediaFile.get(position);
        RoundedImageView roundedImageView = holder.GetRoundImageView();
        FloatingActionButton imageOverlay = holder.GetImageOverlay();
        Glide
                .with(this.context)
                .load(mediaFile.fileUrl)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .signature(new ObjectKey(mediaFile.lastTimeModified)))
                .into(roundedImageView);

        if(sliderMode == SLIDER_MODE.SLIDESHOW)
            return;
        if (mediaFile.mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
            imageOverlay.setVisibility(View.VISIBLE);
            imageOverlay.setOnClickListener(v -> {
                Observer.Invoke(Observer.ObserverCode.TRIGGER_OPEN_VIDEO);
            });
        } else {
            imageOverlay.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listSliderMediaFile.size();
    }

}
