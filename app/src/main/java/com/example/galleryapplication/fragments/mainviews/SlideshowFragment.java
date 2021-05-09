package com.example.galleryapplication.fragments.mainviews;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.galleryapplication.R;
import com.example.galleryapplication.activities.GalleryViewActivity;
import com.example.galleryapplication.adapters.SlideshowAdapter;
import com.example.galleryapplication.classes.MediaFile;
import com.example.galleryapplication.classes.Observer;
import com.example.galleryapplication.enumerators.SLIDER_MODE;
import com.example.galleryapplication.interfaces.IOnBackPressed;
import java.util.ArrayList;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SlideshowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SlideshowFragment extends Fragment implements IOnBackPressed {

    private static ArrayList<MediaFile> mediaFileSlideShow;
    private static final int TRANSITION_TIME_MILLIS = 2000;
    private ViewPager2 viewPager2;
    private Handler sliderHandler = new Handler();
    private static Techniques [] animation = new Techniques[]{Techniques.FadeInUp, Techniques.FadeInDown, Techniques.BounceInLeft, Techniques.FlipInX};
    private static Random random = new Random();
    private int prevPosition = -1;
    private long prevTime = 0;
    public SlideshowFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SlideshowFragment.
     */
    public static SlideshowFragment newInstance() {
        SlideshowFragment fragment = new SlideshowFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_slideshow, container, false);
        viewPager2 = view.findViewById(R.id.view_pager_slide_show);

        ((GalleryViewActivity) getContext()).HideUI();
        mediaFileSlideShow = Observer.GetCurrentMediaFiles();

        SlideshowAdapter slideshowAdapter = new SlideshowAdapter(getContext(), mediaFileSlideShow, viewPager2, SLIDER_MODE.SLIDESHOW);
        viewPager2.setAdapter(slideshowAdapter);
        viewPager2.registerOnPageChangeCallback(onPageChangeCallback);
        return view;
    }

    @Override
    public boolean onBackPressed() {
        viewPager2.unregisterOnPageChangeCallback(onPageChangeCallback);
        ((GalleryViewActivity) getContext()).ShowUI();
        if(getActivity().getFragmentManager().getBackStackEntryCount() > 0){
            getParentFragmentManager().popBackStack();
        }
        return false;
    }

    private Runnable sliderRunnableCallback = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };

    private ViewPager2.OnPageChangeCallback onPageChangeCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            long currentTime = System.currentTimeMillis();
            if(position == prevPosition && currentTime - prevTime < TRANSITION_TIME_MILLIS)
                return;
            prevTime = currentTime;
            prevPosition = position;
            Log.d("Nothing", "on page selected: " + position);

            YoYo.with(animation[random.nextInt(animation.length)]).duration(500).playOn(viewPager2);
            sliderHandler.removeCallbacks(sliderRunnableCallback);
            sliderHandler.postDelayed(sliderRunnableCallback, TRANSITION_TIME_MILLIS + 500);
        }
    };
}