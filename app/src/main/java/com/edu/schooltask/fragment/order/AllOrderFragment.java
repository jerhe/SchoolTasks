package com.edu.schooltask.fragment.order;

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

import java.util.ArrayList;
import java.util.List;

import com.edu.schooltask.activity.LoginActivity;
import com.edu.schooltask.adapter.OrderAdapter;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.base.BaseFragment;
import com.edu.schooltask.beans.User;
import com.edu.schooltask.event.GetUserOrderEvent;
import com.edu.schooltask.event.LoginEvent;
import com.edu.schooltask.event.LoginSuccessEvent;
import com.edu.schooltask.event.LogoutEvent;
import com.edu.schooltask.http.HttpCheckToken;
import com.edu.schooltask.http.HttpUtil;
import com.edu.schooltask.item.OrderItem;
import com.edu.schooltask.view.OrderTypeMenu;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class AllOrderFragment extends BaseFragment{

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tipText;
    private List<OrderItem> typeOrderList = new ArrayList<>();
    private List<OrderItem> allOrderList = new ArrayList<>();
    private OrderTypeMenu orderTypeMenu;

    int pageIndex = 0;

    public AllOrderFragment() {
        super(R.layout.fragment_all_order);
    }

    @Override
    protected void init(){
        orderTypeMenu = (OrderTypeMenu) view.findViewById(R.id.ao_menu);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.ao_srl);
        recyclerView = (RecyclerView) view.findViewById(R.id.ao_rv);
        tipText = (TextView) view.findViewById(R.id.ao_tip);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                allOrderList.clear();
                pageIndex = 0;
                getUserOrder();
            }
        });
        swipeRefreshLayout.setProgressViewOffset(true, 0,  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
                getResources().getDisplayMetrics()));   //改变刷新圆圈高度
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderAdapter = new OrderAdapter(R.layout.item_order, typeOrderList);
        orderAdapter.addHeaderView(LayoutInflater.from(getContext()).inflate(R.layout.header_order_empty,null));
        orderAdapter.openLoadAnimation();
        recyclerView.setAdapter(orderAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public boolean isScroll = false;
            int direction = 0;  //滚动方向  用于判断拖曳
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == 0) {
                    isScroll = false;
                    direction = 0;
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy < 0) {
                    if(direction == -1) isScroll = false;
                    direction = 1;
                }
                if(dy > 0){
                    if(direction == 1) isScroll = false;
                    direction = -1;
                }
                if(!isScroll) {
                    isScroll = true;
                    if (dy < 0) {
                        if(!orderTypeMenu.isShown()){
                            orderTypeMenu.setVisibility(View.VISIBLE);
                            orderTypeMenu.startAnimation(
                                    AnimationUtils.loadAnimation(getContext(),R.anim.translate_up_to_down));
                        }
                    } else if(dy > 0){
                        if(orderTypeMenu.isShown()){
                            orderTypeMenu.setVisibility(View.INVISIBLE);
                            orderTypeMenu.startAnimation(
                                    AnimationUtils.loadAnimation(getContext(),R.anim.translate_down_to_up));
                        }
                    }
                    if(dy == 0) {
                        isScroll = false;
                        direction = 0;
                    }
                }
            }
        });
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        orderAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                getUserOrder();
            }
        }, recyclerView);
        orderTypeMenu.setOnMenuSelectedListener(new OrderTypeMenu.OnMenuItemSelectedListener() {
            @Override
            public void OnMenuItemSelected(int position) {
                addToTypeOrderList(position);
            }
        });
        orderAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                toastShort(typeOrderList.get(position).getContent());
            }
        });
        orderTypeMenu.setSelectItem(0);
        if(mDataCache.getUser() == null){
            tipText.setText("请先登录");
        }
        else{   //本地用户已存在
            tipText.setText("您没有相关的订单");
            getUserOrder();
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

    private void addToTypeOrderList(int position){
        typeOrderList.clear();
        switch (position){
            case 0:    //所有
                typeOrderList.addAll(allOrderList);
                break;
            default:    //position对应订单状态
                for(OrderItem orderItem : allOrderList){
                    if(orderItem.getState() == position - 1){
                        typeOrderList.add(orderItem);
                    }
                }
                break;
        }
        orderAdapter.notifyDataSetChanged();
        checkEmpty();
    }

    public void checkEmpty(){
        if(typeOrderList.size() == 0){
            tipText.setVisibility(View.VISIBLE);
            tipText.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
        }
        else{
            tipText.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetUserOrder(GetUserOrderEvent event){
        if(event.type == 0){
            swipeRefreshLayout.setRefreshing(false);
            if(event.isOk()){
                tipText.setText("您没有相关的订单");
                pageIndex ++;
                try{
                    JSONArray jsonArray = event.getData().getJSONArray("orders");
                    List<OrderItem> orders = new ArrayList<>();
                    for(int i=0; i<jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        OrderItem order = new OrderItem(
                                jsonObject.getString("orderid"),
                                jsonObject.getInt("type"),
                                jsonObject.getString("content"),
                                (float)jsonObject.getDouble("cost"),
                                jsonObject.getInt("state"));
                        orders.add(order);
                    }
                    orderAdapter.loadMoreComplete();
                    if(orders.size() < 5){
                        orderAdapter.loadMoreEnd();
                    }
                    allOrderList.addAll(orders);
                }catch (JSONException e){
                    toastShort("数据异常");
                    e.printStackTrace();
                }
                addToTypeOrderList(orderTypeMenu.getPosition());
            }
            else{
                orderAdapter.loadMoreFail();
                tipText.setText("获取订单失败，请重试");
                checkEmpty();
                toastShort(event.getError());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginSuccess(LoginSuccessEvent event){
        tipText.setText("您没有相关的订单");
        getUserOrder();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogout(LogoutEvent event){
        allOrderList.clear();
        typeOrderList.clear();
        orderAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
        tipText.setText("请先登录");
        tipText.setVisibility(View.VISIBLE);
        pageIndex = 0;
    }

    public void getUserOrder(){
        if(mDataCache.getUser() != null){
            if(!swipeRefreshLayout.isRefreshing())swipeRefreshLayout.setRefreshing(true);
            tipText.setVisibility(View.GONE);
            User user = mDataCache.getUser();
            HttpUtil.getUserOrder(user.getToken(), user.getUserId(), pageIndex, 0);
        }
        else{
            swipeRefreshLayout.setRefreshing(false);
            openActivity(LoginActivity.class);
        }
    }
}
