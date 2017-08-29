package com.edu.schooltask.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.TaskItemAdapter;
import com.edu.schooltask.ui.base.BaseActivity;
import com.edu.schooltask.beans.UserInfoWithToken;
import com.edu.schooltask.beans.task.TaskItem;
import com.edu.schooltask.ui.view.TaskFilterView;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.UserUtil;
import com.edu.schooltask.ui.view.CustomLoadMoreView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import server.api.SchoolTask;
import server.api.event.task.GetTaskListEvent;

public class TaskListActivity extends BaseActivity {
    @BindView(R.id.tl_prl) PullRefreshLayout refreshLayout;
    @BindView(R.id.tl_rv) RecyclerView recyclerView;
    @BindView(R.id.tl_search_text) EditText searchText;
    @BindView(R.id.tl_tfv) TaskFilterView taskFilterView;
    @BindView(R.id.tl_tf_btn) TextView taskFilterBtn;
    @BindView(R.id.tl_shadow) View shadow;

    @OnClick(R.id.tl_tf_btn)
    public void textFilter(){
        if(taskFilterView.isShown()) taskFilterView.hide();
        else taskFilterView.show();

    }
    @OnClick(R.id.tl_shadow)
    public void shadow(){
        if(shadow.isShown()){
            taskFilterView.hide();
        }
    }
    @OnTextChanged(R.id.tl_search_text)
    public void searchTextChanged(){
        getTaskList();
    }


    private TaskItemAdapter adapter;
    private List<TaskItem> taskItems = new ArrayList<>();

    int page = 0;
    String school;
    String des;
    String search;
    BigDecimal minCost = new BigDecimal(0);
    BigDecimal maxCost = new BigDecimal(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        if(taskFilterView.isShown()){
            taskFilterView.hide();
        }
        else{
            super.onBackPressed();
        }
    }

    private void init(){
        UserInfoWithToken user = UserUtil.getLoginUser();
        taskFilterView.setShadowView(shadow);
        taskFilterView.addFilter(getString(R.string.school), "所有学校", user == null ? null : user.getSchool());
        taskFilterView.addFilter(getString(R.string.description), getResources().getStringArray(R.array.taskDescription));
        taskFilterView.addFilter(getString(R.string.reward), getResources().getStringArray(R.array.taskReward));
        taskFilterView.setFilterChangeListener(new TaskFilterView.FilterChangeListener() {
            @Override
            public void onFilterChange() {
                getTaskList();
            }
        });
        searchText.setHintTextColor(Color.GRAY);
        adapter = new TaskItemAdapter(taskItems);
        adapter.setLoadMoreView(new CustomLoadMoreView());
        adapter.openLoadAnimation();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.setEnableLoadMore(true);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(TaskListActivity.this, WaitAcceptOrderActivity.class);
                intent.putExtra("taskItem", taskItems.get(position));
                startActivity(intent);
            }
        });

        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                taskItems.clear();
                getTaskList();
            }
        });
        getTaskList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetTaskList(GetTaskListEvent event){
        if(event.isOk()){
            refreshLayout.setRefreshing(false);
            page++;
            List<TaskItem> taskItems = GsonUtil.toTaskItemList(event.getData());
            this.taskItems.addAll(taskItems);
            adapter.loadMoreComplete();
            if(taskItems.size() == 0) adapter.loadMoreEnd();
            adapter.notifyDataSetChanged();
        }
        else{
            adapter.loadMoreFail();
            toastShort(event.getError());
        }
    }

    private void getTaskList(){
        refreshLayout.setRefreshing(true);
        String school = taskFilterView.getValue("学校");
        if("所有学校".equals(school)) school = "";
        String des = taskFilterView.getValue("分类");
        if("所有".equals(des)) des = "";
        String search = searchText.getText().toString();
        int costIndex = taskFilterView.getSelectedIndex("报酬");
        BigDecimal minCost;
        BigDecimal maxCost;
        switch (costIndex){
            case 0:
                minCost = new BigDecimal(0);
                maxCost = new BigDecimal(10000);
                break;
            case 1:
                minCost = new BigDecimal(1);
                maxCost = new BigDecimal(5);
                break;
            case 2:
                minCost = new BigDecimal(5);
                maxCost = new BigDecimal(10);
                break;
            case 3:
                minCost = new BigDecimal(10);
                maxCost = new BigDecimal(20);
                break;
            case 4:
                minCost = new BigDecimal(20);
                maxCost = new BigDecimal(100);
                break;
            case 5:
                minCost = new BigDecimal(100);
                maxCost = new BigDecimal(10000);
                break;
            default:
                minCost = new BigDecimal(0);
                maxCost = new BigDecimal(10000);
        }
        //赋值
        if(!(school.equals(this.school) && des.equals(this.des) && search.equals(this.search)
        && minCost.compareTo(this.minCost) == 0 && maxCost.compareTo(this.maxCost) == 0)) {
            taskItems.clear();
            page = 0;
            this.school = school;
            this.des = des;
            this.search = search;
            this.minCost = minCost;
            this.maxCost = maxCost;
        }
        SchoolTask.searchTask(school, des, search, minCost, maxCost, page);
    }

}
