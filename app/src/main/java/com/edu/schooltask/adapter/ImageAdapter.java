package com.edu.schooltask.adapter;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.item.ImageItem;
import com.edu.schooltask.view.ImageLayout;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/5/19.
 */

public class ImageAdapter extends BaseQuickAdapter<ImageItem, BaseViewHolder> {
    //七牛缩略图样式
    private final static String IMAGE_STYLE = "?imageView2/5/w/200/h/200/q/75|imageslim";

    public ImageAdapter(List<ImageItem> data) {
        super(R.layout.item_image, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ImageItem item) {
        ImageLayout imageLayout = helper.getView(R.id.image_layout);
        ImageView imageView = helper.getView(R.id.image_image);
        switch (item.getType()){
            case 1: //本地
                imageLayout.setPercentage(1f, 1f);
                Glide.with(imageView.getContext())
                        .load(item.getPath())
                        .centerCrop()
                        .into(imageView);
                break;
            case 2: //网络
                imageLayout.setPercentage(1f, 1f);
                Glide.with(imageView.getContext())
                        .load(item.getPath() + IMAGE_STYLE)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView);
                break;
            case 3: //网络单张
                imageLayout.setPercentage(2f, 1f);
                Glide.with(imageView.getContext())
                        .load(item.getPath() + IMAGE_STYLE)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView);
                break;
        }
    }
}
