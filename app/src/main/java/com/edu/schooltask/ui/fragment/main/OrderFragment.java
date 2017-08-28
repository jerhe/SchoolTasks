package com.edu.schooltask.ui.fragment.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.ui.activity.TaskOrderActivity;
import com.edu.schooltask.adapter.TaskOrderAdapter;
import com.edu.schooltask.ui.base.BaseFragment;
import com.edu.schooltask.beans.task.TaskOrderItem;
import com.edu.schooltask.event.LoginSuccessEvent;
import com.edu.schooltask.event.LogoutEvent;
import com.edu.schooltask.ui.view.TaskFilterView;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.UserUtil;
import com.edu.schooltask.ui.view.recyclerview.TipRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import server.api.SchoolTask;
import server.api.task.accept.AcceptTaskEvent;
import server.api.task.order.GetTaskOrderListEvent;
import server.api.task.release.ReleaseTaskEvent;

/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class OrderFragment extends BaseFragment {
    @BindView(R.id.op_filter_btn) TextView filterBtn;
    @BindView(R.id.op_tfv) TaskFilterView taskFilterView;
    @BindView(R.id.op_shadow) View shadow;
    @BindView(R.id.op_tr) TipRecyclerView tipRecyclerView;

    @OnClick(R.id.op_filter_btn)
    public void filterBtn(){
        if(taskFilterView.isShown()) taskFilterView.hide();
        else taskFilterView.show();
    }
    @OnClick(R.id.op_shadow)
    public void shadow(){
        if(taskFilterView.isShown()){
            taskFilterView.hide();
        }
    }

    int type = 0;
    int state = 0;
    int sort = 0;

    private TaskOrderAdapter taskOrderAdapter;
    private List<TaskOrderItem> orderList = new ArrayList<>();

    public OrderFragment() {
        super(R.layout.fragment_order_page);
    }

    @Override
    protected void init(){
        ButterKnife.bind(this, view);
        taskFilterView.setShadowView(shadow);
        taskFilterView.addFilter("类型", "所有","发布的任务","接受的任务");
        taskFilterView.addFilter("状态", "所有","待完成订单","完成订单","失效订单");
        taskFilterView.addFilter("排序", "按发布时间排序","按状态排序");
        taskFilterView.setFilterChangeListener(new TaskFilterView.FilterChangeListener() {
            @Override
            public void onFilterChange() {
                getUserOrderList();
            }
        });
        type = mDataCache.getInt("filterType", 0);
        state = mDataCache.getInt("filterState", 0);
        sort = mDataCache.getInt("filterSort", 0);

        taskOrderAdapter = new TaskOrderAdapter(orderList);
        tipRecyclerView.init(taskOrderAdapter);
        tipRecyclerView.setRefreshListener(new TipRecyclerView.RefreshListener() {
            @Override
            public void onRefresh(int page) {
                getUserOrderList();
            }
        });
        tipRecyclerView.setLoadMore(true);

        //点击订单事件
        taskOrderAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getActivity(), TaskOrderActivity.class);
                intent.putExtra("orderId", orderList.get(position).getOrderId());
                startActivity(intent);
            }
        });
        taskOrderAdapter.setEmptyView(R.layout.empty_order);
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

    private void saveFilterSelected(){
        mDataCache.saveData("filterType", type);
        mDataCache.saveData("filterState", state);
        mDataCache.saveData("filterSort", sort);
    }

    private void getUserOrderList(){
        int type = taskFilterView.getSelectedIndex("类型");
        int state = taskFilterView.getSelectedIndex("状态");
        int sort = taskFilterView.getSelectedIndex("排序");
        if(this.type != type || this.state != state || this.sort != sort){
            tipRecyclerView.clear();
            this.type = type;
            this.state = state;
            this.sort = sort;
        }
        saveFilterSelected();
        SchoolTask.getUserOrderList(type, state, sort, tipRecyclerView.getPage());
    }

    //获取订单事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetTaskOrderList(GetTaskOrderListEvent event){
        tipRecyclerView.setRefreshing(false);
        if(event.isOk()){
            tipRecyclerView.loadSuccess();
            List<TaskOrderItem> list = GsonUtil.toTaskOrderItem(event.getData());
            String userId = UserUtil.getLoginUser().getUserId();
            for(TaskOrderItem taskOrderItem : list){
                taskOrderItem.setType(taskOrderItem.getReleaseId().equals(userId) ? 0 : 1);
                orderList.add(taskOrderItem);
            }
            if(list.size() == 0) taskOrderAdapter.loadMoreEnd();
        }
        else{
            tipRecyclerView.loadFail();
            toastShort(event.getError());
        }
        tipRecyclerView.notifyDataChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReleaseTask(ReleaseTaskEvent event){
        if(event.isOk()){
            tipRecyclerView.refresh();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAcceptTask(AcceptTaskEvent event){
        if(event.isOk()){
            tipRecyclerView.refresh();
        }
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
