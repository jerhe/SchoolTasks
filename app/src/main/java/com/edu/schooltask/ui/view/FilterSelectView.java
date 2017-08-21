package com.edu.schooltask.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.jaredrummler.materialspinner.MaterialSpinner;

/**
 * Created by 夜夜通宵 on 2017/8/21.
 */

public class FilterSelectView extends LinearLayout {
    private TextView nameText;
    private MaterialSpinner spinner;

    public FilterSelectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_filter_select, this);
        nameText = (TextView) findViewById(R.id.fs_name);
        spinner = (MaterialSpinner) findViewById(R.id.fs_spinner);
    }

    public void setName(String name){
        nameText.setText(name);
    }

    public void setSpinner(String...values){
        spinner.setItems(values);
    }

    public String getValue(){
        return spinner.getText().toString();
    }

    public void setSelectedIndex(int index){
        spinner.setSelectedIndex(index);
    }

    public int getSelectedIndex(){
        return spinner.getSelectedIndex();
    }

    public void setOnSelectedListener(MaterialSpinner.OnItemSelectedListener listener){
        spinner.setOnItemSelectedListener(listener);
    }
}
