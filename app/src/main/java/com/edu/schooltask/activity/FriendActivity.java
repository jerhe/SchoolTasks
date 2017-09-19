package com.edu.schooltask.activity;

import android.os.Bundle;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.UserAdapter;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.beans.FriendList;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.utils.DialogUtil;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.UserUtil;
import com.edu.schooltask.view.recyclerview.RefreshRecyclerView;
import com.orhanobut.dialogplus.DialogPlus;

import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import server.api.client.FriendClient;
import server.api.result.Result;

public class FriendActivity extends BaseActivity {

    private RefreshRecyclerView<UserInfo> recyclerView;

    private Result deleteFriendResult = new Result(true) {
        @Override
        public void onResponse(int id) {

        }

        @Override
        public void onSuccess(int id, Object data) {
            recyclerView.refresh();
        }

        @Override
        public void onFailed(int id, int code, String error) {
            toastShort(error);
        }
    };

    @Override
    public int getLayout() {
        return R.layout.activity_friend;
    }

    @Override
    public void init() {
        recyclerView = new RefreshRecyclerView<UserInfo>(this, true) {
            @Override
            protected BaseQuickAdapter initAdapter(List<UserInfo> list) {
                return new UserAdapter(list);
            }

            @Override
            protected void requestData(Result result) {
                FriendClient.getFriendList(result);
            }

            @Override
            protected void onSuccess(int id, Object data) {
                FriendList friendList = GsonUtil.toFriendList(data);
                List<UserInfo> list = friendList.getList();
                for(UserInfo userInfo : list){  //当好友加载到好友列表时刷新融云用户信息
                    RongIM.getInstance().refreshUserInfoCache(UserUtil.toRongUserInfo(userInfo));
                }
                recyclerView.add(list);
            }

            @Override
            protected void onFailed(int id, int code, String error) {
                toastShort(error);
            }

            @Override
            protected void onItemClick(int position, UserInfo userInfo) {
                startConversation(userInfo);
            }

            @Override
            protected void onItemLongClick(int position, final UserInfo userInfo) {
                DialogUtil.createListDialog(getContext(), userInfo.getName(), new DialogUtil.ListItemClickListener() {
                    @Override
                    public void onItemClick(int position, String item) {
                        switch (position){
                            case 0:
                                openActivity(UserActivity.class, "name", userInfo.getName());
                                break;
                            case 1:
                                startConversation(userInfo);
                                break;
                            case 2:
                                deleteFriend(userInfo);
                                break;
                        }
                    }
                }, "访问Ta的主页", "聊天", "删除好友").show();
            }
        };
        recyclerView.setEmptyView(R.layout.empty_friend);
        addView(recyclerView);
        recyclerView.refresh();
    }

    private void startConversation(UserInfo userInfo){
        Bundle bundle = new Bundle();
        bundle.putString("school", userInfo.getSchool());
        RongIM.getInstance().startConversation(FriendActivity.this,
                Conversation.ConversationType.PRIVATE,
                userInfo.getUserId(), userInfo.getName(), bundle);
    }

    private void deleteFriend(final UserInfo userInfo){
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                DialogUtil.createTextDialog(FriendActivity.this, "提示",
                        "确定要删除好友\t" + userInfo.getName() + "\t吗？", "", new DialogUtil.OnClickListener() {
                            @Override
                            public void onClick(DialogPlus dialogPlus) {
                                FriendClient.delete(deleteFriendResult, userInfo.getUserId());
                            }
                        }).show();
            }
        }, 300);
    }
}
