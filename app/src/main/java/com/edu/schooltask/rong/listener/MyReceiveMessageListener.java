package com.edu.schooltask.rong.listener;

import android.net.Uri;
import android.util.Log;

import com.edu.schooltask.event.GetFriendRequestEvent;
import com.edu.schooltask.event.MessageCountEvent;
import com.edu.schooltask.rong.message.FriendMessage;
import com.edu.schooltask.rong.message.OrderMessage;
import com.edu.schooltask.utils.UserUtil;

import org.greenrobot.eventbus.EventBus;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.InformationNotificationMessage;

/**
 * Created by 夜夜通宵 on 2017/8/25.
 */

public class MyReceiveMessageListener implements RongIMClient.OnReceiveMessageListener {
    @Override
    public boolean onReceived(Message message, int i) {
        MessageContent messageContent = message.getContent();
        if(messageContent instanceof FriendMessage){    //当有好友请求消息时，刷新验证消息
            EventBus.getDefault().post(new GetFriendRequestEvent());
        }
        return false;
    }
}
