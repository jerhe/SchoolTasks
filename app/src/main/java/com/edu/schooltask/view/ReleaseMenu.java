package com.edu.schooltask.view;

import android.content.Context;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.item.IconMenuItem;
import com.edu.schooltask.view.listener.ItemClickListener;
import com.edu.schooltask.view.recyclerview.IconMenuRecyclerView;

/**
 * Created by 夜夜通宵 on 2017/9/5.
 */

public class ReleaseMenu extends RelativeLayout {
    private View shadowView;
    private IconMenuRecyclerView iconMenuRecyclerView;

    Animation fadeInAnimation;
    Animation fadeOutAnimation;
    Animation iconMenuInAnimation;
    Animation iconMenuOutAnimation;

    View startView;


    public ReleaseMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_release_menu, this);
        shadowView = findViewById(R.id.rm_shadow);
        iconMenuRecyclerView = (IconMenuRecyclerView) findViewById(R.id.rm_imrv);
        iconMenuRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(4,1));
        iconMenuRecyclerView.add(new IconMenuItem(IconMenuItem.VERTICAL_LARGE, R.drawable.ic_release_task,
                R.drawable.shape_ring_release, "发布任务"));
        iconMenuRecyclerView.add(new IconMenuItem(IconMenuItem.VERTICAL_LARGE, R.drawable.ic_release_dynamic,
                R.drawable.shape_ring_dynamic, "发布动态"));
        shadowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });

        setFocusable(true);
        setFocusableInTouchMode(true);
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN){
                    if(keyCode == KeyEvent.KEYCODE_BACK){
                        hide();
                        return true;
                    }
                }
                return false;
            }
        });
        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                    if(isShown()) hide();
            }
        });

        iconMenuInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.release_menu_show);
        iconMenuInAnimation.setInterpolator(new OvershootInterpolator());
        iconMenuOutAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.release_menu_hide);
        fadeInAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        fadeOutAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
    }

    public void setItemClickListener(ItemClickListener<IconMenuItem> listener){
        iconMenuRecyclerView.setItemClickListener(listener);
    }

    public void setStartView(View v){
        startView = v;
    }

    public void show(){
        if(isShown()) return;
        setVisibility(VISIBLE);
        requestFocus();
        startAnimation(fadeInAnimation);
        iconMenuRecyclerView.startAnimation(iconMenuInAnimation);
        if(startView != null) startView.animate().rotationBy(180);
    }

    public void hide(){
        if(!isShown()) return;
        setVisibility(GONE);
        startAnimation(fadeOutAnimation);
        iconMenuRecyclerView.startAnimation(iconMenuOutAnimation);
        if(startView != null) startView.animate().rotationBy(-180);
    }
}
