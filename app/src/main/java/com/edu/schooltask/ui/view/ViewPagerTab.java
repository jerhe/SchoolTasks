package com.edu.schooltask.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edu.schooltask.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/5/9.
 */

//ViewPager标签
public class ViewPagerTab extends LinearLayout implements View.OnClickListener{
    private int backgroundColor = Color.parseColor("#FAFAFA"); //标签背景色
    private int textColor = Color.parseColor("#757575");   //默认文本颜色
    private int textLightColor = Color.parseColor("#1B9DFF"); //高亮文本颜色
    private int pointLightColor = Color.parseColor("#1B9DFF");    //高亮滚条颜色

    private LinearLayout layout;
    private LinearLayout pointLayout;

    private ViewPager viewPager;

    private List<String> texts = new ArrayList<>();
    public List<ViewPagerTabItem> items = new ArrayList<>();
    public List<View> pointViews = new ArrayList<>();
    private int oldPosition = 0;
    public ViewPagerTab(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_viewpager_tab,this);

        layout = (LinearLayout) findViewById(R.id.vt_layout);
        pointLayout = (LinearLayout) findViewById(R.id.vt_point_layout);
        layout.setBackgroundColor(backgroundColor);
    }

    /**
     * 添加一个标签
     * @param text  标签标题
     */
    public void addTab(String text){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1);
        for(ViewPagerTabItem item : items){
            item.setLayoutParams(layoutParams);
        }
        ViewPagerTabItem item = new ViewPagerTabItem(getContext(), null);
        item.setLayoutParams(layoutParams); //标签布局
        item.setText(text);
        item.setOnClickListener(this);  //添加切换事件
        texts.add(text);
        items.add(item);
        layout.addView(item);
        View pointView = new View(getContext());    //新建滚条
        if(pointViews.size() == 0) {    //滚条数为0设置为高亮
            pointView.setBackgroundColor(pointLightColor);
        }
        pointLayout.addView(pointView, layoutParams);
        pointViews.add(pointView);
    }

    /**
     * 设置ViewPager关联事件
     * @param viewPager
     */
    public void setViewPager(final ViewPager viewPager){
        this.viewPager = viewPager;
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setPointerPosition(position, positionOffset);   //ViewPager滚动 改变滚条位置
            }

            @Override
            public void onPageSelected(int position) {
                select(position);   //页面切换，改变标签高亮
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    /**
     * 设置标签高亮
     * @param position  标签位置，从0开始
     */
    public void select(int position){
        if(position < items.size()){    //防止越界
            items.get(oldPosition).setTextColor(textColor); //原标签恢复默认颜色
            items.get(position).setTextColor(textLightColor);   //新标签高亮
            items.get(position).setTip(false);  //取消圆点
            oldPosition = position;
            if(viewPager != null) {
                viewPager.setCurrentItem(position); //切换页面
            }
            else {
                View pointView = pointViews.get(0);
                pointView.setX(pointView.getWidth() * position);    //滚条移动到指定位置
            }
        }
    }

    /**
     * setPointerPosition 设置指示器位置,当viewpager发生滚动时调用
     * @param position  当前页面序号
     * @param offset    滚动偏移百分比
     */
    public void setPointerPosition(int position, float offset){
        if(pointViews.size() > 0){
            View point = pointViews.get(0);
            int width = point.getWidth();
            point.setX(width * position + width * offset);
        }
    }


    public void setBackgroundColor(int color){
        layout.setBackgroundColor(color);
    }

    public void setTextColor(int color){ textColor = color;}

    public void setTextLightColor(int color){
        textLightColor = color;
    }

    public void setPointLightColor(int color){
        if(pointViews.size() > 0)pointViews.get(0).setBackgroundColor(color);
    }

    public ViewPagerTabItem getTab(int index){
        return items.get(index);
    }

    public int getPosition(){
        return oldPosition;
    }

    public void setTip(int position, boolean show){
        if(show && oldPosition != position) getTab(position).setTip(true);
        if(!show) getTab(position).setTip(false);
    }

    @Override
    public void onClick(View v) {
        int position = items.indexOf(v);
        select(position);
    }

}
