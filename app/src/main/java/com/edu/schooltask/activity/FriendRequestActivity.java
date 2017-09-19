package com.edu.schooltask.activity;

import android.content.Intent;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.FriendRequestAdapter;
import com.edu.schooltask.beans.FriendRequest;
import com.edu.schooltask.event.GetFriendRequestEvent;
import com.edu.schooltask.rong.message.FriendMessage;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.view.recyclerview.RefreshRecyclerView;
import com.edu.schooltask.utils.GsonUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import server.api.client.FriendClient;
import server.api.result.Result;

public class FriendRequestActivity extends BaseActivity {

    private RefreshRecyclerView<FriendRequest> recyclerView;

    private Result agreeRequestResult = new Result(true) {
        @Override
        public void onResponse(int id) {

        }

        @Override
        public void onSuccess(int id, Object data) {
            RongIM.getInstance().insertOutgoingMessage(Conversation.ConversationType.SYSTEM,
                    getString(R.string.friend_request_id), Message.SentStatus.SENT,
                    new FriendMessage(getString(R.string.friend_agree_message, (String)data)), null);
            recyclerView.refresh();
        }

        @Override
        public void onFailed(int id, int code, String error) {
            if(code == 2) recyclerView.refresh();    //消息过期
            toastShort(error);
        }
    };
    private Result rejectRequestResult = new Result(true) {
        @Override
        public void onResponse(int id) {

        }

        @Override
        public void onSuccess(int id, Object data) {
            RongIM.getInstance().insertOutgoingMessage(Conversation.ConversationType.SYSTEM,
                    getString(R.string.friend_request_id), Message.SentStatus.SENT,
                    new FriendMessage(getString(R.string.friend_object_message, (String)data)), null);
            recyclerView.refresh();
        }

        @Override
        public void onFailed(int id, int code, String error) {
            if(code == 2) recyclerView.refresh();    //消息过期
            toastShort(error);
        }
    };

    @Override
    public int getLayout() {
        return R.layout.activity_friend_request;
    }

    @Override
    public void init() {
        EventBus.getDefault().register(this);
        recyclerView = new RefreshRecyclerView<FriendRequest>(this, true) {
            @Override
            protected BaseQuickAdapter initAdapter(List list) {
                return new FriendRequestAdapter(list);
            }

            @Override
            protected void requestData(Result result) {
                FriendClient.getRequestList(result);
            }

            @Override
            protected void onSuccess(int id, Object data) {
                List<FriendRequest> list = GsonUtil.toFriendRequestList(data);
                recyclerView.add(list);
            }

            @Override
            protected void onFailed(int id, int code, String error) {
                toastShort(error);
            }

            @Override
            protected void onChildClick(View view, int position, FriendRequest friendRequest) {
                switch (view.getId()){
                    case R.id.fr_user:
                        Intent intent = new Intent(FriendRequestActivity.this, UserActivity.class);
                        intent.putExtra("name", friendRequest.getUserInfo().getName());
                        startActivity(intent);
                        break;
                    case R.id.fr_agree:
                        FriendClient.agreeRequest(agreeRequestResult, friendRequest.getUserInfo().getUserId());
                        break;
                    case R.id.fr_reject:
                        FriendClient.rejectRequest(rejectRequestResult, friendRequest.getUserInfo().getUserId());
                        break;
                }
            }
        };
        addView(recyclerView);
        recyclerView.refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetFriendRequestList(GetFriendRequestEvent event){
        recyclerView.refresh();
    }

}
