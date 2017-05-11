package com.edu.schooltask.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edu.schooltask.R;

/**
 * Created by 夜夜通宵 on 2017/5/4.
 */

public class BottomMenu extends LinearLayout implements View.OnClickListener{
    public final static int PAGE_HOME = 0;
    public final static int PAGE_TALK = 1;
    public final static int PAGE_ORDER = 2;
    public final static int PAGE_USER = 3;
    private OnMenuSelectedListener onMenuSelectedListener;

    private LinearLayout homeLayout;
    private LinearLayout talkLayout;
    private LinearLayout orderLayout;
    private LinearLayout userLayout;

    private ImageView homeIcon;
    private ImageView talkIcon;
    private ImageView orderIcon;
    private ImageView userIcon;

    private TextView messageNum;

    public BottomMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        LinearLayout.inflate(context,R.layout.view_bottom_menu,this);
        homeLayout = (LinearLayout) findViewById(R.id.bm_home);
        talkLayout = (LinearLayout) findViewById(R.id.bm_talk);
        orderLayout = (LinearLayout) findViewById(R.id.bm_order);
        userLayout = (LinearLayout) findViewById(R.id.bm_user);

        homeIcon = (ImageView) findViewById(R.id.bm_home_icon);
        talkIcon = (ImageView) findViewById(R.id.bm_talk_icon);
        orderIcon = (ImageView) findViewById(R.id.bm_order_icon);
        userIcon = (ImageView) findViewById(R.id.bm_user_icon);

        homeLayout.setOnClickListener(this);
        talkLayout.setOnClickListener(this);
        orderLayout.setOnClickListener(this);
        userLayout.setOnClickListener(this);

        messageNum = (TextView) findViewById(R.id.bm_talk_num);
    }

    @Override
    public void onClick(View v) {
        if(onMenuSelectedListener != null){
            setIcon(v.getId());
        }
    }

    /**
     * setIcon 设置菜单图标，高亮和默认
     * @param lightViewId 高亮的菜单ID
     */
    public void setIcon(int lightViewId){
        switch (lightViewId){
            case R.id.bm_home:
                onMenuSelectedListener.onMenuSelected(PAGE_HOME);
                homeIcon.setImageResource(R.drawable.ic_action_home_light);
                talkIcon.setImageResource(R.drawable.ic_action_talk);
                orderIcon.setImageResource(R.drawable.ic_action_order);
                userIcon.setImageResource(R.drawable.ic_action_user);
                break;
            case R.id.bm_talk:
                onMenuSelectedListener.onMenuSelected(PAGE_TALK);
                homeIcon.setImageResource(R.drawable.ic_action_home);
                talkIcon.setImageResource(R.drawable.ic_action_talk_light);
                orderIcon.setImageResource(R.drawable.ic_action_order);
                userIcon.setImageResource(R.drawable.ic_action_user);
                break;
            case R.id.bm_order:
                onMenuSelectedListener.onMenuSelected(PAGE_ORDER);
                homeIcon.setImageResource(R.drawable.ic_action_home);
                talkIcon.setImageResource(R.drawable.ic_action_talk);
                orderIcon.setImageResource(R.drawable.ic_action_order_light);
                userIcon.setImageResource(R.drawable.ic_action_user);
                break;
            case R.id.bm_user:
                onMenuSelectedListener.onMenuSelected(PAGE_USER);
                homeIcon.setImageResource(R.drawable.ic_action_home);
                talkIcon.setImageResource(R.drawable.ic_action_talk);
                orderIcon.setImageResource(R.drawable.ic_action_order);
                userIcon.setImageResource(R.drawable.ic_action_user_light);
                break;
        }
    }

    /**
     * setOnMenuSelectedListener 设置菜单切换事件监听器
     * @param onMenuSelectedListener
     */
    public void setOnMenuSelectedListener(OnMenuSelectedListener onMenuSelectedListener){
        this.onMenuSelectedListener = onMenuSelectedListener;
    }

    /**
     * setPagePosition 设置当前页面序号,用于高亮图标
     * @param position
     *
     */
    public void setPagePosition(int position){
        switch (position){
            case 0:
                setIcon(R.id.bm_home);
                break;
            case 1:
                setIcon(R.id.bm_talk);
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
     * OnMenuSelectedListener 菜单切换事件接口
     */
    public interface OnMenuSelectedListener{
        void onMenuSelected(int position);
    }

    /**
     * setMessageNum 设置显示的未读消息红点
     * @params num 未读消息数，为0则不显示
     */
    public void setMessageNum(int num){
        messageNum.setText(num + "");
        if(num == 0) messageNum.setVisibility(INVISIBLE);
        else messageNum.setVisibility(VISIBLE);
    }
}
