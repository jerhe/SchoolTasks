package com.edu.schooltask.fragment.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.activity.LoginActivity;
import com.edu.schooltask.activity.PrivateMessageActivity;
import com.edu.schooltask.adapter.UserAdapter;
import com.edu.schooltask.base.BaseFragment;
import com.edu.schooltask.beans.UserBaseInfo;
import com.edu.schooltask.event.LoginSuccessEvent;
import com.edu.schooltask.event.LogoutEvent;
import com.edu.schooltask.view.CustomLoadMoreView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import server.api.SchoolTask;
import server.api.user.relation.GetFansUserEvent;

/**
 * Created by 夜夜通宵 on 2017/5/28.
 */

public class FansFragment extends BaseFragment {
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    List<UserBaseInfo> fansUsers = new ArrayList<>();
    UserAdapter adapter;

    int page = 0;

    public FansFragment(){
        super(R.layout.fragment_fans);
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
    protected void init() {
        swipeRefreshLayout = getView(R.id.fans_srl);
        recyclerView = getView(R.id.fans_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UserAdapter(R.layout.item_user, fansUsers);
        recyclerView.setAdapter(adapter);
        adapter.openLoadAnimation();
        adapter.setLoadMoreView(new CustomLoadMoreView());
        adapter.setEnableLoadMore(true);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getActivity(), PrivateMessageActivity.class);
                intent.putExtra("user", fansUsers.get(position));
                startActivity(intent);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(mDataCache.getUser() == null){
                    toastShort("请先登录");
                    swipeRefreshLayout.setRefreshing(false);
                    openActivity(LoginActivity.class);
                    return;
                }
                refresh();
            }
        });

        SchoolTask.getFans(page);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetFansUser(GetFansUserEvent event){
        if(swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        if(event.isOk()){
            page++;
            List<UserBaseInfo> users = new Gson().fromJson(new Gson().toJson(event.getData()), new TypeToken<List<UserBaseInfo>>(){}.getType());
            for(UserBaseInfo userBaseInfo : users){
                if(!fansUsers.contains(userBaseInfo)) fansUsers.add(userBaseInfo);
            }
            adapter.loadMoreComplete();
            if(users.size() == 0) adapter.loadMoreEnd();
        }
        else{
            adapter.loadMoreFail();
            toastShort(event.getError());
        }
        adapter.notifyDataSetChanged();
    }

    private void refresh(){
        fansUsers.clear();
        page = 0;
        SchoolTask.getFans(page);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogout(LogoutEvent event){
        fansUsers.clear();
        page = 0;
        adapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogin(LoginSuccessEvent event){
        refresh();
    }
}
