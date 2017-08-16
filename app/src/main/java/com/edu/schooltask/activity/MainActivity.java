package com.edu.schooltask.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.edu.schooltask.R;
import com.edu.schooltask.adapter.ViewPagerAdapter;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.beans.Poll;
import com.edu.schooltask.beans.UserConfig;
import com.edu.schooltask.event.AfterPollEvent;
import com.edu.schooltask.event.LoginSuccessEvent;
import com.edu.schooltask.event.LogoutEvent;
import com.edu.schooltask.event.ShowMessageEvent;
import com.edu.schooltask.event.UnreadMessageNumEvent;
import com.edu.schooltask.fragment.main.HomeFragment;
import com.edu.schooltask.fragment.main.OrderFragment;
import com.edu.schooltask.fragment.main.MessageFragment;
import com.edu.schooltask.fragment.main.UserFragment;
import com.edu.schooltask.beans.PrivateMessage;
import com.edu.schooltask.service.PollService;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.MessageUtil;
import com.edu.schooltask.utils.UserUtil;
import com.edu.schooltask.view.BottomTab;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    List<Fragment> fragments = new ArrayList<>();
    Fragment homeFragment;  //首页
    Fragment talkFragment;  //消息
    Fragment orderFragment; //订单
    Fragment userFragment;  //我

    @BindView(R.id.main_vp) ViewPager viewPager;
    @BindView(R.id.main_bm)
    BottomTab bottomTab;

    NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //初始化ViewPager
        initViewPager();
        //初始化底部菜单
        initBottomMenu();
        //注册事件总线
        EventBus.getDefault().register(this);
        //初始化服务接口
        SchoolTask.init();
        //读取学校列表，没有则从后台获取
        if(mDataCache.getSchool() == null) SchoolTask.getSchool();
        //初始化头像背景的sign，用于刷新图片
        mDataCache.saveData("head", new Random().nextInt(999));
        mDataCache.saveData("bg", new Random().nextInt(999));

        //启动消息推送（轮询）
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent pollIntent = new Intent(MainActivity.this, PollService.class);
        startService(pollIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //撤销事件总线
        EventBus.getDefault().unregister(this);
    }

    private void initViewPager(){
        //创建Fragment
        homeFragment = new HomeFragment();
        talkFragment = new MessageFragment();
        orderFragment = new OrderFragment();
        userFragment = new UserFragment();
        fragments.add(homeFragment);
        fragments.add(talkFragment);
        fragments.add(orderFragment);
        fragments.add(userFragment);
        //ViewPager适配器
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter<>(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(4); //设置预加载页面数
    }

    /**
     * 初始化底部菜单
     */
    private void initBottomMenu(){
        bottomTab.setOnMenuSelectedListener(new BottomTab.OnMenuSelectedListener() {
            @Override
            public void onMenuSelected(int position) {
                viewPager.setCurrentItem(position, false);
            }
        });
        bottomTab.setPagePosition(0);
        bottomTab.setViewPager(viewPager);
    }

    //事件：登录成功
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginSuccess(LoginSuccessEvent event){
        SchoolTask.getUserConfig(); //获取用户配置信息
    }

    //事件：获取用户配置信息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetUserConfig(GetUserConfigEvent event){
        UserConfig userConfig;
        if(event.isOk()){   //获取用户配置信息成功
            userConfig = GsonUtil.toUserConfig(event.getData());
        }
        else{   //获取用户配置信息失败
            userConfig = new UserConfig(0,true,true,true);  //新建默认用户配置
        }
        mDataCache.saveData("userConfig", userConfig);  //保存用户配置信息
    }

    //事件：用户未登录
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUnlogin(UnloginEvent event){
        toastShort("请先登录");
        openActivity(LoginActivity.class);  //进入登录页
    }

    //事件：Token错误
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTokenError(TokenErrorEvent event){
        UserUtil.deleteLoginUser();    //删除已存用户
        remainHome();   //关闭主页以外所有活动
        EventBus.getDefault().post(new LogoutEvent());  //post登出事件
        toastShort("账号异常，请重新登录");
        openActivity(LoginActivity.class);  //进入登录页
    }

    //事件：Token刷新
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshToken(RefreshTokenEvent event){
        BaseTokenCallBack.tokenTag ++;
        UserUtil.updateToken(event.getToken());  //刷新Token
    }

    //事件：获取学校
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetSchool(GetSchoolEvent event){
        if(event.isOk()){
            List<String> schools = GsonUtil.toStringList(event.getData());
            mDataCache.saveSchool(schools); //存入学校
        }
    }

    //事件：获取消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPoll(PollEvent event){
        if(event.isOk()){
            UserConfig userConfig = mDataCache.getData("userConfig");
            List<Poll> polls = GsonUtil.toPollList(event.getData());
            if(polls.size() == 0) {
                EventBus.getDefault().post(new AfterPollEvent());
                return;
            }
            int privateMessageNum = 0;  //私信消息数
            for(int i=0; i<polls.size(); i++){
                Poll poll = polls.get(i);
                //存储消息
                mDataCache.savePrivateMessage(UserUtil.getLoginUser().getUserId(), new PrivateMessage(poll));
                //订单消息
                if(poll.getType() == 0) {
                    createOrderNotifycation(poll, i);
                }

                //计算私信消息数
                if(poll.getType() == 1){    //私信消息
                    privateMessageNum++;
                    if(MessageUtil.isPrivateMessage  //判断当前私信窗口是该消息窗口
                            && poll.getFromUser().getUserId().equals(MessageUtil.userId)){
                        //发送事件至当前打开的私信窗口
                        EventBus.getDefault().post(new ShowMessageEvent());
                        privateMessageNum--;
                    }
                }
            }

            //私信消息通知
            if(!MessageUtil.isPrivateMessage)
                if(userConfig.isPrivateMessage()){  //开启私信通知
                    if(privateMessageNum > 0){
                        Intent intent = new Intent(this, MainActivity.class);
                        notify(intent, "您有" + privateMessageNum + "条新私信", 0);
                    }
                }
        }
        EventBus.getDefault().post(new AfterPollEvent());
    }

    private void notify(Intent intent, String content, int index){
        if (BaseActivity.isForeground()){   //应用在前台
            tip();
            return;
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("微任务")
                .setContentText(content)
                .setWhen(System.currentTimeMillis())
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_icon_star)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_icon_star))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(index, notification);
    }

    //铃声震动提示
    private void tip(){
        //震动
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long [] pattern = {100,400};   // 停止 开启 停止 开启
        vibrator.vibrate(pattern,1);
        //提示音
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone rt = RingtoneManager.getRingtone(getApplicationContext(), uri);
        rt.play();
    }

    //创建订单通知
    private void createOrderNotifycation(Poll poll, int index){
        Intent intent = new Intent(this, TaskOrderActivity.class);
        intent.putExtra("order_id", poll.getOrderId());
        notify(intent, poll.getContent(), index+1);
    }

    //事件：获取未读消息数
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUnreadMessageNum(UnreadMessageNumEvent event){
        bottomTab.setMessageNum(event.getNum());   //设置底部菜单的小圆点
    }


    //事件：发送私信
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSendPrivateMessage(SendPrivateMessageEvent event){
        if (event.isOk()){
            Poll poll = GsonUtil.toPoll(event.getData());
            String userId = UserUtil.getLoginUser().getUserId();
            mDataCache.savePrivateMessage(userId, new PrivateMessage(PrivateMessage.SEND, poll));
            EventBus.getDefault().post(new ShowMessageEvent());
        }
        else{
            toastShort(event.getError());
        }
    }
}
