package com.edu.schooltask.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.edu.schooltask.R;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 夜夜通宵 on 2017/5/25.
 */

public class TaskFilterView extends LinearLayout{
    private FilterChangeListener listener;
    Map<String, FilterSelectView> filterMap = new HashMap<>();

    View shadowView;

    public TaskFilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(LinearLayout.VERTICAL);
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
        this.addView(view);
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

    public void setShadowView(View view){
        shadowView = view;
    }

    public void show(){
        setVisibility(VISIBLE);
        startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.translate_down));
        if(shadowView != null){
            shadowView.setVisibility(VISIBLE);
            shadowView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
        }
    }

    public void hide(){
        setVisibility(View.INVISIBLE);
        startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.translate_up));
        if(shadowView != null){
            shadowView.setVisibility(View.INVISIBLE);
            shadowView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_out));
        }
    }

    public void setFilterChangeListener(FilterChangeListener listener){
        this.listener = listener;
    }

    //分类选择接口
    public interface FilterChangeListener{
        void onFilterChange();
    }
}
