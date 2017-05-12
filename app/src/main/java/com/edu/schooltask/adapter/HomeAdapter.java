package com.edu.schooltask.adapter;

import android.graphics.Color;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.User;
import com.edu.schooltask.data.DataCache;
import com.edu.schooltask.item.HomeItem;
import com.edu.schooltask.item.OrderItem;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/5/11.
 */

public class HomeAdapter extends BaseMultiItemQuickAdapter<HomeItem, BaseViewHolder> {

    private DataCache mDataCache;

    public HomeAdapter(List<HomeItem> data, DataCache mDataCache) {
        super(data);
        this.mDataCache = mDataCache;
        addItemType(HomeItem.LOAD_TIP, R.layout.rv_load_tip);
        addItemType(HomeItem.NEAR_TASK_ITEM, R.layout.item_near_task);
    }

    @Override
    protected void convert(final BaseViewHolder helper, HomeItem item) {
        switch (item.getItemType()){
            case 0:
                helper.setText(R.id.lf_text,item.text);
                break;
            case 1:
                OrderItem orderItem = item.orderItem;
                User user = orderItem.getReleaseUser();
                helper.setText(R.id.nt_name, user.getName());
                helper.setText(R.id.nt_school, orderItem.getSchool());
                helper.setText(R.id.nt_cost, "￥"+ orderItem.getCost());
                helper.setText(R.id.nt_content, orderItem.getContent());
                if(user.getSex() == 0){
                    helper.setText(R.id.nt_sex, "♂");
                    helper.setTextColor(R.id.nt_sex, Color.parseColor("#1B9DFF"));
                }
                if(user.getSex() == 1){
                    helper.setText(R.id.nt_sex, "♀");
                    helper.setTextColor(R.id.nt_sex, Color.parseColor("#FF3333"));
                }
                if(user.getSex() == -1){
                    helper.setVisible(R.id.nt_sex,false);
                }
                ImageView image1 = helper.getView(R.id.nt_image_1);
                ImageView image2 = helper.getView(R.id.nt_image_2);
                ImageView image3 = helper.getView(R.id.nt_image_3);
                break;
        }
    }
}
