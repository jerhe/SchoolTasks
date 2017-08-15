package com.edu.schooltask.fragment.message;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.baoyz.widget.PullRefreshLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.activity.LoginActivity;
import com.edu.schooltask.activity.PrivateMessageActivity;
import com.edu.schooltask.adapter.MessageAdapter;
import com.edu.schooltask.base.BaseFragment;
import com.edu.schooltask.beans.MessageItem;
import com.edu.schooltask.beans.PrivateMessage;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.event.AfterPollEvent;
import com.edu.schooltask.event.LoginSuccessEvent;
import com.edu.schooltask.event.LogoutEvent;
import com.edu.schooltask.event.UnreadMessageNumEvent;
import com.edu.schooltask.other.MessageComparator;
import com.edu.schooltask.utils.UserUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import server.api.SchoolTask;

/**
 * Created by 夜夜通宵 on 2017/5/28.
 */

public class MessageFragment extends BaseFragment {
    @BindView(R.id.message_prl) PullRefreshLayout refreshLayout;
    @BindView(R.id.message_rv) RecyclerView recyclerView;

    MessageAdapter adapter;
    List<MessageItem> messageItems = new ArrayList<>();

    public MessageFragment(){
        super(R.layout.fragment_message);
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
        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!UserUtil.hasLogin()){
                    refreshLayout.setRefreshing(false);
                    toastShort("请先登录");
                    openActivity(LoginActivity.class);
                    return;
                }
                SchoolTask.poll();
            }
        });

        UserInfo user = UserUtil.getLoginUser();
        if(user != null){
            messageItems.addAll(mDataCache.getMessageItem(user.getUserId()));
        }
        adapter = new MessageAdapter(R.layout.item_message, messageItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getActivity(), PrivateMessageActivity.class);
                intent.putExtra("user", messageItems.get(position).getUser());
                startActivity(intent);
            }
        });
        adapter.bindToRecyclerView(recyclerView);
        adapter.setEmptyView(R.layout.empt_message);
    }

    private void refreshMessageItemList(){
        UserInfo user = UserUtil.getLoginUser();
        if(user == null) return;
        //清除消息计数
        for (MessageItem item : messageItems){
            item.setCount(0);
        }
        //设置消息计数
        int notReadCount = 0;
        List<PrivateMessage> privateMessages = mDataCache.getPrivateMessage(user.getUserId());
        for (PrivateMessage message : privateMessages){
            boolean exist = false;
            for(MessageItem item : messageItems){
                if(message.getItemType() == PrivateMessage.SEND){
                    if(item.getUser().getUserId().equals(message.getPoll().getToId())){
                        exist = true;
                        break;
                    }
                }
                else{
                    if(item.getUser().getUserId().equals(message.getPoll().getFromId())){
                        if(!message.isHasRead()){
                            item.setCount(item.getCount() + 1);
                            notReadCount ++;
                        }
                        exist = true;
                        break;
                    }
                }
            }
            if(!exist){
                MessageItem item = new MessageItem(message.getPoll(), message.getItemType());
                messageItems.add(item);
            }
        }
        //设置最后消息和时间
        List<PrivateMessage> list = mDataCache.getPrivateMessage(user.getUserId());
        for (PrivateMessage privateMessage : list){
            for(MessageItem item : messageItems){
                if(item.getUser().getUserId().equals(privateMessage.getPoll().getFromId())
                        || item.getUser().getUserId().equals(privateMessage.getPoll().getToId())){
                    item.setLastMessage(privateMessage.getPoll().getContent());
                    item.setLastTime(privateMessage.getPoll().getCreateTime());
                }
            }
        }
        Collections.sort(messageItems, new MessageComparator());
        mDataCache.saveMessageItem(UserUtil.getLoginUser().getUserId(), messageItems);
        adapter.notifyDataSetChanged();
        EventBus.getDefault().post(new UnreadMessageNumEvent(notReadCount));
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshMessageItemList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPoll(AfterPollEvent event){
        refreshLayout.setRefreshing(false);
        refreshMessageItemList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogout(LogoutEvent event){
        messageItems.clear();
        adapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginSuccess(LoginSuccessEvent event){
        messageItems.addAll(mDataCache.getMessageItem(UserUtil.getLoginUser().getUserId()));
        adapter.notifyDataSetChanged();
    }
}
