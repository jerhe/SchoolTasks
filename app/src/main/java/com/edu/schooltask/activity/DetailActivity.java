package com.edu.schooltask.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.edu.schooltask.R;
import com.edu.schooltask.adapter.DetailAdapter;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.beans.Detail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import server.api.SchoolTask;
import server.api.account.GetDetailEvent;

public class DetailActivity extends BaseActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private DetailAdapter adapter;
    private List<Detail> details = new ArrayList<>();

    int page = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        EventBus.getDefault().register(this);
        swipeRefreshLayout = getView(R.id.detail_srl);
        recyclerView = getView(R.id.detail_rv);
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
            List<Detail> detailList = new Gson().fromJson(new Gson().toJson(event.getData()), new TypeToken<List<Detail>>() {}.getType());
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
