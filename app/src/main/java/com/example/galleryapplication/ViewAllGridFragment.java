package com.example.galleryapplication;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewAllGridFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewAllGridFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ViewAllGridFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewAllGridFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewAllGridFragment newInstance(String param1, String param2) {
        ViewAllGridFragment fragment = new ViewAllGridFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    private HashMap<String, ArrayList<String>> GetAllImages(){
        final int MAXLOADING = 10;
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        HashMap<String, ArrayList<String>> dictImages = new HashMap<>();
        String[] projections = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.BUCKET_ID,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.SIZE };
        Cursor cursor = getActivity().getContentResolver().query(uri, projections, null, null, null);
        int i = 0;
        if(cursor != null && cursor.getCount() > 0){
            while (cursor.moveToNext()){
                Log.d("Nothing", cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID)));
                Log.d("Nothing", cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID)));
                Log.d("Nothing", cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)));
                Log.d("Nothing", cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)));
                Log.d("Nothing", cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.SIZE)));
                String albumName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME));
                if(!dictImages.containsKey(albumName)){
                    dictImages.put(albumName, new ArrayList<>());
                }
                ArrayList<String> arrayImages = dictImages.get(albumName);
                arrayImages.add(cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)));
                i++;
                if(i >= MAXLOADING)
                    break;
            }
        }
        return dictImages;
    }

    private Bitmap ImagePathToBitmap(String imagePath){
        File f = new File(imagePath);
        return BitmapFactory.decodeFile(f.getPath());
    }

    private void SetImage(View thisView){
        ImageView[] imageViews = new ImageView[]{thisView.findViewById(R.id.imageView),thisView.findViewById(R.id.imageView2),thisView.findViewById(R.id.imageView3),thisView.findViewById(R.id.imageView4),thisView.findViewById(R.id.imageView5)};
        HashMap<String, ArrayList<String>> dictImages = GetAllImages();
        int i = 0;
        for (Map.Entry item : dictImages.entrySet()) {
            ArrayList<String> arrayImages = (ArrayList<String>) item.getValue();
            for (String imagePath : arrayImages){
                if(i > 4)
                    return;
                Bitmap imageBitmap = ImagePathToBitmap(imagePath);
                imageViews[i].setImageBitmap(imageBitmap);
                i++;
            }
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisView = inflater.inflate(R.layout.fragment_viewall_grid, container, false);
        SetImage(thisView);
        return thisView;

    }
}