package com.edu.schooltask.activity;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.StateAdapter;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.beans.User;
import com.edu.schooltask.event.GetOrderInfoEvent;
import com.edu.schooltask.http.HttpUtil;
import com.edu.schooltask.item.StateItem;
import com.edu.schooltask.utils.GlideUtil;
import com.edu.schooltask.view.InputText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends BaseActivity {
    private ScrollView orderLayout;
    private InputText orderIdText;
    private InputText orderSchoolText;
    private TextView orderContentText;
    private InputText orderCostText;
    private LinearLayout imageLayout;
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;

    private ImageView releaseHeadImage;
    private TextView releaseNameText;
    private TextView releaseSexText;
    private TextView releaseSignText;
    private ImageView releaseTalkImage;

    private RecyclerView stateRecyclerView;
    private StateAdapter adapter;
    private List<StateItem> stateList = new ArrayList<>();

    private String orderId;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        EventBus.getDefault().register(this);
        initView();
        getOrderInfo();
    }

    private void initView(){
        orderLayout = (ScrollView) findViewById(R.id.order_layout);
        orderIdText = (InputText) findViewById(R.id.order_orderid);
        orderSchoolText = (InputText) findViewById(R.id.order_school);
        orderContentText = (TextView) findViewById(R.id.order_content);
        orderCostText = (InputText) findViewById(R.id.order_cost);
        imageLayout = (LinearLayout) findViewById(R.id.order_image_layout);
        imageView1 = (ImageView) findViewById(R.id.order_image_1);
        imageView2 = (ImageView) findViewById(R.id.order_image_2);
        imageView3 = (ImageView) findViewById(R.id.order_image_3);
        releaseHeadImage = (ImageView) findViewById(R.id.order_release_head);
        releaseNameText = (TextView) findViewById(R.id.order_release_name);
        releaseSexText = (TextView) findViewById(R.id.order_release_sex);
        releaseSignText = (TextView) findViewById(R.id.order_release_sign);
        releaseTalkImage = (ImageView) findViewById(R.id.order_release_talk);
        stateRecyclerView = (RecyclerView) findViewById(R.id.order_state_rv);

        stateRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StateAdapter(R.layout.item_order_state, stateList);
        stateRecyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderid");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetOrderInfo(GetOrderInfoEvent event){
        if(progressDialog != null){
            if (progressDialog.isShowing()) progressDialog.dismiss();
        }
        if(event.isOk()){
            JSONObject data = event.getData();
            try {
                String school = data.getString("order_school");
                String content = data.getString("order_content");
                float cost = (float)data.getDouble("order_cost");
                int imageNum = data.getInt("order_imagenum");
                int state = data.getInt("order_state");
                String releaseTime = data.getString("order_releasetime");
                String acceptTime = data.getString("order_accepttime");
                String finishTime = data.getString("order_finishtime");
                String assessTime = data.getString("order_assesstime");
                String overTime = data.getString("order_overtime");
                  String abandonTime = data.getString("order_abandontime");
                String cancleTime = data.getString("order_cancletime");

                String releaseUserId = data.getString("releaseuser_id");
                String releaseUserName = data.getString("releaseuser_name");
                String releaseUserSign = data.getString("releaseuser_sign");
                int releaseUserSex = data.getInt("releaseuser_sex");

                orderIdText.setText(orderId);
                orderSchoolText.setText(school);
                orderContentText.setText(content);
                orderCostText.setText(cost+"元");

                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String image = HttpUtil.ORDER_IMAGE_URL + orderId;
                        switch (v.getId()){
                            case R.id.order_image_1:
                                image =  image + "/1.png";
                                break;
                            case R.id.order_image_2:
                                image =  image + "/2.png";
                                break;
                            case R.id.order_image_3:
                                image =  image + "/3.png";
                                break;
                        }
                        Intent intent = new Intent(OrderActivity.this, ImageActivity.class);
                        intent.putExtra("image",image);
                        startActivity(intent);
                    }
                };
                if(imageNum == 0) imageLayout.setVisibility(View.GONE);
                if(imageNum > 0){
                    Glide.with(imageView1.getContext())
                            .load(HttpUtil.ORDER_IMAGE_URL + orderId + "/" + "1.png")
                            .placeholder(R.drawable.ic_default_image)
                            .into(imageView1);
                    imageView1.setOnClickListener(listener);
                }
                if(imageNum > 1){
                    Glide.with(imageView2.getContext())
                            .load(HttpUtil.ORDER_IMAGE_URL + orderId + "/" + "2.png")
                            .placeholder(R.drawable.ic_default_image)
                            .into(imageView2);
                    imageView2.setOnClickListener(listener);
                }
                else{
                    imageView2.setVisibility(View.INVISIBLE);
                    imageView3.setVisibility(View.INVISIBLE);
                }
                if(imageNum > 2){
                    Glide.with(imageView3.getContext())
                            .load(HttpUtil.ORDER_IMAGE_URL + orderId + "/" + "3.png")
                            .placeholder(R.drawable.ic_default_image)
                            .into(imageView3);
                    imageView3.setOnClickListener(listener);
                }
                else imageView3.setVisibility(View.INVISIBLE);

                //发布人
                GlideUtil.setHead(releaseHeadImage.getContext(), releaseHeadImage);
                releaseNameText.setText(releaseUserName);
                releaseSignText.setText(releaseUserSign);
                switch (releaseUserSex){
                    case -1:
                        releaseSexText.setText("");
                        break;
                    case 0:
                        releaseSexText.setText("♂");
                        releaseSexText.setTextColor(Color.parseColor("#1B9DFF"));
                        break;
                    case 1:
                        releaseSexText.setText("♀");
                        releaseSexText.setTextColor(Color.RED);
                        break;
                }

                //状态
                String acceptUserId = null;
                String acceptUserName = null;
                String acceptUserSign = null;
                int acceptUserSex = -1;
                if(state != 0){
                    acceptUserId = data.getString("acceptuser_id");
                    acceptUserName = data.getString("acceptuser_name");
                    acceptUserSign = data.getString("acceptuser_sign");
                    acceptUserSex = data.getInt("acceptuser_sex");
                }
                stateList.add(new StateItem(true, "发布订单", releaseTime));
                switch (state){
                    case 0:
                        stateList.add(new StateItem(false, "等待接单", ""));
                        break;
                    case 1:
                        stateList.add(new StateItem(true, "已经接单", acceptTime, acceptUserId,
                                acceptUserName, acceptUserSex, acceptUserSign));
                        stateList.add(new StateItem(false, "等待完成", ""));
                        break;
                    case 2:
                        stateList.add(new StateItem(true, "已经接单", acceptTime, acceptUserId,
                                acceptUserName, acceptUserSex, acceptUserSign));
                        stateList.add(new StateItem(true, "任务完成", finishTime));
                        stateList.add(new StateItem(false, "等待评价", ""));
                        break;
                    case 3:
                        stateList.add(new StateItem(true, "已经接单", acceptTime, acceptUserId,
                                acceptUserName, acceptUserSex, acceptUserSign));
                        stateList.add(new StateItem(true, "任务完成", finishTime));
                        stateList.add(new StateItem(true, "已经评价", assessTime));
                        stateList.add(new StateItem(true, "订单完成", ""));
                        break;
                    case 4:
                        stateList.add(new StateItem(true, "已经接单", acceptTime, acceptUserId,
                                acceptUserName, acceptUserSex, acceptUserSign));
                        stateList.add(new StateItem(true, "订单超时", overTime));
                        stateList.add(new StateItem(true, "订单失效", ""));
                        break;
                    case 5:
                        stateList.add(new StateItem(true, "已经接单", acceptTime, acceptUserId,
                                acceptUserName, acceptUserSex, acceptUserSign));
                        stateList.add(new StateItem(true, "订单取消", cancleTime));
                        stateList.add(new StateItem(true, "订单失效", ""));
                        break;
                    case 6:
                        stateList.add(new StateItem(true, "已经接单", acceptTime, acceptUserId,
                                acceptUserName, acceptUserSex, acceptUserSign));
                        stateList.add(new StateItem(true, "放弃任务", abandonTime));
                        stateList.add(new StateItem(true, "订单失效", ""));
                        break;
                    case 7:
                        stateList.add(new StateItem(true, "已经接单", acceptTime, acceptUserId,
                                acceptUserName, acceptUserSex, acceptUserSign));
                        stateList.add(new StateItem(true, "任务超时", overTime));
                        stateList.add(new StateItem(true, "订单失效", ""));
                        break;
                }
                adapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            orderIdText.setInputEnable(false);
            orderSchoolText.setInputEnable(false);
            orderCostText.setInputEnable(false);

            orderLayout.setVisibility(View.VISIBLE);
            orderLayout.startAnimation(AnimationUtils.loadAnimation(OrderActivity.this, R.anim.fade_in));
        }
        else{
            toastShort(event.getError());
        }
    }

    private void getOrderInfo(){
        User user = mDataCache.getUser();
        if(user != null){
            progressDialog = ProgressDialog.show(this, "", "加载中", true, false);
            HttpUtil.getOrderInfo(user.getToken(), user.getUserId(), orderId);
        }
    }
}
