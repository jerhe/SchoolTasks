package com.edu.schooltask.rong.listener;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.edu.schooltask.activity.FriendRequestActivity;

import io.rong.imkit.RongIM;
import io.rong.imkit.model.UIConversation;
import io.rong.imlib.model.Conversation;

/**
 * Created by 夜夜通宵 on 2017/8/25.
 */

public class MyConversationListBehaviorListener implements RongIM.ConversationListBehaviorListener {

    @Override
    public boolean onConversationPortraitClick(Context context, Conversation.ConversationType conversationType, String userId) {
        return false;
    }

    @Override
    public boolean onConversationPortraitLongClick(Context context, Conversation.ConversationType conversationType, String s) {
        return false;
    }

    @Override
    public boolean onConversationLongClick(Context context, View view, UIConversation uiConversation) {
        return false;
    }

    @Override
    public boolean onConversationClick(Context context, View view, UIConversation uiConversation) {
        String targetId = uiConversation.getConversationTargetId();
        String title = uiConversation.getUIConversationTitle();
        if(targetId.equals("验证消息")){
            Intent intent = new Intent(context, FriendRequestActivity.class);
            context.startActivity(intent);
            RongIM.getInstance().clearMessagesUnreadStatus(Conversation.ConversationType.SYSTEM, "验证消息", null);
            return true;
        }
        return false;

    }
}
