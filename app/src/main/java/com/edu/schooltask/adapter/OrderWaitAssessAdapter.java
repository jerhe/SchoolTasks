package com.edu.schooltask.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.activity.AssessActivity;
import com.edu.schooltask.beans.TaskOrder;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/5/4.
 */

public class OrderWaitAssessAdapter extends BaseQuickAdapter<TaskOrder, BaseViewHolder> {
    private Activity activity;
    public OrderWaitAssessAdapter(Activity activity, int layoutResId, List<TaskOrder> data) {
        super(layoutResId, data);
        this.activity = activity;
    }

    @Override
    protected void convert(final BaseViewHolder helper, TaskOrder item) {
        helper.setText(R.id.owa_content, item.getContent());
        helper.setText(R.id.owa_time, "发布时间："+item.getReleaseTime());
        /*if(item.getType() == 0){
            helper.setVisible(R.id.owa_wait_assess,false);
            Button assessBtn = helper.getView(R.id.owa_assess_btn);
            assessBtn.setVisibility(View.VISIBLE);
            assessBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO 发送ID获取订单详细用Intent传递
                    Intent intent = new Intent(activity, AssessActivity.class);
                    activity.startActivity(intent);
                }
            });
        }
        else{
            helper.setVisible(R.id.owa_assess_btn,false);
            helper.setVisible(R.id.owa_wait_assess,true);
        }*/

    }
}
