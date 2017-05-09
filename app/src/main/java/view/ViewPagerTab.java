package view;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edu.schooltask.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/5/9.
 */

public class ViewPagerTab extends LinearLayout implements View.OnClickListener{
    private String backgroundColor = "#FFFFFF";
    private String textColor = "#757575";
    private int textLightColor;
    private int pointLightColor;

    private OnTabSelectedListener onTabSelectedListener;
    private LinearLayout layout;
    private LinearLayout pointLayout;

    private List<String> texts = new ArrayList<>();
    private List<TextView> textViews = new ArrayList<>();
    private List<View> pointViews = new ArrayList<>();
    private int oldPosition = 0;
    public ViewPagerTab(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_viewpager_tab,this);
        layout = (LinearLayout) findViewById(R.id.vt_layout);
        pointLayout = (LinearLayout) findViewById(R.id.vt_point_layout);
        layout.setBackgroundColor(Color.parseColor(backgroundColor));
        textLightColor = Color.parseColor("#1B9DFF");
        pointLightColor = Color.parseColor("#1B9DFF");
    }

    public void addTab(String text){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT,1);
        for(TextView textView : textViews){
            textView.setLayoutParams(layoutParams);
        }
        TextView textView = new TextView(getContext());
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(layoutParams);
        textView.setText(text);
        textView.setOnClickListener(this);
        texts.add(text);
        textViews.add(textView);
        layout.addView(textView);
        View pointView = new View(getContext());
        if(pointViews.size() == 0) pointView.setBackgroundColor(pointLightColor);
        pointLayout.addView(pointView, layoutParams);
        pointViews.add(pointView);
    }

    public void setViewPager(final ViewPager viewPager){
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setPointerPosition(position, positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                setTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }



    public void setDefaultTab(int position){
        if(textViews.size() >= position){
            setTab(position);
            oldPosition = 0;
        }
    }

    public void setTab(int position){
        if(position < textViews.size()){
            textViews.get(oldPosition).setTextColor(Color.parseColor(textColor));
            textViews.get(position).setTextColor(textLightColor);
            oldPosition = position;
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

    public void setTextColor(String color){ textColor = color;}

    public void setTextLightColor(int color){
        textLightColor = color;
    }

    public void setPointLightColor(int color){
        if(pointViews.size() > 0)pointViews.get(0).setBackgroundColor(color);
    }

    public void setOnTabSelectedListener(OnTabSelectedListener listener){
        onTabSelectedListener = listener;
    }

    @Override
    public void onClick(View v) {
        int position = textViews.indexOf(v);
        setTab(position);
        onTabSelectedListener.onTabSelected(position);
    }


    public interface  OnTabSelectedListener{
        void onTabSelected(int position);
    }
}
