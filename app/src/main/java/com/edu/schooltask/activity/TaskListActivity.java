package com.edu.schooltask.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.HomeAdapter;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.item.HomeItem;
import com.edu.schooltask.item.TaskItem;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.UserUtil;
import com.edu.schooltask.view.CustomLoadMoreView;
import com.edu.schooltask.view.TaskFilter;

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
import server.api.task.get.GetTaskListEvent;

public class TaskListActivity extends BaseActivity {
    @BindView(R.id.tl_srl) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.tl_rv) RecyclerView recyclerView;
    @BindView(R.id.tl_search_text) EditText searchText;
    @BindView(R.id.tl_tf) TaskFilter taskFilter;
    //@BindView(R.id.tl_tf_btn) TextView taskFilterBtn;
    @BindView(R.id.tl_shadow) View shadow;

    @OnClick(R.id.tl_tf_btn)
    public void textFilter(){
        if(taskFilter.isShown()){
            setTaskFilterVisible(false);
        }
        else{
            setTaskFilterVisible(true);
        }
    }
    @OnClick(R.id.tl_shadow)
    public void shadow(){
        if(shadow.isShown()){
            setTaskFilterVisible(false);
        }
    }
    @OnTextChanged(R.id.tl_search_text)
    public void searchTextChanged(){
        getTaskList();
    }


    private HomeAdapter adapter;
    private List<HomeItem> items = new ArrayList<>();

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
        if(taskFilter.isShown()){
            setTaskFilterVisible(false);
        }
        else{
            super.onBackPressed();
        }
    }

    private void init(){
        UserInfo user = UserUtil.getLoginUser();
        taskFilter.setSchool(new String[]{"所有学校", user == null ? null : user.getSchool()});
        taskFilter.setFilterChangeListener(new TaskFilter.FilterChangeListener() {
            @Override
            public void onFilterChange() {
                getTaskList();
            }
        });

        adapter = new HomeAdapter(items, mDataCache, this);
        adapter.setLoadMoreView(new CustomLoadMoreView());
        adapter.openLoadAnimation();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.setEnableLoadMore(true);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(TaskListActivity.this, WaitAcceptOrderActivity.class);
                intent.putExtra("task", items.get(position));
                startActivity(intent);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                items.clear();
                getTaskList();
            }
        });
        getTaskList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetTaskList(GetTaskListEvent event){
        if(event.isOk()){
            page++;
            if(swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
            List<TaskItem> taskItems = GsonUtil.toTaskItemList(event.getData());
            for(TaskItem taskItem : taskItems){
                HomeItem homeItem = new HomeItem(HomeItem.TASK_ITEM, taskItem);
                items.add(homeItem);
            }
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
        if(!swipeRefreshLayout.isRefreshing())swipeRefreshLayout.setRefreshing(true);
        String school = taskFilter.getSchool();
        if("所有学校".equals(school)){
            school = "%";
        }
        String des = taskFilter.getDes();
        if("所有".equals(des)){
            des = "%";
        }
        String searth = searchText.getText().toString();
        if(searth.length() == 0){
            searth = "%";
        }
        int costIndex = taskFilter.getCost();
        BigDecimal minCost;
        BigDecimal maxCost;
        switch (costIndex){
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
        if(!(school.equals(this.school) && des.equals(this.des) && searth.equals(this.search)
        && minCost.compareTo(this.minCost) == 0 && maxCost.compareTo(this.maxCost) == 0)) {
            items.clear();
            page = 0;
            this.school = school;
            this.des = des;
            this.search = searth;
            this.minCost = minCost;
            this.maxCost = maxCost;
        }
        SchoolTask.getTaskList(school, des, searth, minCost, maxCost, page);
    }

    private void setTaskFilterVisible(boolean show){
        if(show){
            taskFilter.setVisibility(View.VISIBLE);
            taskFilter.startAnimation(AnimationUtils.loadAnimation(
                    TaskListActivity.this, R.anim.translate_down));
            shadow.setVisibility(View.VISIBLE);
            shadow.startAnimation(AnimationUtils.loadAnimation(
                    TaskListActivity.this, R.anim.fade_in));
        }
        else{
            taskFilter.setVisibility(View.INVISIBLE);
            taskFilter.startAnimation(AnimationUtils.loadAnimation(
                    TaskListActivity.this,R.anim.translate_up));
            shadow.setVisibility(View.INVISIBLE);
            shadow.startAnimation(AnimationUtils.loadAnimation(
                    TaskListActivity.this, R.anim.fade_out));
        }
    }

}
