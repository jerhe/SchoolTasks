package com.edu.schooltask.view.recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.ImageAdapter;
import com.edu.schooltask.item.ImageItem;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/8/14.
 */

public class ImageRecyclerView extends BaseRecyclerView<ImageItem> {
    ImageClickListener listener;
    SpaceClickListener spaceClickListener;

    public ImageRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ImageRecyclerView);
        int columns = typedArray.getInt(R.styleable.ImageRecyclerView_imageColumns, 3); //默认3列
        typedArray.recycle();

        setLayoutManager(new GridLayoutManager(context, columns));
        setNestedScrollingEnabled(false);
        setFocusableInTouchMode(false); //防止自动滚动
    }

    @Override
    protected BaseQuickAdapter initAdapter(List<ImageItem> list) {
        return new ImageAdapter(list);
    }

    private void refreshVisible(){
        if (get().size() == 0) setVisibility(GONE);
        else setVisibility(VISIBLE);
    }

    public void setImageClickListener(final ImageClickListener listener){
        this.listener = listener;
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                listener.onImageClick(position, get(position));
            }
        });
    }

    public void setSpaceClickListener(final SpaceClickListener listener){
        this.spaceClickListener = listener;
        setOnTouchListener(new OnTouchListener() {
            float y;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        y = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        if(event.getY() == y) listener.OnSpaceClick();
                        break;
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

    @Override
    public void add(ImageItem imageItem) {
        super.add(imageItem);
        refreshVisible();
    }

    @Override
    public void add(List<ImageItem> list) {
        super.add(list);
        refreshVisible();
    }

    @Override
    public void remove(int position) {
        super.remove(position);
        refreshVisible();
    }

    @Override
    public void clear() {
        super.clear();
        refreshVisible();
    }
}
