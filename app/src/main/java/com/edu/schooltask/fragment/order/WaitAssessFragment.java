package com.edu.schooltask.fragment.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.activity.LoginActivity;
import com.edu.schooltask.activity.TaskOrderActivity;
import com.edu.schooltask.adapter.OrderWaitAssessAdapter;
import com.edu.schooltask.base.BaseFragment;
import com.edu.schooltask.beans.TaskOrder;
import com.edu.schooltask.beans.User;
import com.edu.schooltask.event.LoginSuccessEvent;
import com.edu.schooltask.event.LogoutEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import server.api.SchoolTask;
import server.api.task.order.GetWaitAssessOrderEvent;

/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class WaitAssessFragment extends BaseFragment{

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tipText;
    private List<TaskOrder> taskOrderList = new ArrayList<>();
    private OrderWaitAssessAdapter orderWaitAssessAdapter;

    int page = 0;

    public boolean isFirst = true;

    public WaitAssessFragment() {
        super(R.layout.fragment_wait_assess);
    }

    @Override
    protected void init(){
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.wa_srl);
        recyclerView = (RecyclerView)view.findViewById(R.id.wa_rv);
        tipText = (TextView) view.findViewById(R.id.wa_tip);

        orderWaitAssessAdapter = new OrderWaitAssessAdapter(getActivity(), R.layout.item_order_wait_assess, taskOrderList);
        recyclerView.setAdapter(orderWaitAssessAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        orderWaitAssessAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                getWaitAssessOrder();
            }
        }, recyclerView);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                taskOrderList.clear();
                page = 0;
                getWaitAssessOrder();
            }
        });

        orderWaitAssessAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getActivity(), TaskOrderActivity.class);
                //intent.putExtra("orderid", taskOrderList.get(position).getId());
                startActivity(intent);
            }
        });

        if(mDataCache.getUser() == null){
            tipText.setText("请先登录");
        }
        else{   //本地用户已存在
            tipText.setText("您没有相关的订单");
            //getWaitAssessOrder();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void checkEmpty(){
        if(taskOrderList.size() == 0){
            tipText.setVisibility(View.VISIBLE);
            tipText.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
        }
        else{
            tipText.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetWaitAssessOrder(GetWaitAssessOrderEvent event){
        swipeRefreshLayout.setRefreshing(false);
        if(event.isOk()){
            page++;
            tipText.setText("您没有相关的订单");
            List<TaskOrder> taskOrders = event.getTaskOrders();
            User user = mDataCache.getUser();
            for(TaskOrder taskOrder : taskOrders){
                taskOrder.type = taskOrder.getReleaseUser().getUserId().equals(user.getUserId()) ?
                        0 : 1;
                taskOrder.initStateStr();
                taskOrderList.add(taskOrder);
            }
            orderWaitAssessAdapter.loadMoreComplete();
            if(taskOrders.size() == 0) {
                orderWaitAssessAdapter.loadMoreEnd();
            }
        }
        else{
            tipText.setText("获取订单失败，请重试");
            orderWaitAssessAdapter.loadMoreFail();
            toastShort(event.getError());
        }
        checkEmpty();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginSuccess(LoginSuccessEvent event){
        tipText.setText("您还没有相关的订单");
        getWaitAssessOrder();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogout(LogoutEvent event){
        taskOrderList.clear();
        orderWaitAssessAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
        tipText.setText("请先登录");
        tipText.setVisibility(View.VISIBLE);
        page = 0;
    }

    public void getWaitAssessOrder(){
        if(mDataCache.getUser() != null){
            if(!swipeRefreshLayout.isRefreshing())swipeRefreshLayout.setRefreshing(true);
            tipText.setVisibility(View.GONE);
            SchoolTask.getWaitAssessOrder(page);
        }
        else{
            swipeRefreshLayout.setRefreshing(false);
            openActivity(LoginActivity.class);
        }
    }
}
