package com.edu.schooltask.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.edu.schooltask.R;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 夜夜通宵 on 2017/5/25.
 */

public class TaskFilterView extends RelativeLayout{

    private FilterChangeListener listener;  //筛选监听器
    Map<String, FilterSelectView> filterMap = new HashMap<>();  //筛选项

    LinearLayout filterLayout;  //筛选布局
    View shadowView;     //阴影布局

    Animation fadeInAnimation;
    Animation fadeOutAnimation;
    Animation translateDownAnimation;
    Animation translateUpAnimation;
    Animation emptyAnimation;

    public TaskFilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        fadeOutAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_out);
        translateDownAnimation = AnimationUtils.loadAnimation(context, R.anim.translate_down);
        translateUpAnimation = AnimationUtils.loadAnimation(context, R.anim.translate_up);
        emptyAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.empty);
        //阴影布局
        shadowView = new View(context, null);
        shadowView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        shadowView.setBackgroundColor(getResources().getColor(R.color.shadow));
        shadowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });
        this.addView(shadowView);
        //筛选布局
        filterLayout = new LinearLayout(context, null);
        filterLayout.setBackgroundColor(getResources().getColor(R.color.backgroundColor));
        filterLayout.setOrientation(LinearLayout.VERTICAL);
        filterLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        this.addView(filterLayout);
        //焦点
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
        //失去焦点关闭
        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(isShown()){
                        hide();
                    }
                }
            }
        });
    }

    public void addFilter(String name, String...values){
        FilterSelectView view = new FilterSelectView(getContext(), null);
        view.setName(name);
        view.setSpinner(values);
        view.setOnSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if(listener != null) listener.onFilterChange();
            }
        });
        filterLayout.addView(view);
        filterMap.put(name, view);
    }

    public void setSelectedIndex(int index){
        filterMap.get(index).setSelectedIndex(index);
    }

    public String getValue(String name){
        return filterMap.get(name).getValue();
    }

    public int getSelectedIndex(String name){
        return filterMap.get(name).getSelectedIndex();
    }

    public void show(){
        setVisibility(VISIBLE);
        filterLayout.startAnimation(translateDownAnimation);
        shadowView.startAnimation(fadeInAnimation);
        requestFocus();
    }

    public void hide(){
        filterLayout.startAnimation(translateUpAnimation);
        shadowView.startAnimation(fadeOutAnimation);
        startAnimation(emptyAnimation);
        setVisibility(GONE);
    }

    public void setFilterChangeListener(FilterChangeListener listener){
        this.listener = listener;
    }

    //分类选择接口
    public interface FilterChangeListener{
        void onFilterChange();
    }

}
