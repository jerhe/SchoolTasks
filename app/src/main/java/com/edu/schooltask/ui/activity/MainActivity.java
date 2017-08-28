package com.edu.schooltask.ui.activity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.edu.schooltask.R;
import com.edu.schooltask.adapter.ViewPagerAdapter;
import com.edu.schooltask.beans.UserConfig;
import com.edu.schooltask.beans.UserInfoWithToken;
import com.edu.schooltask.event.LoginSuccessEvent;
import com.edu.schooltask.event.LogoutEvent;
import com.edu.schooltask.event.MessageCountEvent;
import com.edu.schooltask.ui.base.BaseActivity;
import com.edu.schooltask.ui.fragment.main.HomeFragment;
import com.edu.schooltask.ui.fragment.main.MessageFragment;
import com.edu.schooltask.ui.fragment.main.OrderFragment;
import com.edu.schooltask.ui.fragment.main.UserFragment;
import com.edu.schooltask.ui.view.BottomTab;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.UserUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.RunnableScheduledFuture;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;
import server.api.SchoolTask;
import server.api.base.BaseTokenCallBack;
import server.api.user.login.UnloginEvent;
import server.api.rong.GetTokenEvent;
import server.api.school.GetSchoolEvent;
import server.api.user.token.RefreshTokenEvent;
import server.api.user.token.TokenErrorEvent;
import server.api.user.GetUserInfoEvent;
import server.api.user.GetUserConfigEvent;

public class MainActivity extends BaseActivity {
    List<Fragment> fragments = new ArrayList<>();
    Fragment homeFragment;  //首页
    Fragment messageFragment;  //消息
    Fragment orderFragment; //订单
    Fragment userFragment;  //我

    @BindView(R.id.main_vp) ViewPager viewPager;
    @BindView(R.id.main_bm) BottomTab bottomTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //注册事件总线
        EventBus.getDefault().register(this);
        //初始化服务接口
        SchoolTask.init();
        //初始化ViewPager
        initViewPager();
        //初始化底部菜单
        initBottomMenu();
        //读取学校列表，没有则从后台获取
        if(mDataCache.getSchool() == null) SchoolTask.getSchool();
        //初始化头像背景的sign，用于刷新图片
        mDataCache.saveData("head", new Random().nextInt(999));
        mDataCache.saveData("bg", new Random().nextInt(999));
        if(UserUtil.hasLogin()){
            EventBus.getDefault().post(new LoginSuccessEvent());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initViewPager(){
        //创建Fragment
        homeFragment = new HomeFragment();
        messageFragment = new MessageFragment();
        orderFragment = new OrderFragment();
        userFragment = new UserFragment();
        fragments.add(homeFragment);
        fragments.add(messageFragment);
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
        SchoolTask.rongGetToken();  //获取融云Token
        SchoolTask.getUserInfo(); //获取用户信息
        SchoolTask.getUserConfig(); //获取用户配置信息
    }

    //事件：获取用户配置信息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetUserConfig(GetUserConfigEvent event){
        UserConfig userConfig;
        if(event.isOk()){   //获取用户配置信息成功
            userConfig = GsonUtil.toUserConfig(event.getData());
            mDataCache.saveUserConfig(UserUtil.getLoginUser().getUserId(), userConfig);
        }
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

    //事件：获取用户信息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetUserInfo(GetUserInfoEvent event){
        if(event.isOk()){
            UserUtil.updateInfo(GsonUtil.toUserInfo(event.getData()));
        }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageCount(MessageCountEvent event){
        bottomTab.setMessageTip(event.getCount() != 0);
    }

    //事件：获取融云Token
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetToken(GetTokenEvent event){
        if (event.isOk()){
            UserInfoWithToken user = UserUtil.getLoginUser();
            rongConnect(user, (String)event.getData());
        }
        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    SchoolTask.rongGetToken();
                }
            }, 5000);
        }
    }

    private void rongConnect(final UserInfoWithToken user, String token){
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                Log.e("融云","连接失败");
            }

            @Override
            public void onSuccess(String s) {
                UserInfoWithToken user = UserUtil.getLoginUser();
                RongIM.getInstance().setCurrentUserInfo(new UserInfo(user.getUserId(), user.getName(),
                        Uri.parse(SchoolTask.HEAD + user.getUserId() + ".png")));
                RongIM.getInstance().setMessageAttachedUserInfo(true);
                RongIM.getInstance().enableNewComingMessageIcon(true);//显示新消息提醒
                RongIM.getInstance().enableUnreadMessageIcon(true);//显示未读消息数目
                Log.e("融云","连接成功," + s);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Log.e("融云","连接失败," + errorCode);
            }
        });
    }



}
