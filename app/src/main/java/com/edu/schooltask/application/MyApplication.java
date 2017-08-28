package com.edu.schooltask.application;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.edu.schooltask.event.MessageCountEvent;
import com.edu.schooltask.rong.listener.MyConversationBehaviorListener;
import com.edu.schooltask.rong.listener.MyConversationListBehaviorListener;
import com.edu.schooltask.rong.listener.MyReceiveMessageListener;
import com.edu.schooltask.rong.message.FriendMessage;
import com.edu.schooltask.rong.message.OrderMessage;
import com.edu.schooltask.rong.provider.FriendMessageProvider;
import com.edu.schooltask.rong.provider.OrderMessageProvider;
import com.edu.schooltask.ui.activity.UserActivity;
import com.zhy.http.okhttp.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePalApplication;

import java.util.concurrent.TimeUnit;

import io.rong.imkit.RongIM;
import io.rong.imkit.manager.IUnReadMessageObserver;
import io.rong.imkit.model.UIConversation;
import io.rong.imlib.AnnotationNotFoundException;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import okhttp3.OkHttpClient;

/**
 * Created by 夜夜通宵 on 2017/5/17.
 */

public class MyApplication extends LitePalApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        RongIM.init(this, "0vnjpoad0gq9z");
        //设置会话监听
        RongIM.setConversationBehaviorListener(new MyConversationBehaviorListener());
        //设置会话列表监听
        RongIM.setConversationListBehaviorListener(new MyConversationListBehaviorListener());
        //设置自定义消息接收监听器
        RongIM.setOnReceiveMessageListener(new MyReceiveMessageListener());
        //未读消息数监听
        RongIM.getInstance().addUnReadMessageCountChangedObserver(new IUnReadMessageObserver() {
            @Override
            public void onCountChanged(int i) {
                EventBus.getDefault().post(new MessageCountEvent(i));
            }
        }, Conversation.ConversationType.PRIVATE, Conversation.ConversationType.SYSTEM);
        //注册自定义消息类型
        try {
            RongIMClient.registerMessageType(OrderMessage.class);
            RongIMClient.registerMessageType(FriendMessage.class);
        } catch (AnnotationNotFoundException e) {
            e.printStackTrace();
        }
        //注册自定义消息模板
        RongIM.registerMessageTemplate(new OrderMessageProvider());
        RongIM.registerMessageTemplate(new FriendMessageProvider());

        //BP.init("b8423903660c0d5aa0f0bcee7af3fb09");    //Bomb初始化
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)    //连接超时时间
                .readTimeout(10, TimeUnit.SECONDS)       //读取超时时间
                .retryOnConnectionFailure(true)         //是否重试
                .build();
        OkHttpUtils.initClient(client); //设置为默认client
    }
}
