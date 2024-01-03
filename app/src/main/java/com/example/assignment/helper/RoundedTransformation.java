package com.example.assignment.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import com.squareup.picasso.Transformation;

public class RoundedTransformation implements Transformation {

    private final int radius;
    private final int margin; // Width and height of the margin

    // radius is corner radii in dp
    // margin is the board in dp
    public RoundedTransformation(final int radius, final int margin) {
        this.radius = radius;
        this.margin = margin;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));

        // Calculate the dimensions for the rounded rectangle
        int width = source.getWidth();
        int height = source.getHeight();

        // Create a new bitmap with adjusted dimensions
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        // Draw the rounded rectangle with the specified margin
        canvas.drawRoundRect(
                margin,
                margin,
                width - margin,
                height - margin,
                radius,
                radius,
                paint
        );

        // Recycle the source bitmap if it's different from the output bitmap
        if (source != output) {
            source.recycle();
        }

        return output;
    }

    @Override
    public String key() {
        return "rounded";
    }
}
