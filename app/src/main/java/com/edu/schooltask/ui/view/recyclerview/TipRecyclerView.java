package com.edu.schooltask.ui.view.recyclerview;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.ui.activity.LoginActivity;
import com.edu.schooltask.utils.UserUtil;
import com.edu.schooltask.ui.view.CustomLoadMoreView;

/**
 * Created by 夜夜通宵 on 2017/7/24.
 */

public class TipRecyclerView extends RelativeLayout {
    private TextView loginTip;
    private PullRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    private BaseQuickAdapter adapter;
    private int page = 0;

    private RefreshListener refreshListener;
    private boolean isRefreshing = false;

    public TipRecyclerView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_tip_recyclerview, this);
        loginTip = (TextView) findViewById(R.id.tr_login_tip);
        refreshLayout = (PullRefreshLayout) findViewById(R.id.tr_prl);
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
        //设置适配器
        adapter.openLoadAnimation();
        adapter.setLoadMoreView(new CustomLoadMoreView());
        //判断用户登录
        if(UserUtil.hasLogin()){
           showRecyclerView();
        }
        else{
            showLoginTip();
        }
        //刷新事件
        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefreshing = true;
                clear();
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

    public void clear(){
        adapter.getData().clear();
        page = 0;
    }

    public void showLoginTip(){
        loginTip.setVisibility(VISIBLE);
        refreshLayout.setVisibility(GONE);
    }

    public void showRecyclerView(){
        loginTip.setVisibility(GONE);
        refreshLayout.setVisibility(VISIBLE);
    }

    public void notifyDataChanged(){
        showRecyclerView();
        adapter.notifyDataSetChanged();
    }

    public int getPage(){
        return page;
    }

    public void setRefreshing(boolean refreshing){
        refreshLayout.setRefreshing(refreshing);
    }

    public void setRefreshListener(RefreshListener listener){
        this.refreshListener = listener;
        if(loginTip.getVisibility() != VISIBLE) listener.onRefresh(page);   //执行刷新
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

    public void logout(){
        clear();
        setRefreshing(false);
        notifyDataChanged();
        showLoginTip();
    }

    public void login(){
        refreshListener.onRefresh(page);
    }

    public interface RefreshListener{
        void onRefresh(int page);
    }
}
