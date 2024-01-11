package com.example.assignment.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.assignment.R;

public class AddImageDialog extends Dialog {

    private View.OnClickListener onCameraClickListener;
    private View.OnClickListener onGalleryClickListener;
    private View.OnClickListener onCancelClickListener;

    public AddImageDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_dialog);

        ImageView btnOpenCamera = findViewById(R.id.cameraBtn);
        ImageView btnOpenGallery = findViewById(R.id.galleryBtn);
        Button btnCancel = findViewById(R.id.btnCancel);

        btnOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCameraClickListener != null) {
                    onCameraClickListener.onClick(v);
                }
            }
        });

        btnOpenGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onGalleryClickListener != null) {
                    onGalleryClickListener.onClick(v);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCancelClickListener != null) {
                    onCancelClickListener.onClick(v);
                }
            }
        });
    }

    public void setOnCameraClickListener(View.OnClickListener listener) {
        this.onCameraClickListener = listener;
    }

    public void setOnGalleryClickListener(View.OnClickListener listener) {
        this.onGalleryClickListener = listener;
    }

    public void setOnCancelClickListener(View.OnClickListener listener) {
        this.onCancelClickListener = listener;
    }
}
