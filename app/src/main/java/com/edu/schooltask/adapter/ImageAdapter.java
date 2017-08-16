package com.edu.schooltask.adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.item.ImageItem;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/5/19.
 */

public class ImageAdapter extends BaseQuickAdapter<ImageItem, BaseViewHolder> {

    public ImageAdapter(List<ImageItem> data) {
        super(R.layout.item_image, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ImageItem item) {
        ImageView imageView = helper.getView(R.id.image_image);
        switch (item.getType()){
            case 0:
                imageView.setImageResource(R.drawable.ic_action_add_image);
                break;
            case 1:
                Glide.with(imageView.getContext()).load(item.getPath()).into(imageView);
                break;
        }
    }
}
