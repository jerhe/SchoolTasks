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
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.beans.UserInfoBase;
import com.edu.schooltask.event.LoginSuccessEvent;
import com.edu.schooltask.event.LogoutEvent;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.UserUtil;
import com.edu.schooltask.view.CustomLoadMoreView;
import com.edu.schooltask.view.TipRecyclerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import server.api.SchoolTask;
import server.api.user.relation.GetFansUserEvent;

/**
 * Created by 夜夜通宵 on 2017/5/28.
 */

public class FansFragment extends BaseFragment {
    @BindView(R.id.fans_tr) TipRecyclerView tipRecyclerView;

    List<UserInfoBase> fansUsers = new ArrayList<>();
    UserAdapter adapter;

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
        ButterKnife.bind(this, view);
        adapter = new UserAdapter(R.layout.item_user, fansUsers);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getActivity(), PrivateMessageActivity.class);
                intent.putExtra("user", fansUsers.get(position));
                startActivity(intent);
            }
        });
        tipRecyclerView.init(adapter);
        tipRecyclerView.setRefreshListener(new TipRecyclerView.RefreshListener() {
            @Override
            public void onRefresh(int page) {
                SchoolTask.getFans(page);
            }
        });
        //缓存中读取粉丝
        UserInfo user = UserUtil.getLoginUser();
        if(user != null){
            List<UserInfoBase> fans = mDataCache.getFans(user.getUserId());
            fansUsers.addAll(fans);
            tipRecyclerView.notifyDataChanged();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetFansUser(GetFansUserEvent event){
        tipRecyclerView.setRefreshing(false);
        if(event.isOk()){
            tipRecyclerView.loadSuccess();
            List<UserInfoBase> users = GsonUtil.toUserInfoBaseList(event.getData());
            for(UserInfoBase userInfoBase : users){
                if(!fansUsers.contains(userInfoBase)) fansUsers.add(userInfoBase);
            }
            if(users.size() == 0) adapter.loadMoreEnd();
            mDataCache.saveFans(UserUtil.getLoginUser().getUserId(), fansUsers);
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
