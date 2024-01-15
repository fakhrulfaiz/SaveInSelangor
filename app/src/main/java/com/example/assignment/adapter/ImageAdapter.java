package com.example.assignment.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.assignment.helper.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageAdapter extends ArrayAdapter<Uri> {
    private Context mContext;

    public ImageAdapter(Context context, ArrayList<Uri> images) {
        super(context, 0, images);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
// Set layout parameters for the ImageView

            imageView.setLayoutParams(new GridView.LayoutParams(300, 300)); // Adjust size as needed
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        // Load and set the image from the URI using Picasso
        Uri imageUri = getItem(position);
        if (imageUri != null) {
            Picasso.get()
                    .load(imageUri)
                    .fit().transform(new RoundedTransformation(30, 0))
                    .into(imageView);
        }

        return imageView;
    }
}
