package com.edu.schooltask.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.task.TaskInfo;
import com.edu.schooltask.beans.task.TaskOrderItem;
import com.edu.schooltask.utils.DateUtil;

import java.util.List;


/**
 * Created by 夜夜通宵 on 2017/5/4.
 */

public class TaskOrderAdapter extends BaseQuickAdapter<TaskOrderItem, BaseViewHolder> {
    public TaskOrderAdapter(List<TaskOrderItem> data) {
        super(R.layout.item_task_order, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TaskOrderItem item) {
        TextView typeText = helper.getView(R.id.to_type);
        GradientDrawable typeGrad = (GradientDrawable)typeText.getBackground();
        if(item.getType() == 0) typeGrad.setColor(Color.parseColor("#1b9dff"));
        else typeGrad.setColor(Color.parseColor("#ffa500"));
        String typeStr = item.getType() == 0 ? "发" : "接";
        typeText.setText(typeStr);
        helper.setText(R.id.to_content, item.getContent());
        helper.setText(R.id.to_cost, "¥" + item.getReward());
        String stateStr;
        switch (item.getState()){
            case 0:
                stateStr = "待接单";
                break;
            case 1:
                stateStr = "待完成";
                break;
            case 2:
                stateStr = "待确认";
                break;
            case 3:
                stateStr = "已完成";
                break;
            case 4:
                stateStr = "已失效";
                break;
            case 5:
                stateStr = "已取消";
                break;
            case 6:
                stateStr = "已放弃";
                break;
            case 7:
                stateStr = "已超时";
                break;
            default:
                stateStr = "";
        }
        helper.setText(R.id.to_state, stateStr);
        if(item.getState() == 0){
            helper.setText(R.id.to_time, DateUtil.getLimitTime(item.getLimitTime()));
            helper.setVisible(R.id.tc_time_ic, true);
        }
        else{
            helper.setText(R.id.to_time,"");
            helper.setVisible(R.id.tc_time_ic, false);
        }

    }
}
