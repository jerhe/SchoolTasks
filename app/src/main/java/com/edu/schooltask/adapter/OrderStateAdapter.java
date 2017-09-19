package com.edu.schooltask.adapter;

import android.graphics.Color;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.item.OrderStateItem;
import com.edu.schooltask.view.useritem.UserItemSmallView;
import com.edu.schooltask.utils.StringUtil;

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
            helper.setImageResource(R.id.state_state_flag, R.drawable.ic_action_finished);
        else
            helper.setImageResource(R.id.state_state_flag, R.drawable.ic_action_unfinish);

        if(item.isAccept()){
            helper.setVisible(R.id.os_uisv, true);
            UserInfo acceptUser = item.getAcceptUser();
            UserItemSmallView userItemSmallView = helper.getView(R.id.os_uisv);
            userItemSmallView.setAll(acceptUser);
        }
        else{
            helper.setVisible(R.id.os_uisv, false);
        }
        if(!StringUtil.isEmpty(item.getName())){
            helper.setText(R.id.os_state_name, item.getName());
        }
        String text = item.getText();
        if(!StringUtil.isEmpty(text)){
            if(text.endsWith(".0")) text = text.replace(".0", "");
            helper.setText(R.id.os_state_text, text);
        }
        else{
            helper.setText(R.id.os_state_text, "");
        }
    }
}
