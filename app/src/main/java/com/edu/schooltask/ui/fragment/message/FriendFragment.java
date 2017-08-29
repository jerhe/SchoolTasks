package com.edu.schooltask.ui.fragment.message;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.baoyz.widget.PullRefreshLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.FriendList;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.ui.base.BaseFragment;
import com.edu.schooltask.ui.view.recyclerview.BaseRecyclerView;
import com.edu.schooltask.ui.view.recyclerview.FriendRecyclerView;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.UserUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import server.api.SchoolTask;
import server.api.event.friend.AgreeRequestEvent;
import server.api.event.friend.GetFriendListEvent;

/**
 * Created by 夜夜通宵 on 2017/8/25.
 */

public class FriendFragment extends BaseFragment {

    @BindView(R.id.friend_prl) PullRefreshLayout refreshLayout;
    @BindView(R.id.friend_frv) FriendRecyclerView recyclerView;

    public FriendFragment(){
        super(R.layout.fragment_friend);
    }

    @Override
    protected void init() {
        ButterKnife.bind(this, view);
        recyclerView.setOnGetDataListener(new BaseRecyclerView.OnGetDataListener() {
            @Override
            public void onGetData() {
                SchoolTask.getFriendList();
            }
        });
        recyclerView.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                UserInfo userInfo = recyclerView.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("school", userInfo.getSchool());
                RongIM.getInstance().startConversation(getContext(), Conversation.ConversationType.PRIVATE,
                        userInfo.getUserId(), userInfo.getName(), bundle);
            }
        });
        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.refresh();
            }
        });
        if(UserUtil.hasLogin()) recyclerView.refresh();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetFriendList(GetFriendListEvent event){
        refreshLayout.setRefreshing(false);
        if (event.isOk()){
            FriendList friendList = GsonUtil.toFriendList(event.getData());
            List<UserInfo> list = friendList.getList();
            for(UserInfo userInfo : list){  //当好友加载到好友列表时刷新融云用户信息
                RongIM.getInstance().refreshUserInfoCache(UserUtil.toRongUserInfo(userInfo));
            }
            recyclerView.add(list);
        }
        else{
            toastShort(event.getError());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAgreeRequest(AgreeRequestEvent event){    //同意好友时刷新好友列表
        if (event.isOk()){
            recyclerView.refresh();
        }
    }
}
