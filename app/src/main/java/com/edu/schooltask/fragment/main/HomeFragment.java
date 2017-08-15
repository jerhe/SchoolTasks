package com.edu.schooltask.fragment.main;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.baoyz.widget.PullRefreshLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.activity.LoginActivity;
import com.edu.schooltask.activity.ReleaseTaskActivity;
import com.edu.schooltask.activity.TaskListActivity;
import com.edu.schooltask.activity.WaitAcceptOrderActivity;
import com.edu.schooltask.adapter.HomeAdapter;
import com.edu.schooltask.adapter.TaskItemAdapter;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.base.BaseFragment;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.event.LoginSuccessEvent;
import com.edu.schooltask.event.LogoutEvent;
import com.edu.schooltask.event.TabSelectedEvent;
import com.edu.schooltask.other.GlideImageLoader;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.NetUtil;
import com.edu.schooltask.item.HomeItem;
import com.edu.schooltask.item.TaskItem;
import com.edu.schooltask.utils.UserUtil;
import com.edu.schooltask.view.CustomLoadMoreView;
import com.edu.schooltask.view.ViewPagerTab;
import com.youth.banner.Banner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import server.api.SchoolTask;
import server.api.task.get.GetSchoolTaskEvent;

/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class HomeFragment extends BaseFragment {
    @BindView(R.id.home_banner) Banner banner;
    @BindView(R.id.home_task_release) ImageView taskReleaseBtn;
    @BindView(R.id.home_task_search) ImageView taskSearchBtn;
    @BindView(R.id.home_secondhand) ImageView secondhandBtn;
    @BindView(R.id.home_job) ImageView jobBtn;
    @BindView(R.id.home_tab) ViewPagerTab tab;
    @BindView(R.id.home_prl) PullRefreshLayout refreshLayout;
    @BindView(R.id.home_rv) RecyclerView recyclerView;

    private TaskItemAdapter adapter;
    private List<TaskItem> taskItems = new ArrayList<>();

    private int type = 0;   //0.附近任务 1.二手交易 2.最新兼职
    private int page = 0;

    List<String> images = new ArrayList<>();

    public HomeFragment() {
        super(R.layout.fragment_home_page);
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

    @Override
    protected void init(){
        ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TaskItemAdapter(taskItems);
        adapter.setLoadMoreView(new CustomLoadMoreView());
        adapter.bindToRecyclerView(recyclerView);
        adapter.setEnableLoadMore(true);
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                switch (type){
                    case 0:
                        getSchoolTask();
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                }
            }
        },recyclerView);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                TaskItem item = taskItems.get(position);
                Intent intent = new Intent(getActivity(), WaitAcceptOrderActivity.class);
                intent.putExtra("taskItem", item);
                startActivity(intent);
            }
        });

        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                clearList();
                getSchoolTask();
            }
        });

        initBanner();
        initButton();
        initTab();
        getSchoolTask();
    }

    private void initBanner(){
        banner.setImageLoader(new GlideImageLoader());
        images.add("http://oqqzw04zt.bkt.clouddn.com/banner1.jpg");
        images.add("http://oqqzw04zt.bkt.clouddn.com/banner1.jpg");
        images.add("http://oqqzw04zt.bkt.clouddn.com/banner1.jpg");
        banner.setImages(images);
        banner.start();
    }

    private void initButton(){
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetUtil.isNetworkConnected(getContext())){  //网络已连接
                    if(UserUtil.hasLogin()){
                        switch (v.getId()){
                            case R.id.home_task_release:
                                openActivity(ReleaseTaskActivity.class);
                                break;
                            case R.id.home_task_search:
                                openActivity(TaskListActivity.class);
                                break;
                        }
                    }
                    else{
                        toastShort("请先登录");
                        openActivity(LoginActivity.class);
                    }
                }
                else{
                    toastShort("请检查网络连接");
                }
            }
        };
        taskReleaseBtn.setOnClickListener(listener);
        taskSearchBtn.setOnClickListener(listener);
    }

    private void initTab(){
        tab.addTab("附近任务");
        tab.addTab("最新二手");
        tab.addTab("最新兼职");
        tab.select(0);
    }

    private void getSchoolTask(){
        UserInfo user = UserUtil.getLoginUser();
        if(user != null){
            SchoolTask.getSchoolTask(user.getSchool(), page);
        }
        else{   //用户未登录则获取最新任务
            SchoolTask.getSchoolTask("*", page);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginSuccess(LoginSuccessEvent event){
        clearList();
        getSchoolTask();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogout(LogoutEvent event){
        clearList();
        getSchoolTask();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetSchoolTask(GetSchoolTaskEvent event){
        refreshLayout.setRefreshing(false);
        if (event.isOk()){
            page ++;
            List<TaskItem> taskItems = GsonUtil.toTaskItemList(event.getData());
            this.taskItems.addAll(taskItems);
            adapter.loadMoreComplete();
            if(taskItems.size() == 0){
                adapter.loadMoreEnd();
            }
            adapter.notifyDataSetChanged();
        }
        else{
            adapter.loadMoreFail();
            toastShort(event.getError());
        }
    }

    private void clearList(){
        page = 0;
        taskItems.clear();
    }
}
