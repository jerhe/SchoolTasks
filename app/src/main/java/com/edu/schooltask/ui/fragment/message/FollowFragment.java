package com.edu.schooltask.ui.fragment.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.ui.activity.PrivateMessageActivity;
import com.edu.schooltask.adapter.UserAdapter;
import com.edu.schooltask.ui.base.BaseFragment;
import com.edu.schooltask.beans.UserInfoWithToken;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.event.LoginSuccessEvent;
import com.edu.schooltask.event.LogoutEvent;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.UserUtil;
import com.edu.schooltask.ui.view.recyclerview.TipRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import server.api.SchoolTask;
import server.api.user.relation.GetFollowUserEvent;

/**
 * Created by 夜夜通宵 on 2017/5/28.
 */

public class FollowFragment extends BaseFragment {
    @BindView(R.id.follow_tr) TipRecyclerView tipRecyclerView;

    List<UserInfo> followerUsers = new ArrayList<>();
    UserAdapter adapter;

    public FollowFragment(){
        super(R.layout.fragment_follow);
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
        ButterKnife.bind(this, view);
        adapter = new UserAdapter(followerUsers);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getActivity(), PrivateMessageActivity.class);
                intent.putExtra("user", followerUsers.get(position));
                startActivity(intent);
            }
        });
        tipRecyclerView.init(adapter);
        tipRecyclerView.setRefreshListener(new TipRecyclerView.RefreshListener() {
            @Override
            public void onRefresh(int page) {
                SchoolTask.getFollowers(page);
            }
        });
        //缓存中读取关注
        UserInfoWithToken user = UserUtil.getLoginUser();
        if(user != null){
            List<UserInfo> followers = mDataCache.getFollowers(user.getUserId());
            followerUsers.addAll(followers);
            tipRecyclerView.notifyDataChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetFollowUser(GetFollowUserEvent event){
        tipRecyclerView.setRefreshing(false);
        if(event.isOk()){
            tipRecyclerView.loadSuccess();
            List<UserInfo> users = GsonUtil.toUserInfoBaseList(event.getData());
            for(UserInfo userInfo : users){
                if(!followerUsers.contains(userInfo)) followerUsers.add(userInfo);
            }
            if(users.size() == 0) adapter.loadMoreEnd();
            mDataCache.saveFollowers(UserUtil.getLoginUser().getUserId(), followerUsers);
        }
        else{
            tipRecyclerView.loadFail();
            toastShort(event.getError());
        }
        tipRecyclerView.notifyDataChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogout(LogoutEvent event){
        tipRecyclerView.logout();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogin(LoginSuccessEvent event){
        tipRecyclerView.login();
    }


}
