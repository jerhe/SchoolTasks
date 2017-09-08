package com.edu.schooltask.application;

import android.util.Log;

import com.edu.schooltask.event.MessageCountEvent;
import com.edu.schooltask.rong.listener.MyConversationBehaviorListener;
import com.edu.schooltask.rong.listener.MyConversationListBehaviorListener;
import com.edu.schooltask.rong.listener.MyReceiveMessageListener;
import com.edu.schooltask.rong.message.FriendMessage;
import com.edu.schooltask.rong.message.OrderMessage;
import com.edu.schooltask.rong.provider.FriendMessageProvider;
import com.edu.schooltask.rong.provider.OrderMessageProvider;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.https.HttpsUtils;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePalApplication;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import io.rong.imkit.RongIM;
import io.rong.imkit.manager.IUnReadMessageObserver;
import io.rong.imlib.AnnotationNotFoundException;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
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

        //okhttp
        try {
            HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(
                    new InputStream[]{getAssets().open("school_task.cer")}, null, null);
            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                    .connectTimeout(20, TimeUnit.SECONDS)    //连接超时时间
                    .readTimeout(20, TimeUnit.SECONDS)       //读取超时时间
                    .retryOnConnectionFailure(true)         //是否重试
                    .build();
            OkHttpUtils.initClient(client); //设置为默认client
        } catch (IOException e) {
            Log.e("okhttpInitError", e.toString());
            e.printStackTrace();
        }

    }
}
