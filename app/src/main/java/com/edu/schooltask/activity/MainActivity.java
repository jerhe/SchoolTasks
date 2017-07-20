package com.edu.schooltask.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.edu.schooltask.R;
import com.edu.schooltask.adapter.ViewPagerAdapter;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.beans.Poll;
import com.edu.schooltask.beans.UserConfig;
import com.edu.schooltask.event.LoginSuccessEvent;
import com.edu.schooltask.event.LogoutEvent;
import com.edu.schooltask.event.ShowMessageEvent;
import com.edu.schooltask.event.UnreadMessageNumEvent;
import com.edu.schooltask.fragment.main.HomeFragment;
import com.edu.schooltask.fragment.main.OrderFragment;
import com.edu.schooltask.fragment.main.MessageFragment;
import com.edu.schooltask.fragment.main.UserFragment;
import com.edu.schooltask.item.PrivateMessageItem;
import com.edu.schooltask.service.PollService;
import com.edu.schooltask.view.BottomMenu;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import server.api.SchoolTask;
import server.api.user.config.GetUserConfigEvent;
import server.api.login.UnloginEvent;
import server.api.poll.PollEvent;
import server.api.poll.SendPrivateMessageEvent;
import server.api.school.GetSchoolEvent;
import server.api.base.BaseTokenCallBack;
import server.api.token.RefreshTokenEvent;
import server.api.token.TokenErrorEvent;

public class MainActivity extends BaseActivity {
    ViewPager viewPager;
    List<Fragment> fragments = new ArrayList<>();
    Fragment homeFragment;
    Fragment talkFragment;
    Fragment orderFragment;
    Fragment userFragment;
    BottomMenu bottomMenu;

    NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bind();
        initViewPager();
        initBottomMenu();
        EventBus.getDefault().register(this);
        new SchoolTask(mDataCache);
        if(mDataCache.getSchool() == null) SchoolTask.getSchool();

        mDataCache.saveData("head", new Random().nextInt(999));
        mDataCache.saveData("bg", new Random().nextInt(999));

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent pollIntent = new Intent(MainActivity.this, PollService.class);
        startService(pollIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("main","destroy");
        EventBus.getDefault().unregister(this);
    }

    private void bind(){
        viewPager = getView(R.id.main_vp);
        bottomMenu = getView(R.id.main_bm);
    }

    private void initViewPager(){
        homeFragment = new HomeFragment();
        talkFragment = new MessageFragment();
        orderFragment = new OrderFragment();
        userFragment = new UserFragment();
        fragments.add(homeFragment);
        fragments.add(talkFragment);
        fragments.add(orderFragment);
        fragments.add(userFragment);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                bottomMenu.setPagePosition(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        viewPager.setOffscreenPageLimit(4); //设置预加载页面数
    }

    private void initBottomMenu(){
        bottomMenu.setOnMenuSelectedListener(new BottomMenu.OnMenuSelectedListener() {
            @Override
            public void onMenuSelected(int position) {
                viewPager.setCurrentItem(position, false);
            }
        });
        bottomMenu.setPagePosition(0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginSuccess(LoginSuccessEvent event){
        SchoolTask.getUserConfig();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetUserConfig(GetUserConfigEvent event){
        UserConfig userConfig;
        if(event.isOk()){
            userConfig = new Gson().fromJson(new Gson().toJson(event.getData()), new TypeToken<UserConfig>(){}.getType());
        }
        else{
            userConfig = new UserConfig(0,true,true,true);
        }
        mDataCache.saveData("userConfig", userConfig);
    }

    //未登录
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUnlogin(UnloginEvent event){
        toastShort("请先登录");
        openActivity(LoginActivity.class);
    }

    //Token错误
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTokenError(TokenErrorEvent event){
        mDataCache.removeUser();
        remainHome();   //关闭主页以外所有活动
        EventBus.getDefault().post(new LogoutEvent());
        toastShort("账号异常，请重新登录");
        openActivity(LoginActivity.class);
    }

    //刷新Token
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshToken(RefreshTokenEvent event){
        Log.e("token refresh", "@tag:" + BaseTokenCallBack.tokenTag + " @token:" + event.getToken());
        BaseTokenCallBack.tokenTag ++;
        mDataCache.refreshToken(event.getToken());
    }

    //获取学校
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetSchool(GetSchoolEvent event){
        if(event.isOk()){
            ArrayList<String> schools = new Gson().fromJson(new Gson().toJson(event.getData()), new TypeToken<ArrayList<String>>(){}.getType());
            mDataCache.saveSchool(schools);
        }
    }

    //获取消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPoll(PollEvent event){
        if(event.isOk()){
            UserConfig userConfig = mDataCache.getData("userConfig");
            List<Poll> polls = new Gson().fromJson(new Gson().toJson(event.getData()), new TypeToken<List<Poll>>(){}.getType());
            if(polls.size() != 0){  //有消息
                int privateMessageNum = 0;  //私信消息数
                for(int i=0; i<polls.size(); i++){
                    Poll poll = polls.get(i);
                    Intent intent;
                    if(poll.getType() == 0) {   //订单消息
                        intent = new Intent(this, TaskOrderActivity.class);
                        intent.putExtra("order_id", poll.getOrderId());
                        //通知
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
                        Notification notification = new NotificationCompat.Builder(this)
                                .setContentTitle("微任务")
                                .setContentText(polls.get(i).getMsg())
                                .setWhen(System.currentTimeMillis())
                                .setDefaults(NotificationCompat.DEFAULT_ALL)
                                .setSmallIcon(R.drawable.ic_icon_star)
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_icon_star))
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true)
                                .build();
                        notificationManager.notify(i+1, notification);
                    }
                    //存储消息
                    List<PrivateMessageItem> privateMessageItems =
                            mDataCache.getData("privateMessage"
                                    +poll.getToUser().getUserId()+poll.getFromUser().getUserId());
                    if(privateMessageItems == null){
                        privateMessageItems = new ArrayList<>();
                    }
                    privateMessageItems.add(new PrivateMessageItem(poll.getType() == 0 ?
                            PrivateMessageItem.SYSTEM : PrivateMessageItem.RECEIVE, poll));
                    //存储消息
                    mDataCache.saveListData("privateMessage" + poll.getToUser().getUserId()
                            + poll.getFromUser().getUserId(), (List)privateMessageItems);
                    //计算私信消息数
                    if(poll.getType() == 1){    //私信消息
                        privateMessageNum++;
                        if(privateMessageActivity != null){
                            if(poll.getFromUser().getUserId().equals(   //判断当前私信窗口是该消息窗口
                                    privateMessageActivity.user.getUserId())){
                                //发送事件至当前打开的私信窗口
                                EventBus.getDefault().post(new ShowMessageEvent(poll));
                                privateMessageNum--;
                            }
                        }
                    }
                }

                //私信消息处理
                if(userConfig.isPrivateMessage()){  //开启私信通知
                    if(privateMessageNum > 0){
                        Intent intent = new Intent(this, MainActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
                        Notification notification = new NotificationCompat.Builder(this)
                                .setContentTitle("微任务")
                                .setContentText("您有" + privateMessageNum + "条新私信")
                                .setWhen(System.currentTimeMillis())
                                .setDefaults(NotificationCompat.DEFAULT_ALL)
                                .setSmallIcon(R.drawable.ic_icon_star)
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_icon_star))
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true)
                                .build();
                        notificationManager.notify(0, notification);
                    }
                }
            }
            else{   //无消息

            }
        }
    }

    //获取消息数
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUnreadMessageNum(UnreadMessageNumEvent event){
        bottomMenu.setMessageNum(event.getNum());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSendPrivateMessage(SendPrivateMessageEvent event){
        if (event.isOk()){
            Poll poll = new Gson().fromJson(new Gson().toJson(event.getData()), new TypeToken<Poll>(){}.getType());
            List<PrivateMessageItem> messages = mDataCache.getData("privateMessage"
                    + poll.getFromUser().getUserId() + poll.getToUser().getUserId());
            if(messages == null){
                messages = new ArrayList<>();
            }
            messages.add(new PrivateMessageItem(PrivateMessageItem.SEND, poll));
            mDataCache.saveListData("privateMessage"
                    + poll.getFromUser().getUserId() + poll.getToUser().getUserId(), messages);
            EventBus.getDefault().post(new ShowMessageEvent(poll));
        }
        else{
            toastShort("发送失败");
        }
    }
}
