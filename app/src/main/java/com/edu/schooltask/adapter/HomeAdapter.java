package com.edu.schooltask.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.activity.ImageActivity;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.beans.User;
import com.edu.schooltask.data.DataCache;
import com.edu.schooltask.http.HttpUtil;
import com.edu.schooltask.item.HomeItem;
import com.edu.schooltask.item.OrderItem;
import com.edu.schooltask.utils.DateUtil;

import java.util.Calendar;
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
        addItemType(HomeItem.NEAR_TASK_ITEM, R.layout.item_near_task);
    }

    @Override
    protected void convert(final BaseViewHolder helper, HomeItem item) {
        switch (item.getItemType()){
            case 0:
                helper.setText(R.id.lf_text,item.text);
                break;
            case 1:
                final OrderItem orderItem = item.orderItem;
                User user = orderItem.getReleaseUser();
                helper.setText(R.id.nt_name, user.getName());
                helper.setText(R.id.nt_school, orderItem.getSchool());
                helper.setText(R.id.nt_cost, "￥"+ orderItem.getCost());
                helper.setText(R.id.nt_content, orderItem.getContent());
                helper.setText(R.id.nt_release_time,
                        DateUtil.getLong(DateUtil.stringToDate(orderItem.getReleaseTime())));
                if(user.getSex() == 0){
                    helper.setText(R.id.nt_sex, "♂");
                    helper.setTextColor(R.id.nt_sex, Color.parseColor("#1B9DFF"));
                }
                if(user.getSex() == 1){
                    helper.setText(R.id.nt_sex, "♀");
                    helper.setTextColor(R.id.nt_sex, Color.parseColor("#FF3333"));
                }
                if(user.getSex() == -1){
                    helper.setText(R.id.nt_sex, "");
                }
                //头像
                final ImageView headView = helper.getView(R.id.nt_head);
                Glide.with(headView.getContext())
                        .load(R.drawable.head)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.head)
                        .into(new BitmapImageViewTarget(headView){
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(headView.getResources(),resource);
                                roundedBitmapDrawable.setCircular(true);
                                headView.setImageDrawable(roundedBitmapDrawable);
                            }
                        });
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
                image1.setOnClickListener(listener);
                image2.setOnClickListener(listener);
                image3.setOnClickListener(listener);
                int num = orderItem.getImageNum();
                switch (num){
                    case 0:
                        imageLayout.setVisibility(View.GONE);
                        break;
                    case 1:
                        imageLayout.setVisibility(View.VISIBLE);
                        Glide.with(image1.getContext())
                                .load(HttpUtil.ORDER_IMAGE_URL + orderItem.getId() + "/" + "1.png")
                                .into(image1);
                        image1.setVisibility(View.VISIBLE);
                        image2.setVisibility(View.INVISIBLE);
                        image3.setVisibility(View.INVISIBLE);
                        break;
                    case 2:
                        imageLayout.setVisibility(View.VISIBLE);
                        Glide.with(image1.getContext())
                                .load(HttpUtil.ORDER_IMAGE_URL + orderItem.getId() + "/" + "1.png")
                                .into(image1);
                        Glide.with(image2.getContext())
                                .load(HttpUtil.ORDER_IMAGE_URL + orderItem.getId() + "/" + "2.png")
                                .into(image2);
                        image1.setVisibility(View.VISIBLE);
                        image2.setVisibility(View.VISIBLE);
                        image3.setVisibility(View.INVISIBLE);
                        break;
                    case 3:
                        imageLayout.setVisibility(View.VISIBLE);
                        Glide.with(image1.getContext())
                                .load(HttpUtil.ORDER_IMAGE_URL + orderItem.getId() + "/" + "1.png")
                                .into(image1);
                        Glide.with(image2.getContext())
                                .load(HttpUtil.ORDER_IMAGE_URL + orderItem.getId() + "/" + "2.png")
                                .into(image2);
                        Glide.with(image3.getContext())
                                .load(HttpUtil.ORDER_IMAGE_URL + orderItem.getId() + "/" + "3.png")
                                .into(image3);
                        image1.setVisibility(View.VISIBLE);
                        image2.setVisibility(View.VISIBLE);
                        image3.setVisibility(View.VISIBLE);
                        break;
                }
        }
    }
}
