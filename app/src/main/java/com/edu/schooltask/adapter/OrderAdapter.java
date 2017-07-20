package com.edu.schooltask.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.TaskOrder;

import java.util.List;


/**
 * Created by 夜夜通宵 on 2017/5/4.
 */

public class OrderAdapter extends BaseQuickAdapter<TaskOrder, BaseViewHolder> {
    public OrderAdapter(int layoutResId, List<TaskOrder> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TaskOrder item) {
        TextView typeText = helper.getView(R.id.to_type);
        GradientDrawable typeGrad = (GradientDrawable)typeText.getBackground();
        if(item.type == 0) typeGrad.setColor(Color.parseColor("#1b9dff"));
        else typeGrad.setColor(Color.parseColor("#ffa500"));
        String typeStr = item.type == 0 ? "发" : "接";
        typeText.setText(typeStr);
        helper.setText(R.id.to_content, item.getContent());
        helper.setText(R.id.to_cost, "¥"+item.getCost());
        helper.setText(R.id.to_state, item.getStateStr());
    }
}
