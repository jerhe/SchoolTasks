package com.edu.schooltask.adapter;

import android.graphics.Color;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.UserBaseInfo;
import com.edu.schooltask.item.StateItem;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/5/14.
 */

public class StateAdapter extends BaseQuickAdapter<StateItem, BaseViewHolder> {
    int finishColor;
    int unfinishColor;
    public StateAdapter(int layoutResId, List<StateItem> data) {
        super(layoutResId, data);
        finishColor = Color.parseColor("#1B9DFF");
        unfinishColor = Color.parseColor("#AAAAAA");
    }

    @Override
    protected void convert(BaseViewHolder helper, StateItem item) {

        if(item.isFinish())
            helper.setImageResource(R.id.state_state_flag, R.drawable.ic_icon_state_finish);
        else
            helper.setImageResource(R.id.state_state_flag, R.drawable.ic_icon_state_unfinish);

        if(item.isAccept()){
            UserBaseInfo acceptUser = item.getAcceptUser();
            helper.setVisible(R.id.os_user_layout, true);
            helper.setText(R.id.os_accept_name, acceptUser.getName());
            helper.setText(R.id.os_accept_school, acceptUser.getSchool());
            switch (acceptUser.getSex()){
                case -1:
                    helper.setText(R.id.os_accept_sex, "");
                    break;
                case 0:
                    helper.setText(R.id.os_accept_sex, "♂");
                    break;
                case 1:
                    helper.setText(R.id.os_accept_sex, "♀");
                    break;
            }
        }
        else{
            helper.setVisible(R.id.os_user_layout, false);
        }
        if(item.getName() != null){
            helper.setText(R.id.os_state_name, item.getName());
        }
        if(item.getText() != null){
            helper.setText(R.id.os_state_text, item.getText());
        }
    }
}
