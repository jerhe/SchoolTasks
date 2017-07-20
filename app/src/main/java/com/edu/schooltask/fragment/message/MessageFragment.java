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
import com.edu.schooltask.adapter.MessageAdapter;
import com.edu.schooltask.base.BaseFragment;
import com.edu.schooltask.beans.Poll;
import com.edu.schooltask.beans.User;
import com.edu.schooltask.event.LoginSuccessEvent;
import com.edu.schooltask.event.LogoutEvent;
import com.edu.schooltask.event.ReadMessageEvent;
import com.edu.schooltask.event.UnreadMessageNumEvent;
import com.edu.schooltask.item.MessageItem;
import com.edu.schooltask.other.MessageComparator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import server.api.SchoolTask;
import server.api.poll.PollEvent;

/**
 * Created by 夜夜通宵 on 2017/5/28.
 */

public class MessageFragment extends BaseFragment {
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
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
        swipeRefreshLayout = getView(R.id.message_srl);
        recyclerView = getView(R.id.message_rv);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(mDataCache.getUser() == null){
                    swipeRefreshLayout.setRefreshing(false);
                    toastShort("请先登录");
                    openActivity(LoginActivity.class);
                    return;
                }
                SchoolTask.poll();
            }
        });

        adapter = new MessageAdapter(R.layout.item_message, messageItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getActivity(), PrivateMessageActivity.class);
                intent.putExtra("user", messageItems.get(position).getUser());
                startActivity(intent);
                for(Poll poll : messageItems.get(position).getPolls()){
                    poll.setHasRead(true);
                }
                refreshList();
            }
        });
        getMessageItemFromCache();
    }

    private void getMessageItemFromCache(){
        User user = mDataCache.getUser();
        if(user != null){
            List<MessageItem> messageItemList = mDataCache.getData("messageList" + user.getUserId());
            if(messageItemList != null) {
                messageItems.addAll(messageItemList);
                refreshList();
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPoll(PollEvent event){
        if(swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
        if (event.isOk()){
            List<Poll> polls = new Gson().fromJson(new Gson().toJson(event.getData()), new TypeToken<List<Poll>>(){}.getType());
            int pollsSize = polls.size();
            if (pollsSize != 0){
                for(Poll poll : polls){
                    MessageItem messageItem = findMessageItemByUserId(poll.getFromUser().getUserId());
                    if(messageItem == null){
                        messageItem = new MessageItem();
                        messageItem.setUser(poll.getFromUser());
                        messageItem.setLastPoll(poll);
                        messageItem.setLastTime(poll.getTime());
                        List<Poll> pollList = new ArrayList<>();
                        pollList.add(poll);
                        messageItem.setPolls(pollList);
                        messageItems.add(messageItem);
                    }
                    else{
                        messageItem.getPolls().add(poll);
                        messageItem.setLastTime(poll.getTime());
                        messageItem.setLastPoll(poll);
                    }
                }
                refreshList();
            }
        }
    }

    private void refreshList(){
        Collections.sort(messageItems, new MessageComparator());
        User user = mDataCache.getUser();
        mDataCache.saveListData("messageList" + user.getUserId(),(List)messageItems);
        adapter.notifyDataSetChanged();
        int unReadMessageNum = 0;
        for(MessageItem messageItem : messageItems){
            for(Poll poll : messageItem.getPolls()){
                if(!poll.isHasRead()) unReadMessageNum++;
            }
        }
        EventBus.getDefault().post(new UnreadMessageNumEvent(unReadMessageNum));
    }

    private MessageItem findMessageItemByUserId(String userId){
        for(MessageItem messageItem : messageItems){
            if(messageItem.getUser().getUserId().equals(userId)) return messageItem;
        }
        return null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReadMessage(ReadMessageEvent event){
        Poll newPoll = event.getPoll();
        for(MessageItem messageItem : messageItems){
            for(Poll poll : messageItem.getPolls()){
                if(newPoll.getPollId() == poll.getPollId()){
                    poll.setHasRead(true);
                    break;
                }
            }
        }
        refreshList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogout(LogoutEvent event){
        messageItems.clear();
        adapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginSuccess(LoginSuccessEvent event){
        getMessageItemFromCache();
    }
}
