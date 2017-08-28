package com.edu.schooltask.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.baoyz.widget.PullRefreshLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.FriendRequest;
import com.edu.schooltask.event.GetFriendRequestEvent;
import com.edu.schooltask.rong.message.FriendMessage;
import com.edu.schooltask.ui.base.BaseActivity;
import com.edu.schooltask.ui.view.recyclerview.BaseRecyclerView;
import com.edu.schooltask.ui.view.recyclerview.FriendRequestRecyclerView;
import com.edu.schooltask.utils.GsonUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import server.api.SchoolTask;
import server.api.friend.AgreeRequestEvent;
import server.api.friend.GetRequestListEvent;
import server.api.friend.RejectRequestEvent;

public class FriendRequestActivity extends BaseActivity {

    @BindView(R.id.fr_prl) PullRefreshLayout refreshLayout;
    @BindView(R.id.fr_frrv) FriendRequestRecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        recyclerView.setOnGetDataListener(new BaseRecyclerView.OnGetDataListener() {
            @Override
            public void onGetData() {
                SchoolTask.getRequestList();
            }
        });
        recyclerView.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()){
                    case R.id.fr_user:
                        Intent intent = new Intent(FriendRequestActivity.this, UserActivity.class);
                        intent.putExtra("name", recyclerView.get(position).getUserInfo().getName());
                        startActivity(intent);
                        break;
                    case R.id.fr_agree:
                        SchoolTask.agreeRequest(recyclerView.get(position).getUserInfo().getUserId());
                        break;
                    case R.id.fr_reject:
                        SchoolTask.rejectRequest(recyclerView.get(position).getUserInfo().getUserId());
                        break;
                }

            }
        });
        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerView.refresh();
            }
        });
        recyclerView.refresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetFriendRequestList(GetFriendRequestEvent event){
        recyclerView.refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetRequestList(GetRequestListEvent event){
        refreshLayout.setRefreshing(false);
        if (event.isOk()){
            List<FriendRequest> list = GsonUtil.toFriendRequestList(event.getData());
            recyclerView.add(list);
        }
        else{
            toastShort(event.getError());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAgreeRequest(AgreeRequestEvent event){
        if (event.isOk()){
            String name = (String)event.getData();
            RongIM.getInstance().insertOutgoingMessage(Conversation.ConversationType.SYSTEM, "验证消息",
                    Message.SentStatus.SENT, new FriendMessage("已同意 " + name + " 的好友请求"), null);
            recyclerView.refresh();
        }
        else{
            if(event.getCode() == 2) recyclerView.refresh();    //消息过期
            toastShort(event.getError());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRejectRequest(RejectRequestEvent event){
        if (event.isOk()){
            String name = (String)event.getData();
            RongIM.getInstance().insertOutgoingMessage(Conversation.ConversationType.SYSTEM, "验证消息",
                    Message.SentStatus.SENT, new FriendMessage("已拒绝 " + name + " 的好友请求"), null);
            recyclerView.refresh();
        }
        else{
            if(event.getCode() == 2) recyclerView.refresh();    //消息过期
            toastShort(event.getError());
        }
    }
}
