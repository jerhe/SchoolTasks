package com.edu.schooltask.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.event.RefreshHomeEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/5/4.
 */

public class BottomTab extends LinearLayout implements View.OnClickListener{
    public final static int PAGE_HOME = 0;  //首页
    public final static int PAGE_MESSAGE = 1;  //消息
    public final static int PAGE_ORDER = 2; //订单
    public final static int PAGE_USER = 3;  //用户

    private LinearLayout homeLayout;    //按钮
    private LinearLayout messageLayout;
    private LinearLayout orderLayout;
    private LinearLayout userLayout;
    private LinearLayout releaseLayout;

    private ImageView homeIcon; //图标
    private ImageView messageIcon;
    private ImageView orderIcon;
    private ImageView userIcon;
    private ImageView releaseIcon;

    private TextView homeText;  //文本
    private TextView messageText;
    private TextView orderText;
    private TextView userText;

    int normalColor;
    int lightColor;

    List<View> tabs = new ArrayList<>();
    int oldPosition = 0;

    private TextView messageTip;    //消息圆点

    private ViewPager viewPager;

    public BottomTab(Context context, AttributeSet attrs) {
        super(context, attrs);
        LinearLayout.inflate(context,R.layout.view_bottom_tab,this);
        homeLayout = (LinearLayout) findViewById(R.id.bm_home);
        messageLayout = (LinearLayout) findViewById(R.id.bm_message);
        orderLayout = (LinearLayout) findViewById(R.id.bm_order);
        userLayout = (LinearLayout) findViewById(R.id.bm_user);
        releaseLayout = (LinearLayout) findViewById(R.id.bm_release);

        tabs.add(homeLayout);
        tabs.add(messageLayout);
        tabs.add(orderLayout);
        tabs.add(userLayout);

        homeIcon = (ImageView) findViewById(R.id.bm_home_icon);
        messageIcon = (ImageView) findViewById(R.id.bm_message_icon);
        orderIcon = (ImageView) findViewById(R.id.bm_order_icon);
        userIcon = (ImageView) findViewById(R.id.bm_user_icon);
        releaseIcon = (ImageView) findViewById(R.id.bm_release_icon);

        homeText = (TextView) findViewById(R.id.bm_home_text);
        messageText = (TextView) findViewById(R.id.bm_message_text);
        orderText = (TextView) findViewById(R.id.bm_order_text);
        userText = (TextView) findViewById(R.id.bm_user_text);

        normalColor = getResources().getColor(R.color.darkColor);
        lightColor = getResources().getColor(R.color.colorPrimary);

        homeLayout.setOnClickListener(this);
        messageLayout.setOnClickListener(this);
        orderLayout.setOnClickListener(this);
        userLayout.setOnClickListener(this);

        messageTip = (TextView) findViewById(R.id.bm_tip);
    }

    @Override
    public void onClick(View v) {
        int index = tabs.indexOf(v);
        if(index == 0 && oldPosition == 0){
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    EventBus.getDefault().post(new RefreshHomeEvent());
                }
            }, 1000);
        }
        else viewPager.setCurrentItem(index, false);
    }

    /**
     * setIcon 设置菜单图标，高亮和默认
     * @param lightViewId 高亮的菜单ID
     */
    public void setIcon(int lightViewId){
        switch (lightViewId){
            case R.id.bm_home:
                homeIcon.setImageResource(R.drawable.ic_home_light);
                messageIcon.setImageResource(R.drawable.ic_message);
                orderIcon.setImageResource(R.drawable.ic_order);
                userIcon.setImageResource(R.drawable.ic_user);
                homeText.setTextColor(lightColor);
                messageText.setTextColor(normalColor);
                orderText.setTextColor(normalColor);
                userText.setTextColor(normalColor);
                break;
            case R.id.bm_message:
                homeIcon.setImageResource(R.drawable.ic_home);
                messageIcon.setImageResource(R.drawable.ic_message_light);
                orderIcon.setImageResource(R.drawable.ic_order);
                userIcon.setImageResource(R.drawable.ic_user);
                homeText.setTextColor(normalColor);
                messageText.setTextColor(lightColor);
                orderText.setTextColor(normalColor);
                userText.setTextColor(normalColor);
                setMessageTip(false);
                break;
            case R.id.bm_order:
                homeIcon.setImageResource(R.drawable.ic_home);
                messageIcon.setImageResource(R.drawable.ic_message);
                orderIcon.setImageResource(R.drawable.ic_order_light);
                userIcon.setImageResource(R.drawable.ic_user);
                homeText.setTextColor(normalColor);
                messageText.setTextColor(normalColor);
                orderText.setTextColor(lightColor);
                userText.setTextColor(normalColor);
                break;
            case R.id.bm_user:
                homeIcon.setImageResource(R.drawable.ic_home);
                messageIcon.setImageResource(R.drawable.ic_message);
                orderIcon.setImageResource(R.drawable.ic_order);
                userIcon.setImageResource(R.drawable.ic_user_light);
                homeText.setTextColor(normalColor);
                messageText.setTextColor(normalColor);
                orderText.setTextColor(normalColor);
                userText.setTextColor(lightColor);
                break;

        }
    }

    /**
     * setPagePosition 设置当前页面序号,用于高亮图标
     * @param position
     *
     */
    public void setPagePosition(int position){
        oldPosition = position;
        switch (position){
            case 0:
                setIcon(R.id.bm_home);
                break;
            case 1:
                setIcon(R.id.bm_message);
                break;
            case 2:
                setIcon(R.id.bm_order);
                break;
            case 3:
                setIcon(R.id.bm_user);
                break;
        }
    }

    /**
     * setMessageNum 设置显示消息红点
     * @params show
     */
    public void setMessageTip(boolean show){
        if(show){
            if (oldPosition != 1){
                messageTip.setVisibility(VISIBLE);
            }
        }
        else{
            messageTip.setVisibility(INVISIBLE);
        }
    }

    public void setViewPager(ViewPager viewPager){
        this.viewPager = viewPager;
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                setPagePosition(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    public void setReleaseListener(OnClickListener listener){
        releaseLayout.setOnClickListener(listener);
    }

    public void setReleaseLongListener(OnLongClickListener listener){
        releaseLayout.setOnLongClickListener(listener);
    }

    public View getReleaseTab(){
        return releaseLayout;
    }

}
