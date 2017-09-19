package com.edu.schooltask.fragment.main;

import android.view.View;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.TaskOrderAdapter;
import com.edu.schooltask.beans.BaseList;
import com.edu.schooltask.beans.task.TaskOrderItem;
import com.edu.schooltask.activity.LoginActivity;
import com.edu.schooltask.activity.TaskOrderActivity;
import com.edu.schooltask.base.BaseFragment;
import com.edu.schooltask.view.recyclerview.RefreshPageRecyclerView;
import com.edu.schooltask.view.task.TaskFilterView;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.UserUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import server.api.client.TaskClient;
import server.api.result.Result;

/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class OrderFragment extends BaseFragment {
    @BindView(R.id.op_tfv) TaskFilterView taskFilterView;

    @OnClick(R.id.op_filter_btn)
    public void filterBtn(){
        if(taskFilterView.isShown()) taskFilterView.hide();
        else taskFilterView.show();
    }

    private RefreshPageRecyclerView<TaskOrderItem> recyclerView;
    private RelativeLayout loginLayout;

    int type = 0;
    int state = 0;
    int sort = 0;

    public OrderFragment() {
        super(R.layout.fragment_order_page);
    }

    @Override
    protected void init(){
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
        taskFilterView.setSelectedIndex("类型", type);
        taskFilterView.setSelectedIndex("状态", state);
        taskFilterView.setSelectedIndex("排序", sort);

        recyclerView = new RefreshPageRecyclerView<TaskOrderItem>(getContext(), true) {
            @Override
            protected BaseQuickAdapter initAdapter(List<TaskOrderItem> list) {
                return new TaskOrderAdapter(list);
            }

            @Override
            protected void requestPageData(Result result, int page) {
                TaskClient.getUserOrderList(result, type, state, sort, page);
            }

            @Override
            protected void onSuccess(int id, Object data) {
                BaseList<TaskOrderItem> list = GsonUtil.toTaskOrderItemList(data);
                recyclerView.add(list.getList(), list.isMore());
            }

            @Override
            protected void onFailed(int id, int code, String error) {
                toastShort(error);
            }

            @Override
            protected void onItemClick(int position, TaskOrderItem taskOrderItem) {
                openActivity(TaskOrderActivity.class, "orderId", taskOrderItem.getOrderId());
            }
        };
        recyclerView.setEmptyView(R.layout.empty_order);
        recyclerView.setVisibility(View.GONE);
        addView(recyclerView);

        loginLayout = inflate(R.layout.layout_login);
        loginLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(LoginActivity.class);
            }
        });
        addView(loginLayout);
        if(UserUtil.hasLogin()){
            onLogin();
        }
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
            this.type = type;
            this.state = state;
            this.sort = sort;
            recyclerView.refresh();
            saveFilterSelected();
        }
    }

    @Override
    protected void onLogin() {
        loginLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.refresh();
    }

    @Override
    protected void onLogout() {
        loginLayout.setVisibility(View.VISIBLE);
        recyclerView.clear();
        recyclerView.setVisibility(View.GONE);
    }


}
