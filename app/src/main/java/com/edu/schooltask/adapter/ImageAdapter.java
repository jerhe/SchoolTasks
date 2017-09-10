package com.edu.schooltask.adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.item.ImageItem;
import com.edu.schooltask.ui.view.ImageLayout;

import java.util.List;

import io.rong.imkit.widget.SquareLayout;

/**
 * Created by 夜夜通宵 on 2017/5/19.
 */

public class ImageAdapter extends BaseQuickAdapter<ImageItem, BaseViewHolder> {
    //七牛缩略图样式
    final static String IMAGE_STYLE = "?imageView2/5/w/200/h/200/q/75|imageslim";

    public ImageAdapter(List<ImageItem> data) {
        super(R.layout.item_image, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ImageItem item) {
        ImageLayout imageLayout = helper.getView(R.id.image_layout);
        ImageView imageView = helper.getView(R.id.image_image);
        switch (item.getType()){
            case 0: //添加
                imageView.setImageResource(R.drawable.ic_add_image);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                break;
            case 1: //本地
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(imageView.getContext()).load(item.getPath()).into(imageView);
                break;
            case 2: //网络
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(imageView.getContext()).load(item.getPath() + IMAGE_STYLE).into(imageView);
                imageLayout.setPercentage(1f, 1f);
                break;
            case 3: //单张
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(imageView.getContext()).load(item.getPath() + IMAGE_STYLE).into(imageView);
                imageLayout.setPercentage(2f, 1f);
                break;
        }
    }
}
