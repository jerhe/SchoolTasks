package com.edu.schooltask.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.activity.AssessActivity;
import com.edu.schooltask.item.OrderItem;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/5/4.
 */

public class OrderWaitAssessAdapter extends BaseQuickAdapter<OrderItem, BaseViewHolder> {
    private Activity activity;
    public OrderWaitAssessAdapter(Activity activity, int layoutResId, List<OrderItem> data) {
        super(layoutResId, data);
        this.activity = activity;
    }

    @Override
    protected void convert(final BaseViewHolder helper, OrderItem item) {
        helper.setText(R.id.owa_title, item.getTitle());
        helper.setText(R.id.owa_time, "发布时间："+item.getReleaseTime());
        Button assessBtn = helper.getView(R.id.owa_assess_btn);
        assessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 发送ID获取订单详细用Intent传递
                Intent intent = new Intent(activity, AssessActivity.class);
                activity.startActivity(intent);
            }
        });
    }
}
