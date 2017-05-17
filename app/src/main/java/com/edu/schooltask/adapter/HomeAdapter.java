package com.edu.schooltask.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.activity.ImageActivity;
import com.edu.schooltask.activity.UserActivity;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.beans.User;
import com.edu.schooltask.data.DataCache;
import com.edu.schooltask.http.HttpUtil;
import com.edu.schooltask.item.HomeItem;
import com.edu.schooltask.item.OrderItem;
import com.edu.schooltask.utils.DateUtil;
import com.edu.schooltask.utils.GlideUtil;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/5/11.
 */

public class HomeAdapter extends BaseMultiItemQuickAdapter<HomeItem, BaseViewHolder> {

    private DataCache mDataCache;
    private BaseActivity activity;

    public HomeAdapter(List<HomeItem> data, DataCache mDataCache, BaseActivity activity) {
        super(data);
        this.mDataCache = mDataCache;
        this.activity = activity;
        addItemType(HomeItem.LOAD_TIP, R.layout.rv_load_tip);
        addItemType(HomeItem.NEAR_TASK_SHOW_ITEM, R.layout.item_near_task);
        addItemType(HomeItem.NEAR_TASK_ITEM, R.layout.item_near_task);
        addItemType(HomeItem.COUNT_ITEM, R.layout.item_look_count);
        addItemType(HomeItem.COMMENT, R.layout.item_order_comment_parent);
    }

    @Override
    protected void convert(final BaseViewHolder helper, HomeItem item) {
        switch (item.getItemType()){
            case 0:
                helper.setText(R.id.lf_text,item.text);
                break;
            case 1:
                setNearTaskItem(helper, item, false);
                break;
            case 2:
                setNearTaskItem(helper, item, true);
                break;
            case 3:
                helper.setText(R.id.lc_comment_count, "评论("+item.commentCount+")");
                helper.setText(R.id.lc_look_count, "浏览:"+item.lookCount);
                break;
            case 4:
                ImageView headView = helper.getView(R.id.ui_head);
                GlideUtil.setHead(headView.getContext(), headView);
                helper.setText(R.id.ui_school, item.orderComment.getUserSchool());
                helper.setText(R.id.ui_name,item.orderComment.getUserName());
                helper.setText(R.id.ui_release_time,
                        DateUtil.getLong(DateUtil.stringToDate(item.orderComment.getTime())));
                switch (item.orderComment.getUserSex()){
                    case -1:
                        helper.setText(R.id.ui_sex,"");
                        break;
                    case 0:
                        helper.setText(R.id.ui_sex,"男");
                        helper.setTextColor(R.id.ui_sex, Color.parseColor("#1B9DFF"));
                        break;
                    case 1:
                        helper.setText(R.id.ui_sex,"女");
                        helper.setTextColor(R.id.ui_sex, Color.parseColor("#FF0000"));
                        break;
                }
                int childCount = item.orderComment.getChildCount();
                if(item.orderComment.getParentId() != 0){
                    helper.setText(R.id.ocp_comment, "回复 "+item.orderComment.getToUserName()
                            +"："+item.orderComment.getComment());
                    helper.setVisible(R.id.ocp_child_count, false);
                }
                else{
                    helper.setText(R.id.ocp_comment, item.orderComment.getComment());
                    if(childCount != 0){
                        helper.setVisible(R.id.ocp_child_count, true);
                        helper.setText(R.id.ocp_child_count, childCount + "条回复");
                        helper.addOnClickListener(R.id.ocp_child_count);
                    }
                    else{
                        helper.setVisible(R.id.ocp_child_count, false);
                    }
                }
                helper.addOnClickListener(R.id.ui_layout);
                break;
        }
    }

    private void setNearTaskItem(BaseViewHolder helper, HomeItem item, boolean addListener){
        final OrderItem orderItem = item.orderItem;
        final User user = orderItem.getReleaseUser();
        helper.setText(R.id.ui_name, user.getName());
        helper.setText(R.id.ui_school,
                addListener ? orderItem.getSchool()+"(任务)" : orderItem.getSchool());
        helper.setText(R.id.nt_cost, "¥"+ orderItem.getCost());
        helper.setText(R.id.nt_content, orderItem.getContent());
        helper.setText(R.id.ui_release_time,
                DateUtil.getLong(DateUtil.stringToDate(orderItem.getReleaseTime())));
        if(user.getSex() == 0){
            helper.setText(R.id.ui_sex, "♂");
            helper.setTextColor(R.id.ui_sex, Color.parseColor("#1B9DFF"));
        }
        if(user.getSex() == 1){
            helper.setText(R.id.ui_sex, "♀");
            helper.setTextColor(R.id.ui_sex, Color.parseColor("#FF3333"));
        }
        if(user.getSex() == -1){
            helper.setText(R.id.ui_sex, "");
        }
        RelativeLayout userView = helper.getView(R.id.ui_layout);
        //头像
        final ImageView headView = helper.getView(R.id.ui_head);
        GlideUtil.setHead(headView.getContext(), headView);

        //图片
        LinearLayout imageLayout = helper.getView(R.id.nt_image_layout);
        final ImageView image1 = helper.getView(R.id.nt_image_1);
        ImageView image2 = helper.getView(R.id.nt_image_2);
        ImageView image3 = helper.getView(R.id.nt_image_3);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String image = HttpUtil.ORDER_IMAGE_URL + orderItem.getId();
                switch (v.getId()){
                    case R.id.nt_image_1:
                        image =  image + "/1.png";
                        break;
                    case R.id.nt_image_2:
                        image =  image + "/2.png";
                        break;
                    case R.id.nt_image_3:
                        image =  image + "/3.png";
                        break;
                }
                Intent intent = new Intent(activity, ImageActivity.class);
                intent.putExtra("image",image);
                activity.startActivity(intent);
            }
        };
        //事件
        if(addListener){

            image1.setOnClickListener(listener);
            image2.setOnClickListener(listener);
            image3.setOnClickListener(listener);
            userView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, UserActivity.class);
                    intent.putExtra("user", user);
                    activity.startActivity(intent);
                }
            });
        }
        Glide.clear(image1);
        Glide.clear(image2);
        Glide.clear(image3);
        int num = orderItem.getImageNum();
        switch (num){
            case 0:
                imageLayout.setVisibility(View.GONE);
                break;
            case 1:
                imageLayout.setVisibility(View.VISIBLE);
                Glide.with(image1.getContext())
                        .load(HttpUtil.ORDER_IMAGE_URL + orderItem.getId() + "/" + "1.png")
                        .placeholder(R.drawable.ic_default_image)
                        .into(image1);
                image1.setVisibility(View.VISIBLE);
                image2.setVisibility(View.INVISIBLE);
                image3.setVisibility(View.INVISIBLE);
                break;
            case 2:
                imageLayout.setVisibility(View.VISIBLE);
                Glide.with(image1.getContext())
                        .load(HttpUtil.ORDER_IMAGE_URL + orderItem.getId() + "/" + "1.png")
                        .placeholder(R.drawable.ic_default_image)
                        .into(image1);
                Glide.with(image2.getContext())
                        .load(HttpUtil.ORDER_IMAGE_URL + orderItem.getId() + "/" + "2.png")
                        .placeholder(R.drawable.ic_default_image)
                        .into(image2);
                image1.setVisibility(View.VISIBLE);
                image2.setVisibility(View.VISIBLE);
                image3.setVisibility(View.INVISIBLE);
                break;
            case 3:
                imageLayout.setVisibility(View.VISIBLE);
                Glide.with(image1.getContext())
                        .load(HttpUtil.ORDER_IMAGE_URL + orderItem.getId() + "/" + "1.png")
                        .placeholder(R.drawable.ic_default_image)
                        .into(image1);
                Glide.with(image2.getContext())
                        .load(HttpUtil.ORDER_IMAGE_URL + orderItem.getId() + "/" + "2.png")
                        .placeholder(R.drawable.ic_default_image)
                        .into(image2);
                Glide.with(image3.getContext())
                        .load(HttpUtil.ORDER_IMAGE_URL + orderItem.getId() + "/" + "3.png")
                        .placeholder(R.drawable.ic_default_image)
                        .into(image3);
                image1.setVisibility(View.VISIBLE);
                image2.setVisibility(View.VISIBLE);
                image3.setVisibility(View.VISIBLE);
                break;
        }
    }
}
