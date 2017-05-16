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
import com.edu.schooltask.activity.OrderActivity;
import com.edu.schooltask.adapter.OrderWaitAssessAdapter;
import com.edu.schooltask.base.BaseFragment;
import com.edu.schooltask.beans.User;
import com.edu.schooltask.event.GetAssessOrderEvent;
import com.edu.schooltask.event.GetUserOrderEvent;
import com.edu.schooltask.event.LoginSuccessEvent;
import com.edu.schooltask.event.LogoutEvent;
import com.edu.schooltask.http.HttpUtil;
import com.edu.schooltask.item.OrderItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class WaitAssessFragment extends BaseFragment{

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tipText;
    private List<OrderItem> orderItemList = new ArrayList<>();
    private OrderWaitAssessAdapter orderWaitAssessAdapter;

    int pageIndex = 0;

    public WaitAssessFragment() {
        super(R.layout.fragment_wait_assess);
    }

    @Override
    protected void init(){
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.wa_srl);
        recyclerView = (RecyclerView)view.findViewById(R.id.wa_rv);
        tipText = (TextView) view.findViewById(R.id.wa_tip);

        orderWaitAssessAdapter = new OrderWaitAssessAdapter(getActivity(), R.layout.item_order_wait_assess, orderItemList);
        recyclerView.setAdapter(orderWaitAssessAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        orderWaitAssessAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                getAssessOrder();
            }
        }, recyclerView);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                orderItemList.clear();
                pageIndex = 0;
                getAssessOrder();
            }
        });

        orderWaitAssessAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getActivity(), OrderActivity.class);
                intent.putExtra("orderid", orderItemList.get(position).getId());
                startActivity(intent);
            }
        });

        if(mDataCache.getUser() == null){
            tipText.setText("请先登录");
        }
        else{   //本地用户已存在
            tipText.setText("您没有相关的订单");
            getAssessOrder();
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
        if(orderItemList.size() == 0){
            tipText.setVisibility(View.VISIBLE);
            tipText.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
        }
        else{
            tipText.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetAssessOrder(GetAssessOrderEvent event){
        swipeRefreshLayout.setRefreshing(false);
        if(event.isOk()){
            pageIndex ++;
            tipText.setText("您没有相关的订单");
            try{
                JSONArray jsonArray = event.getData().getJSONArray("orders");
                List<OrderItem> orders = new ArrayList<>();
                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    OrderItem order = new OrderItem(
                            jsonObject.getString("order_id"),
                            jsonObject.getInt("type"),
                            jsonObject.getString("content"),
                            jsonObject.getString("release_time"),
                            jsonObject.getInt("cost"));
                    orders.add(order);
                }
                orderWaitAssessAdapter.loadMoreComplete();
                if(orders.size() < 5){
                    orderWaitAssessAdapter.loadMoreEnd();
                }
                orderItemList.addAll(orders);
            }catch (JSONException e){
                toastShort("数据异常");
                e.printStackTrace();
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
        getAssessOrder();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogout(LogoutEvent event){
        orderItemList.clear();
        orderWaitAssessAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
        tipText.setText("请先登录");
        tipText.setVisibility(View.VISIBLE);
        pageIndex = 0;
    }

    public void getAssessOrder(){
        if(mDataCache.getUser() != null){
            if(!swipeRefreshLayout.isRefreshing())swipeRefreshLayout.setRefreshing(true);
            tipText.setVisibility(View.GONE);
            User user = mDataCache.getUser();
            HttpUtil.getAssessOrder(user.getToken(), user.getUserId(), pageIndex);
        }
        else{
            swipeRefreshLayout.setRefreshing(false);
            openActivity(LoginActivity.class);
        }
    }
}
