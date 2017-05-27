package com.edu.schooltask.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.edu.schooltask.R;

import de.hdodenhof.circleimageview.CircleImageView;
import server.api.SchoolTask;

/**
 * Created by 夜夜通宵 on 2017/5/14.
 */

public class GlideUtil {

    public static void setHead(Context context, String userId, CircleImageView imageView,
                               boolean skipMemoryCache){
        Glide.with(context)
                .load(SchoolTask.HEAD_URL + userId + ".png")
                .asBitmap()
                .dontAnimate()
                .diskCacheStrategy(skipMemoryCache ? DiskCacheStrategy.NONE : DiskCacheStrategy.ALL)
                .skipMemoryCache(skipMemoryCache)
                .placeholder(R.drawable.head)
                .error(R.drawable.head)
                .into(imageView);
    }

    public static void setBackground(Context context, String userId, ImageView imageView,
                                     boolean skipMemoryCache){
        Glide.with(context)
                .load(SchoolTask.BG_URL + userId + ".png")
                .asBitmap()
                .diskCacheStrategy(skipMemoryCache ? DiskCacheStrategy.NONE : DiskCacheStrategy.ALL)
                .skipMemoryCache(skipMemoryCache)
                .placeholder(R.drawable.background)
                .error(R.drawable.background)
                .into(imageView);
    }

    //TEST
    /*public static void setHead(Context context, final ImageView imageView){
        Glide.with(context)
                .load(R.drawable.head)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.head)
                .into(new BitmapImageViewTarget(imageView){
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(imageView.getResources(),resource);
                        roundedBitmapDrawable.setCircular(true);
                        imageView.setImageDrawable(roundedBitmapDrawable);
                    }
                });
    }*/
}
