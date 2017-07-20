package com.edu.schooltask.fragment.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.activity.LoginActivity;
import com.edu.schooltask.activity.TaskOrderActivity;
import com.edu.schooltask.adapter.OrderAdapter;
import com.edu.schooltask.base.BaseFragment;
import com.edu.schooltask.beans.TaskOrder;
import com.edu.schooltask.beans.User;
import com.edu.schooltask.event.LoginSuccessEvent;
import com.edu.schooltask.event.LogoutEvent;
import com.edu.schooltask.view.CustomLoadMoreView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import server.api.SchoolTask;
import server.api.task.order.GetTaskOrderEvent;

/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class OrderFragment extends BaseFragment {
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tipText;
    private List<TaskOrder> orderList = new ArrayList<>();

    int page = 0;

    public OrderFragment() {
        super(R.layout.fragment_order_page);
    }


    @Override
    protected void init(){
        swipeRefreshLayout = getView(R.id.op_srl);
        recyclerView = getView(R.id.op_rv);
        tipText = getView(R.id.op_tip);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                orderList.clear();
                page = 0;
                getTaskOrder();
            }
        });
        swipeRefreshLayout.setProgressViewOffset(true, 0,  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
                getResources().getDisplayMetrics()));   //改变刷新圆圈高度
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderAdapter = new OrderAdapter(R.layout.item_task_order, orderList);
        orderAdapter.openLoadAnimation();
        orderAdapter.setLoadMoreView(new CustomLoadMoreView());
        recyclerView.setAdapter(orderAdapter);

        //加载更多事件
        orderAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                getTaskOrder();
            }
        }, recyclerView);

        //点击订单事件
        orderAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getActivity(), TaskOrderActivity.class);
                intent.putExtra("order_id", orderList.get(position).getOrderId());
                startActivity(intent);
            }
        });

        if(mDataCache.getUser() == null){
            tipText.setText("请先登录");
        }
        else{   //本地用户已存在
            tipText.setText("您没有相关的订单");
            getTaskOrder();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        Log.d("F","create");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void checkEmpty(){
        if(orderList.size() == 0){
            tipText.setVisibility(View.VISIBLE);
            tipText.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
        }
        else{
            tipText.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetTaskOrder(GetTaskOrderEvent event){
        swipeRefreshLayout.setRefreshing(false);
        if(event.isOk()){
            tipText.setText("您没有相关的订单");
            page ++;
            List<TaskOrder> taskOrders = new Gson().fromJson(new Gson().toJson(event.getData()), new TypeToken<List<TaskOrder>>(){}.getType());
            User user = mDataCache.getUser();
            for(TaskOrder taskOrder : taskOrders){
                taskOrder.type = taskOrder.getReleaseUser().getUserId().equals(user.getUserId()) ?
                        0 : 1;
                taskOrder.initStateStr();
                orderList.add(taskOrder);
            }
            orderAdapter.loadMoreComplete();
            if(taskOrders.size() == 0){
                orderAdapter.loadMoreEnd();
            }
        }
        else{
            orderAdapter.loadMoreFail();
            tipText.setText("获取订单失败，请重试");
            checkEmpty();
            toastShort(event.getError());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginSuccess(LoginSuccessEvent event){
        tipText.setText("您没有相关的订单");
        getTaskOrder();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogout(LogoutEvent event){
        orderList.clear();
        orderAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
        tipText.setText("请先登录");
        tipText.setVisibility(View.VISIBLE);
        page = 0;
    }

    public void getTaskOrder(){
        if(mDataCache.getUser() != null){
            if(!swipeRefreshLayout.isRefreshing())swipeRefreshLayout.setRefreshing(true);
            tipText.setVisibility(View.GONE);
            SchoolTask.getUserOrder(page);
        }
        else{
            swipeRefreshLayout.setRefreshing(false);
            openActivity(LoginActivity.class);
        }
    }
}
