package com.edu.schooltask.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.baoyz.widget.PullRefreshLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.UserInfoWithToken;
import com.edu.schooltask.beans.task.TaskItem;
import com.edu.schooltask.ui.base.BaseActivity;
import com.edu.schooltask.ui.view.TaskFilterView;
import com.edu.schooltask.ui.view.recyclerview.BasePageRecyclerView;
import com.edu.schooltask.ui.view.recyclerview.TaskItemRecyclerView;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.UserUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import server.api.SchoolTask;
import server.api.event.task.GetTaskListEvent;

public class TaskListActivity extends BaseActivity {
    @BindView(R.id.tl_prl) PullRefreshLayout refreshLayout;
    @BindView(R.id.tl_rv) TaskItemRecyclerView recyclerView;
    @BindView(R.id.tl_search_layout) RelativeLayout searchLayout;
    @BindView(R.id.tl_search_text) EditText searchText;
    @BindView(R.id.tl_tfv) TaskFilterView taskFilterView;

    @OnClick(R.id.tl_tf_btn)
    public void textFilter(){
        if(taskFilterView.isShown()) taskFilterView.hide();
        else taskFilterView.show();

    }

    @OnTextChanged(R.id.tl_search_text)
    public void searchTextChanged(){
        getTaskList(0);
    }

    Animation searchLayoutInAnimation;
    Animation searchLayoutOutAnimation;


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

    private void init(){
        searchLayoutInAnimation = AnimationUtils.loadAnimation(TaskListActivity.this ,
                R.anim.translate_top_in);
        searchLayoutOutAnimation = AnimationUtils.loadAnimation(TaskListActivity.this ,
                R.anim.translate_top_out);
        UserInfoWithToken user = UserUtil.getLoginUser();
        taskFilterView.addFilter(getString(R.string.school), "所有学校", user == null ? null : user.getSchool());
        taskFilterView.addFilter(getString(R.string.description), getResources().getStringArray(R.array.taskDescription));
        taskFilterView.addFilter(getString(R.string.reward), getResources().getStringArray(R.array.taskReward));
        taskFilterView.setFilterChangeListener(new TaskFilterView.FilterChangeListener() {
            @Override
            public void onFilterChange() {
                getTaskList(0);
            }
        });
        searchText.setHintTextColor(Color.GRAY);

        recyclerView.setEmptyView(R.layout.empty_task);
        recyclerView.setOnGetPageDataListener(new BasePageRecyclerView.OnGetPageDataListener() {
            @Override
            public void onGetPageData(int page) {
                getTaskList(page);
            }
        });

        recyclerView.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(TaskListActivity.this, WaitAcceptOrderActivity.class);
                intent.putExtra("taskItem", recyclerView.get(position));
                startActivity(intent);
            }
        });
        recyclerView.addHeader(LayoutInflater.from(TaskListActivity.this).inflate(R.layout.header_task_list, null));
        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.refresh();
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0) {
                    if(searchLayout.isShown()){
                        searchLayout.setVisibility(View.GONE);
                        searchLayout.startAnimation(searchLayoutOutAnimation);
                    }
                    return;
                }
                if(dy < 0){
                    if(!searchLayout.isShown()){
                        searchLayout.setVisibility(View.VISIBLE);
                        searchLayout.startAnimation(searchLayoutInAnimation);
                    }
                }
            }
        });
        getTaskList(0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetTaskList(GetTaskListEvent event){
        refreshLayout.setRefreshing(false);
        if(event.isOk()){
            List<TaskItem> taskItems = GsonUtil.toTaskItemList(event.getData());
            recyclerView.add(taskItems);
        }
        else{
            toastShort(event.getError());
        }
    }

    private void getTaskList(int page){
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
            recyclerView.clear(false);
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
