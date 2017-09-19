package com.edu.schooltask.adapter;

import android.content.Context;
import android.content.Intent;

import com.edu.schooltask.activity.ImageActivity;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.NineGridViewAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/9/18.
 */

public class NineAdapter extends NineGridViewAdapter {

    public NineAdapter(Context context, List<ImageInfo> imageInfo) {
        super(context, imageInfo);
    }

    @Override
    protected void onImageItemClick(Context context, NineGridView nineGridView, int index, List<ImageInfo> imageInfos) {
        super.onImageItemClick(context, nineGridView, index, imageInfos);
        Intent intent = new Intent(context, ImageActivity.class);
        intent.putExtra("editable", false);
        intent.putExtra("index", index);
        List<String> images = new ArrayList<>();
        for(ImageInfo imageInfo : imageInfos) images.add(imageInfo.getBigImageUrl());
        intent.putExtra("images", (Serializable) images);
        context.startActivity(intent);
    }
}
