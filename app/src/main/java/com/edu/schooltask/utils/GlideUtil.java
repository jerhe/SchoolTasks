package com.edu.schooltask.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.edu.schooltask.R;
import com.edu.schooltask.other.DataCache;

import server.api.SchoolTask;

/**
 * Created by 夜夜通宵 on 2017/5/14.
 */

public class GlideUtil {

    //从云端获取并设置头像
    public static void setHead(Context context, String userId, ImageView imageView){
        DataCache dataCache = new DataCache(context);
        int head = dataCache.getData("head");
        Glide.with(context)
                .load(SchoolTask.HEAD + userId + ".png")
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.rc_default_portrait)
                .error(R.drawable.rc_default_portrait)
                .signature(new StringSignature(head+""))
                .into(imageView);
    }

    public static void setBackground(Context context, String userId, ImageView imageView){
        DataCache dataCache = new DataCache(context);
        int bg = dataCache.getData("bg");
        Glide.with(context)
                .load(SchoolTask.BG + userId + ".png")
                .signature(new StringSignature(bg+""))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.background)
                .error(R.drawable.background)
                .into(imageView);
    }
}
