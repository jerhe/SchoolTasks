package com.edu.schooltask.fragment.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.activity.TaskOrderActivity;
import com.edu.schooltask.adapter.OrderAdapter;
import com.edu.schooltask.base.BaseFragment;
import com.edu.schooltask.beans.TaskOrder;
import com.edu.schooltask.event.LoginSuccessEvent;
import com.edu.schooltask.event.LogoutEvent;
import com.edu.schooltask.utils.UserUtil;
import com.edu.schooltask.view.recyclerview.TipRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import server.api.SchoolTask;
import server.api.task.order.GetTaskOrderEvent;

import static com.edu.schooltask.utils.GsonUtil.toTaskOrderList;

/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class OrderFragment extends BaseFragment {
    @BindView(R.id.op_tr) TipRecyclerView tipRecyclerView;

    private OrderAdapter orderAdapter;
    private List<TaskOrder> orderList = new ArrayList<>();

    public OrderFragment() {
        super(R.layout.fragment_order_page);
    }

    @Override
    protected void init(){
        ButterKnife.bind(this, view);
        orderAdapter = new OrderAdapter(R.layout.item_task_order, orderList);
        tipRecyclerView.init(orderAdapter);
        tipRecyclerView.setRefreshListener(new TipRecyclerView.RefreshListener() {
            @Override
            public void onRefresh(int page) {
                SchoolTask.getUserOrder(page);
            }
        });
        tipRecyclerView.setLoadMore(true);

        //点击订单事件
        orderAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getActivity(), TaskOrderActivity.class);
                intent.putExtra("order_id", orderList.get(position).getOrderId());
                startActivity(intent);
            }
        });
        orderAdapter.setEmptyView(R.layout.empty_order);
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

    //获取订单事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetTaskOrder(GetTaskOrderEvent event){
        tipRecyclerView.setRefreshing(false);
        if(event.isOk()){
            tipRecyclerView.loadSuccess();
            List<TaskOrder> taskOrders = toTaskOrderList(event.getData());
            for(TaskOrder taskOrder : taskOrders){
                taskOrder.type = taskOrder.getReleaseId().equals(UserUtil.getLoginUser().getUserId()) ? 0 : 1;
                orderList.add(taskOrder);
            }
            if(taskOrders.size() == 0) orderAdapter.loadMoreEnd();

        }
        else{
            tipRecyclerView.loadFail();
            toastShort(event.getError());
        }
        tipRecyclerView.notifyDataChanged();
    }

    //登录事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginSuccess(LoginSuccessEvent event){
        tipRecyclerView.login();
    }

    //登出事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogout(LogoutEvent event){
        tipRecyclerView.logout();
    }
}
