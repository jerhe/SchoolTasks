package com.edu.schooltask.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.ImageAdapter;
import com.edu.schooltask.item.ImageItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/8/14.
 */

public class ImageRecyclerView extends RecyclerView {
    ImageAdapter adapter;
    List<ImageItem> imageItems = new ArrayList<>();
    ImageClickListener listener;
    SpaceClickListener spaceClickListener;

    public ImageRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImageRecyclerView);
        int columns = typedArray.getInt(R.styleable.ImageRecyclerView_imageColumns, 3); //默认3列
        setLayoutManager(new GridLayoutManager(context, columns));

        adapter = new ImageAdapter(R.layout.item_image, imageItems);
        setAdapter(adapter);
    }

    //添加图片
    public void addImage(ImageItem imageItem){
        imageItems.add(imageItem);
        adapter.notifyDataSetChanged();
    }

    //添加多张图片
    public void addImages(List<ImageItem> imageItems){
        this.imageItems.addAll(imageItems);
        adapter.notifyDataSetChanged();
    }

    //删除图片
    public void removeImage(int index){
        imageItems.remove(index);
        adapter.notifyDataSetChanged();
    }

    //删除最后一张图片
    public void removeLastImage(){
        imageItems.remove(imageItems.size() - 1);
        adapter.notifyDataSetChanged();
    }

    public void clear(){
        imageItems.clear();
        adapter.notifyDataSetChanged();
    }

    //获取所有图片
    public List<ImageItem> getImages(){
        return imageItems;
    }

    public void setImageClickListener(final ImageClickListener listener){
        this.listener = listener;
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                listener.onImageClick(position, imageItems.get(position));
            }
        });
    }

    public void setSpaceClickListener(final SpaceClickListener listener){
        this.spaceClickListener = listener;
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    listener.OnSpaceClick();
                }
                return false;
            }
        });
    }

    public interface ImageClickListener{
        void onImageClick(int position, ImageItem imageItem);
    }

    public interface SpaceClickListener{
        void OnSpaceClick();
    }
}
