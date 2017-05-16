package com.edu.schooltask.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.StateAdapter;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.beans.User;
import com.edu.schooltask.event.ChangeOrderStateEvent;
import com.edu.schooltask.event.GetOrderInfoEvent;
import com.edu.schooltask.http.HttpUtil;
import com.edu.schooltask.item.StateItem;
import com.edu.schooltask.utils.DialogUtil;
import com.edu.schooltask.utils.GlideUtil;
import com.edu.schooltask.view.InputText;
import com.orhanobut.dialogplus.DialogPlus;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends BaseActivity implements View.OnClickListener{
    private ScrollView orderLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
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

    private Button confirmBtn;
    private Button finishBtn;
    private Button overtimeBtn;
    private Button cancelBtn;
    private Button abandonBtn;
    private Button assessBtn;

    private RecyclerView stateRecyclerView;
    private StateAdapter adapter;
    private List<StateItem> stateList = new ArrayList<>();

    private String orderId;

    DialogPlus payDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        EventBus.getDefault().register(this);
        initView();
        getOrderInfo();
    }

    private void initView(){
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.order_srl);
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
        confirmBtn = (Button) findViewById(R.id.order_confirm_btn);
        finishBtn = (Button) findViewById(R.id.order_finish_btn);
        overtimeBtn = (Button) findViewById(R.id.order_overtime_btn);
        cancelBtn = (Button) findViewById(R.id.order_cancel_btn);
        abandonBtn = (Button) findViewById(R.id.order_abandon_btn);
        assessBtn = (Button) findViewById(R.id.order_assess_btn);


        stateRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StateAdapter(R.layout.item_order_state, stateList);
        stateRecyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOrderInfo();
            }
        });

        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderid");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        if (payDialog != null){
            if(payDialog.isShowing()){
                payDialog.dismiss();
                EventBus.getDefault().post(new ChangeOrderStateEvent("操作取消"));
            }
            else{
                finish();
            }
        }
        else{
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetOrderInfo(GetOrderInfoEvent event){
        if(swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
        if(event.isOk()){
            abandonBtn.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.GONE);
            confirmBtn.setVisibility(View.GONE);
            finishBtn.setVisibility(View.GONE);
            overtimeBtn.setVisibility(View.GONE);
            assessBtn.setVisibility(View.GONE);
            User user = mDataCache.getUser();
            if(user == null) return;
            JSONObject data = event.getData();
            try {
                String school = data.getString("order_school");
                String content = data.getString("order_content");
                float cost = (float)data.getDouble("order_cost");
                int imageNum = data.getInt("order_image_num");
                int state = data.getInt("order_state");
                String releaseTime = data.getString("order_release_time");
                String acceptTime = data.getString("order_accept_time");
                String finishTime = data.getString("order_finish_time");
                String assessTime = data.getString("order_assess_time");
                String overTime = data.getString("order_over_time");
                  String abandonTime = data.getString("order_abandon_time");
                String cancelTime = data.getString("order_cancel_time");

                String releaseUserId = data.getString("release_user_id");
                String releaseUserName = data.getString("release_user_name");
                String releaseUserSign = data.getString("release_user_sign");
                int releaseUserSex = data.getInt("release_user_sex");

                boolean isReleaseUser = user.getUserId().equals(releaseUserId);

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
                if(state != 0 && state != 5 && state != 6){
                    acceptUserId = data.getString("accept_user_id");
                    acceptUserName = data.getString("accept_user_name");
                    acceptUserSign = data.getString("accept_user_sign");
                    acceptUserSex = data.getInt("accept_user_sex");
                }
                stateList.add(new StateItem(true, "发布订单", releaseTime));
                switch (state){
                    case 0:
                        stateList.add(new StateItem(false, "等待接单", ""));
                        if(isReleaseUser){
                            cancelBtn.setVisibility(View.VISIBLE);
                            cancelBtn.setOnClickListener(this);
                        }
                        break;
                    case 1:
                        stateList.add(new StateItem(true, "已经接单", acceptTime, acceptUserId,
                                acceptUserName, acceptUserSex, acceptUserSign));
                        stateList.add(new StateItem(false, "等待完成", ""));
                        if(isReleaseUser){
                            overtimeBtn.setVisibility(View.VISIBLE);
                            overtimeBtn.setOnClickListener(this);
                        }
                        else{
                            abandonBtn.setVisibility(View.VISIBLE);
                            abandonBtn.setOnClickListener(this);
                            finishBtn.setVisibility(View.VISIBLE);
                            finishBtn.setOnClickListener(this);
                        }
                        break;
                    case 2:
                        stateList.add(new StateItem(true, "已经接单", acceptTime, acceptUserId,
                                acceptUserName, acceptUserSex, acceptUserSign));
                        stateList.add(new StateItem(true, "任务完成", finishTime));
                        stateList.add(new StateItem(true, "等待确认", "超过三天将自动确认"));
                        if(isReleaseUser) {
                            confirmBtn.setVisibility(View.VISIBLE);
                            confirmBtn.setOnClickListener(this);
                        }
                        break;
                    case 3:
                        stateList.add(new StateItem(true, "已经接单", acceptTime, acceptUserId,
                                acceptUserName, acceptUserSex, acceptUserSign));
                        stateList.add(new StateItem(true, "任务完成", finishTime));
                        stateList.add(new StateItem(false, "等待评价", ""));
                        if(isReleaseUser) {
                            assessBtn.setVisibility(View.VISIBLE);
                            assessBtn.setOnClickListener(this);
                        }
                        break;
                    case 4:
                        stateList.add(new StateItem(true, "已经接单", acceptTime, acceptUserId,
                                acceptUserName, acceptUserSex, acceptUserSign));
                        stateList.add(new StateItem(true, "任务完成", finishTime));
                        stateList.add(new StateItem(true, "已经评价", assessTime));
                        stateList.add(new StateItem(true, "订单完成", ""));
                        break;
                    case 5:
                        stateList.add(new StateItem(true, "订单超时", overTime));
                        stateList.add(new StateItem(true, "订单失效", ""));
                        break;
                    case 6:
                        stateList.add(new StateItem(true, "订单取消", cancelTime));
                        stateList.add(new StateItem(true, "订单失效", ""));
                        break;
                    case 7:
                        stateList.add(new StateItem(true, "已经接单", acceptTime, acceptUserId,
                                acceptUserName, acceptUserSex, acceptUserSign));
                        stateList.add(new StateItem(true, "放弃任务", abandonTime));
                        stateList.add(new StateItem(true, "订单失效", ""));
                        break;
                    case 8:
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
            stateList.clear();
            swipeRefreshLayout.setRefreshing(true);
            HttpUtil.getOrderInfo(user.getToken(), user.getUserId(), orderId);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeOrderState(ChangeOrderStateEvent event){
        if(event.isOk()){
            toastShort("成功");
            getOrderInfo();
        }
        else{
            toastShort(event.getError());
        }
    }

    @Override
    public void onClick(View v) {
        DialogPlus confirmDialog = null;
        switch (v.getId()){
            case R.id.order_cancel_btn:
                confirmDialog = DialogUtil.createYesNoDialog(this, "提示",
                        "是否要取消该任务", "取消后订单的金额(不包括时限支出)将退回您的账户", "是",
                        new DialogUtil.OnClickListener() {
                            @Override
                            public void onClick() {
                                final User user = mDataCache.getUser();
                                if(user != null){
                                    HttpUtil.changeOrderState(user.getToken(), orderId, user.getUserId(), 6);
                                }
                            }
                        }, "否");
                break;
            case R.id.order_overtime_btn:
                confirmDialog = DialogUtil.createYesNoDialog(this, "提示",
                        "任务已经超时？", "超时后订单的金额(不包括时限支出)将退回您的账户", "超时",
                        new DialogUtil.OnClickListener() {
                            @Override
                            public void onClick() {
                                final User user = mDataCache.getUser();
                                if(user != null){
                                    HttpUtil.changeOrderState(user.getToken(), orderId, user.getUserId(), 8);
                                }
                            }
                        }, "否");
                break;
            case R.id.order_confirm_btn:
                confirmDialog = DialogUtil.createYesNoDialog(this, "提示",
                        "确定任务已经完成？", "确定完成后接单人将收到您的付款", "确定",
                        new DialogUtil.OnClickListener() {
                            @Override
                            public void onClick() {
                                final User user = mDataCache.getUser();
                                if(user != null){
                                    HttpUtil.changeOrderState(user.getToken(), orderId, user.getUserId(), 3);
                                }
                            }
                        }, "取消");
                break;
            case R.id.order_finish_btn:
                confirmDialog = DialogUtil.createYesNoDialog(this, "提示",
                        "确定任务已经完成？", "完成任务时请尽量保留照片或者其余可以表明您已经完成任务的物件，以避免不必要的纠纷", "确定",
                        new DialogUtil.OnClickListener() {
                            @Override
                            public void onClick() {
                                final User user = mDataCache.getUser();
                                if(user != null){
                                    HttpUtil.changeOrderState(user.getToken(), orderId, user.getUserId(), 2);
                                }
                            }
                        }, "取消");
                break;
            case R.id.order_abandon_btn:
                confirmDialog = DialogUtil.createYesNoDialog(this, "提示",
                        "确定要放弃任务吗？", "放弃任务将会导致您的信用值降低，可信用值过低会造成您不能接受任务", "放弃",
                        new DialogUtil.OnClickListener() {
                            @Override
                            public void onClick() {
                                final User user = mDataCache.getUser();
                                if(user != null){
                                    HttpUtil.changeOrderState(user.getToken(), orderId, user.getUserId(), 7);
                                }
                            }
                        }, "取消");
                break;
        }
        if( confirmDialog != null) confirmDialog.show();
    }
}
