package com.edu.schooltask.adapter;

import android.graphics.Color;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.UserInfoBase;
import com.edu.schooltask.item.OrderStateItem;
import com.edu.schooltask.view.UserItemView;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/5/14.
 */

public class OrderStateAdapter extends BaseQuickAdapter<OrderStateItem, BaseViewHolder> {
    int finishedColor;
    int unfinishedColor;
    public OrderStateAdapter(int layoutResId, List<OrderStateItem> data) {
        super(layoutResId, data);
        finishedColor = Color.parseColor("#1B9DFF");
        unfinishedColor = Color.parseColor("#AAAAAA");
    }

    @Override
    protected void convert(BaseViewHolder helper, OrderStateItem item) {
        if(item.isFinish())
            helper.setImageResource(R.id.state_state_flag, R.drawable.ic_icon_state_finish);
        else
            helper.setImageResource(R.id.state_state_flag, R.drawable.ic_icon_state_unfinish);

        if(item.isAccept()){
            helper.setVisible(R.id.os_uiv, true);
            UserInfoBase acceptUser = item.getAcceptUser();
            UserItemView userItemView = helper.getView(R.id.os_uiv);
            userItemView.setAll(acceptUser);
        }
        else{
            helper.setVisible(R.id.os_uiv, false);
        }
        if(item.getName() != null){
            helper.setText(R.id.os_state_name, item.getName());
        }
        if(item.getText() != null){
            helper.setText(R.id.os_state_text, item.getText());
        }
    }
}
