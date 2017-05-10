package com.edu.schooltask.adapter;


import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/5/5.
 */

public class BannerViewPagerAdapter<T extends ImageView> extends PagerAdapter {
    private List<ImageView> pages;

    public BannerViewPagerAdapter(List<ImageView> imageViewList){
        this.pages = imageViewList;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;   //使用户看不到边界
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        position %= pages.size();
        if (position<0){
            position = pages.size()+position;
        }
        ImageView view = pages.get(position);
        //如果View已经在之前添加到了一个父组件，则必须先remove，否则会抛出IllegalStateException。
        ViewParent vp =view.getParent();
        if (vp!=null){
            ViewGroup parent = (ViewGroup)vp;
            parent.removeView(view);
        }
        container.addView(view);
        //TODO 事件
        return view;
    }
}
