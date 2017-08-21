package com.edu.schooltask.ui.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.edu.schooltask.R;
import com.edu.schooltask.adapter.DetailAdapter;
import com.edu.schooltask.ui.base.BaseActivity;
import com.edu.schooltask.beans.Detail;
import com.edu.schooltask.utils.GsonUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import server.api.SchoolTask;
import server.api.user.account.GetDetailEvent;

public class DetailActivity extends BaseActivity {
    @BindView(R.id.detail_srl) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.detail_rv) RecyclerView recyclerView;

    private DetailAdapter adapter;
    private List<Detail> details = new ArrayList<>();

    int page = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DetailAdapter(R.layout.item_detail, details);
        adapter.setEnableLoadMore(true);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                details.clear();
                page = 0;
                getDetail();
            }
        });
        getDetail();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetDetail(GetDetailEvent event){
        if(swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        if(event.isOk()){
            page ++;
            List<Detail> detailList = GsonUtil.toDetailList(event.getData());
            details.addAll(detailList);
            adapter.loadMoreComplete();
            if(detailList.size() == 0) adapter.loadMoreEnd();
            adapter.notifyDataSetChanged();
        }
        else{
            adapter.loadMoreFail();
            toastShort(event.getError());
        }
    }

    private void getDetail(){
        SchoolTask.getDetail(page);
    }
}
