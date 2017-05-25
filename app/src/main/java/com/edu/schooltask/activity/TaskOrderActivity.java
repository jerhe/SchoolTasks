package com.edu.schooltask.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.ImageAdapter;
import com.edu.schooltask.adapter.StateAdapter;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.beans.TaskOrder;
import com.edu.schooltask.beans.User;
import com.edu.schooltask.beans.UserBaseInfo;
import com.edu.schooltask.utils.NetUtil;
import com.edu.schooltask.item.ImageItem;
import com.edu.schooltask.item.StateItem;
import com.edu.schooltask.utils.DialogUtil;
import com.edu.schooltask.utils.GlideUtil;
import com.edu.schooltask.view.InputText;
import com.orhanobut.dialogplus.DialogPlus;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import server.api.SchoolTask;
import server.api.task.order.ChangeTaskOrderStateEvent;
import server.api.task.order.GetTaskOrderInfoEvent;

public class TaskOrderActivity extends BaseActivity implements View.OnClickListener{
    private ScrollView orderLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private InputText orderIdText;
    private InputText orderSchoolText;
    private TextView orderContentText;
    private InputText orderCostText;
    private RecyclerView imageRecyclerView;

    private ImageView releaseHeadImage;
    private TextView releaseNameText;
    private TextView releaseSexText;
    private TextView releaseSchoolText;
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
        setContentView(R.layout.activity_task_order);
        EventBus.getDefault().register(this);
        initView();
        getTaskOrderInfo();
    }

    private void initView(){
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.order_srl);
        orderLayout = (ScrollView) findViewById(R.id.order_layout);
        orderIdText = (InputText) findViewById(R.id.order_orderid);
        orderSchoolText = (InputText) findViewById(R.id.order_school);
        orderContentText = (TextView) findViewById(R.id.order_content);
        orderCostText = (InputText) findViewById(R.id.order_cost);
        imageRecyclerView = (RecyclerView) findViewById(R.id.order_image_rv);

        releaseHeadImage = (ImageView) findViewById(R.id.order_release_head);
        releaseNameText = (TextView) findViewById(R.id.order_release_name);
        releaseSexText = (TextView) findViewById(R.id.order_release_sex);
        releaseSchoolText = (TextView) findViewById(R.id.order_release_school);
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
                getTaskOrderInfo();
            }
        });

        Intent intent = getIntent();
        orderId = intent.getStringExtra("order_id");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /*@Override
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
    }*/

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetTaskOrderInfo(GetTaskOrderInfoEvent event){
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
            TaskOrder taskOrder = event.getTaskOrder();
            UserBaseInfo releaseUser = taskOrder.getReleaseUser();
            User user = mDataCache.getUser();
            boolean isReleaseUser = user.getUserId().equals(releaseUser.getUserId());
            orderIdText.setText(orderId);
            orderSchoolText.setText(taskOrder.getSchool());
            orderContentText.setText(taskOrder.getContent());
            orderCostText.setText(taskOrder.getCost()+"元");

            //发布人
            GlideUtil.setHead(releaseHeadImage.getContext(), releaseHeadImage);
            releaseNameText.setText(releaseUser.getName());
            releaseSchoolText.setText(releaseUser.getSchool());
            switch (releaseUser.getSex()){
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

            //图片
            final List<ImageItem> imageItems = new ArrayList<>();
            String imageUrl = SchoolTask.TASK_IMAGE_URL + taskOrder.getOrderId() + "/";
            for(int i = 0; i< taskOrder.getImageNum(); i++){
                imageItems.add(new ImageItem(1,imageUrl + i + ".png"));
            }
            imageRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
            ImageAdapter imageAdapter = new ImageAdapter(R.layout.item_image, imageItems);
            imageAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    Intent intent = new Intent(TaskOrderActivity.this, ImageActivity.class);
                    intent.putExtra("editable", false);
                    intent.putExtra("index", position);
                    intent.putExtra("images", (Serializable) imageItems);
                    startActivity(intent);
                }
            });
            imageRecyclerView.setAdapter(imageAdapter);
            //状态
            int state = taskOrder.getState();
            UserBaseInfo acceptUser = taskOrder.getAcceptUser();
            stateList.add(new StateItem(true, "发布订单", taskOrder.getReleaseTime()));
            switch (state){
                case 0:
                    stateList.add(new StateItem(false, "等待接单", ""));
                    if(isReleaseUser){
                        cancelBtn.setVisibility(View.VISIBLE);
                        cancelBtn.setOnClickListener(this);
                    }
                    break;
                case 1:
                    stateList.add(new StateItem(true, "已经接单", taskOrder.getAcceptTime(), acceptUser));
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
                    stateList.add(new StateItem(true, "已经接单", taskOrder.getAcceptTime(), acceptUser));
                    stateList.add(new StateItem(true, "任务完成", taskOrder.getFinishTime()));
                    stateList.add(new StateItem(true, "等待确认", "超过三天将自动确认"));
                    if(isReleaseUser) {
                        confirmBtn.setVisibility(View.VISIBLE);
                        confirmBtn.setOnClickListener(this);
                    }
                    break;
                case 3:
                    stateList.add(new StateItem(true, "已经接单", taskOrder.getAcceptTime(), acceptUser));
                    stateList.add(new StateItem(true, "任务完成", taskOrder.getFinishTime()));
                    stateList.add(new StateItem(false, "等待评价", ""));
                    if(isReleaseUser) {
                        assessBtn.setVisibility(View.VISIBLE);
                        assessBtn.setOnClickListener(this);
                    }
                    break;
                case 4:
                    stateList.add(new StateItem(true, "已经接单", taskOrder.getAcceptTime(), acceptUser));
                    stateList.add(new StateItem(true, "任务完成", taskOrder.getFinishTime()));
                    stateList.add(new StateItem(true, "已经评价", taskOrder.getAssessTime()));
                    stateList.add(new StateItem(true, "订单完成", ""));
                    break;
                case 5:
                    stateList.add(new StateItem(true, "订单超时", taskOrder.getOverTime()));
                    stateList.add(new StateItem(true, "订单失效", ""));
                    break;
                case 6:
                    stateList.add(new StateItem(true, "订单取消", taskOrder.getCancelTime()));
                    stateList.add(new StateItem(true, "订单失效", ""));
                    break;
                case 7:
                    stateList.add(new StateItem(true, "已经接单", taskOrder.getAcceptTime(), acceptUser));
                    stateList.add(new StateItem(true, "放弃任务", taskOrder.getAbandonTime()));
                    stateList.add(new StateItem(true, "订单失效", ""));
                    break;
                case 8:
                    stateList.add(new StateItem(true, "已经接单", taskOrder.getAcceptTime(), acceptUser));
                    stateList.add(new StateItem(true, "任务超时", taskOrder.getOverTime()));
                    stateList.add(new StateItem(true, "订单失效", ""));
                    break;
            }
            adapter.notifyDataSetChanged();
            orderIdText.setInputEnable(false);
            orderSchoolText.setInputEnable(false);
            orderCostText.setInputEnable(false);
            orderLayout.setVisibility(View.VISIBLE);
            orderLayout.startAnimation(AnimationUtils.loadAnimation(TaskOrderActivity.this, R.anim.fade_in));
        }
        else{
            toastShort(event.getError());
        }
    }

    private void getTaskOrderInfo(){
        stateList.clear();
        swipeRefreshLayout.setRefreshing(true);
        SchoolTask.getTaskOrderInfo(orderId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeTaskOrderState(ChangeTaskOrderStateEvent event){
        if(event.isOk()){
            toastShort("成功");
            getTaskOrderInfo();
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
                                SchoolTask.changeTaskOrderState(orderId, 6);
                            }
                        }, "否");
                break;
            case R.id.order_overtime_btn:
                confirmDialog = DialogUtil.createYesNoDialog(this, "提示",
                        "任务已经超时？", "超时后订单的金额(不包括时限支出)将退回您的账户", "超时",
                        new DialogUtil.OnClickListener() {
                            @Override
                            public void onClick() {
                                SchoolTask.changeTaskOrderState(orderId, 8);
                            }
                        }, "否");
                break;
            case R.id.order_confirm_btn:
                confirmDialog = DialogUtil.createYesNoDialog(this, "提示",
                        "确定任务已经完成？", "确定完成后接单人将收到您的付款", "确定",
                        new DialogUtil.OnClickListener() {
                            @Override
                            public void onClick() {
                                SchoolTask.changeTaskOrderState(orderId, 3);
                            }
                        }, "取消");
                break;
            case R.id.order_finish_btn:
                confirmDialog = DialogUtil.createYesNoDialog(this, "提示",
                        "确定任务已经完成？", "完成任务时请尽量保留照片或者其余可以表明您已经完成任务的物件，以避免不必要的纠纷", "确定",
                        new DialogUtil.OnClickListener() {
                            @Override
                            public void onClick() {
                                SchoolTask.changeTaskOrderState(orderId, 2);
                            }
                        }, "取消");
                break;
            case R.id.order_abandon_btn:
                confirmDialog = DialogUtil.createYesNoDialog(this, "提示",
                        "确定要放弃任务吗？", "放弃任务将会导致您的信用值降低，可信用值过低会造成您不能接受任务", "放弃",
                        new DialogUtil.OnClickListener() {
                            @Override
                            public void onClick() {
                                SchoolTask.changeTaskOrderState(orderId, 7);
                            }
                        }, "取消");
                break;
        }
        if( confirmDialog != null) confirmDialog.show();
    }
}
