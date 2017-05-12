package com.edu.schooltask.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.alexvasilkov.gestures.views.GestureImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.edu.schooltask.R;
import com.edu.schooltask.base.BaseActivity;

public class ImageActivity extends BaseActivity {
    private GestureImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        Intent intent = getIntent();
        String image = intent.getStringExtra("image");
        imageView = (GestureImageView) findViewById(R.id.image_image);
        imageView.getController().getSettings().setMaxZoom(3.0f);
        Glide.with(this).load(image).into(imageView);
    }
}
