package com.edu.schooltask.rong.listener;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.edu.schooltask.activity.UserActivity;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;

/**
 * Created by 夜夜通宵 on 2017/8/25.
 */

public class MyConversationBehaviorListener implements RongIM.ConversationBehaviorListener {
    @Override
    public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
        Intent intent = new Intent(context, UserActivity.class);
        intent.putExtra("name", userInfo.getName());
        context.startActivity(intent);
        return true;
    }

    @Override
    public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
        return false;
    }

    @Override
    public boolean onMessageClick(Context context, View view, Message message) {
        return false;
    }

    @Override
    public boolean onMessageLinkClick(Context context, String s) {
        return false;
    }

    @Override
    public boolean onMessageLongClick(Context context, View view, Message message) {
        return false;
    }
}
