package com.edu.schooltask.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.activity.LoginActivity;
import com.edu.schooltask.activity.ReleaseActivity;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.beans.User;
import com.edu.schooltask.data.DataCache;
import com.edu.schooltask.http.HttpUtil;
import com.edu.schooltask.item.HomeItem;
import com.edu.schooltask.item.OrderItem;
import com.edu.schooltask.other.BannerViewPagerPointer;
import com.edu.schooltask.view.ViewPagerTab;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 夜夜通宵 on 2017/5/11.
 */

public class HomeAdapter extends BaseMultiItemQuickAdapter<HomeItem, BaseViewHolder> {

    private DataCache mDataCache;

    public HomeAdapter(List<HomeItem> data, DataCache mDataCache) {
        super(data);
        this.mDataCache = mDataCache;
        addItemType(HomeItem.NEAR_TASK_ITEM, R.layout.item_near_task);
    }

    @Override
    protected void convert(final BaseViewHolder helper, HomeItem item) {
        switch (item.getItemType()){
            case 1:
                OrderItem orderItem = item.orderItem;
                User user = orderItem.getReleaseUser();
                helper.setText(R.id.nt_name, user.getName());
                helper.setText(R.id.nt_school, user.getSchool());
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
