package com.edu.schooltask.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.activity.LoginActivity;
import com.edu.schooltask.data.DataCache;
import com.edu.schooltask.utils.UserUtil;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/7/24.
 */

public class TipRecyclerView extends RelativeLayout {
    private TextView loginTip;
    private TextView emptyTip;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private BaseQuickAdapter adapter;
    private int page = 0;

    private RefreshListener refreshListener;
    private boolean isRefreshing = false;

    public TipRecyclerView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_tip_recyclerview, this);
        loginTip = (TextView) findViewById(R.id.tr_login_tip);
        emptyTip = (TextView) findViewById(R.id.tr_empty_tip);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.tr_srl);
        recyclerView = (RecyclerView) findViewById(R.id.tr_rv);

        loginTip.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                context.startActivity(intent);
            }
        });
    }

    public void init(BaseQuickAdapter adapter){
        //初始化RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        this.adapter = adapter;
        recyclerView.setAdapter(adapter);
        //判断用户登录
        if(UserUtil.hasLogin()){
            showEmptyTip();
        }
        else{
            showLoginTip();
        }
        //刷新事件
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshing = true;
                page = 0;
                if(refreshListener != null) refreshListener.onRefresh(page);
            }
        });
    }

    //设置加载更多
    public void setLoadMore(boolean loadMore){
        if(loadMore){
            adapter.setEnableLoadMore(true);
            adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
                @Override
                public void onLoadMoreRequested() {
                    if(refreshListener != null) {
                        if(loginTip.getVisibility() != VISIBLE)
                            refreshListener.onRefresh(page);
                    }
                }
            }, recyclerView);
        }
        else{
            adapter.setEnableLoadMore(false);
        }
    }

    private void clear(){
        adapter.getData().clear();
        page = 0;
    }

    public void showLoginTip(){
        loginTip.setVisibility(VISIBLE);
        emptyTip.setVisibility(GONE);
        swipeRefreshLayout.setVisibility(GONE);
    }

    public void showEmptyTip(){
        loginTip.setVisibility(GONE);
        emptyTip.setVisibility(VISIBLE);
        swipeRefreshLayout.setVisibility(VISIBLE);
    }

    public void showRecyclerView(){
        loginTip.setVisibility(GONE);
        emptyTip.setVisibility(GONE);
        swipeRefreshLayout.setVisibility(VISIBLE);
    }

    public void notifyDataChanged(){
        if(adapter.getData().size() == 0){
            showEmptyTip();
        }
        else{
            showRecyclerView();
        }
        adapter.notifyDataSetChanged();
    }

    public void setRefreshing(boolean refreshing){
        swipeRefreshLayout.setRefreshing(refreshing);
    }

    public void setRefreshListener(RefreshListener listener){
        this.refreshListener = listener;
        if(loginTip.getVisibility() != VISIBLE) listener.onRefresh(page);   //执行刷新
    }

    public void setEmptyTip(String tip){
        emptyTip.setText(tip);
    }

    //加载成功
    public void loadSuccess(){
        adapter.loadMoreComplete();
        if(isRefreshing) {
            clear();
            isRefreshing = false;
        }
        page++;
    }

    public void loadFail(){
        adapter.loadMoreFail();
    }

    //改变刷新圆圈高度
    public void setRefreshOffset(int offset){
        swipeRefreshLayout.setProgressViewOffset(true, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, offset,
                getResources().getDisplayMetrics()));
    }

    public void logout(){
        clear();
        setRefreshing(false);
        notifyDataChanged();
        showLoginTip();
    }

    public void login(){
        showEmptyTip();
        refreshListener.onRefresh(page);
    }

    public interface RefreshListener{
        void onRefresh(int page);
    }
}
