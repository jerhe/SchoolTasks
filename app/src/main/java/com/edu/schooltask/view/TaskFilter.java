package com.edu.schooltask.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.jaredrummler.materialspinner.MaterialSpinner;

/**
 * Created by 夜夜通宵 on 2017/5/25.
 */

public class TaskFilter extends LinearLayout{
    private FilterChangeListener listener;
    private MaterialSpinner schoolText;
    private MaterialSpinner desText;
    private MaterialSpinner costText;
    public TaskFilter(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_task_filter,this);
        schoolText = (MaterialSpinner) findViewById(R.id.filter_school);
        desText = (MaterialSpinner) findViewById(R.id.filter_des);
        costText = (MaterialSpinner) findViewById(R.id.filter_cost);
        schoolText.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if(listener != null){
                    listener.onFilterChange();
                }
            }
        });
        desText.setItems("所有","学习","生活","娱乐","运动","商家","其他");
        desText.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if(listener != null){
                    listener.onFilterChange();
                }
            }
        });
        costText.setItems("所有","1-5元","5-10元","10-20元","20-100元","100元以上");
        costText.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if(listener != null){
                    listener.onFilterChange();
                }
            }
        });
    }

    public String getSchool(){
        return schoolText.getText().toString();
    }

    public void setSchool(String[] school){
        schoolText.setItems(school);
    }

    public String getDes(){
        return desText.getText().toString();
    }

    public int getCost(){
        return costText.getSelectedIndex();
    }

    public void setFilterChangeListener(FilterChangeListener listener){
        this.listener = listener;
    }

    //分类选择接口
    public interface FilterChangeListener{
        void onFilterChange();
    }
}
