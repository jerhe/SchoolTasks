package com.edu.schooltask.activity;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.edu.schooltask.R;
import com.edu.schooltask.adapter.ViewPagerAdapter;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.beans.UserInfoWithToken;
import com.edu.schooltask.event.LoginEvent;
import com.edu.schooltask.event.LogoutEvent;
import com.edu.schooltask.event.MessageCountEvent;
import com.edu.schooltask.event.UnloginEvent;
import com.edu.schooltask.fragment.main.HomeFragment;
import com.edu.schooltask.fragment.main.MessageFragment;
import com.edu.schooltask.fragment.main.OrderFragment;
import com.edu.schooltask.fragment.main.UserFragment;
import com.edu.schooltask.item.IconMenuItem;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.UserUtil;
import com.edu.schooltask.view.BottomTab;
import com.edu.schooltask.view.ReleaseMenu;
import com.edu.schooltask.view.listener.ItemClickListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import server.api.client.RongClient;
import server.api.client.SchoolClient;
import server.api.client.UserClient;
import server.api.event.TokenErrorEvent;
import server.api.result.Result;

public class MainActivity extends BaseActivity {

    @BindView(R.id.main_vp) ViewPager viewPager;
    @BindView(R.id.main_bm) BottomTab bottomTab;
    @BindView(R.id.main_rm) ReleaseMenu releaseMenu;

    List<Fragment> fragments = new ArrayList<>();
    Fragment homeFragment;  //首页
    Fragment messageFragment;  //消息
    Fragment orderFragment; //订单
    Fragment userFragment;  //我

    private Result getSchoolResult = new Result() {
        @Override
        public void onResponse(int id) {

        }

        @Override
        public void onSuccess(int id, Object data) {
            List<String> schools = GsonUtil.toStringList(data);
            mDataCache.saveSchool(schools);
        }

        @Override
        public void onFailed(int id, int code, String error) {

        }
    };
    private Result getSelfUserInfoResult = new Result(true) {
        @Override
        public void onResponse(int id) {

        }

        @Override
        public void onSuccess(int id, Object data) {
            UserUtil.updateInfo(GsonUtil.toUserInfo(data));
        }

        @Override
        public void onFailed(int id, int code, String error) {

        }
    };
    private Result rongGetTokenResult = new Result(true) {
        @Override
        public void onResponse(int id) {

        }

        @Override
        public void onSuccess(int id, Object data) {
            rongConnect((String)data);
        }

        @Override
        public void onFailed(int id, int code, String error) {
            if(UserUtil.hasLogin())
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(UserUtil.hasLogin()) //防止离线时用户退出继续请求
                            RongClient.rongGetToken(rongGetTokenResult);
                    }
                }, 10000);
        }
    };

    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void init() {
        EventBus.getDefault().register(this);
        initViewPager();
        initBottomMenu();
        if(mDataCache.getSchool() == null) SchoolClient.getSchool(getSchoolResult);
        if(UserUtil.hasLogin()){
            EventBus.getDefault().post(new LoginEvent());
        }
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
        bottomTab.setPagePosition(0);
        bottomTab.setViewPager(viewPager);

        bottomTab.setReleaseListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (releaseMenu.isShown()) {
                    releaseMenu.hide();
                }
                else {
                    releaseMenu.show();
                }
            }
        });
        bottomTab.setReleaseLongListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                openActivity(ReleaseTaskActivity.class);
                releaseMenu.hide();
                return true;
            }
        });

        releaseMenu.setItemClickListener(new ItemClickListener<IconMenuItem>() {
            @Override
            public void onItemClick(int position, IconMenuItem o) {
                switch (position){
                    case 0:
                        openActivity(ReleaseTaskActivity.class);
                        break;
                    case 1:
                        openActivity(ReleaseDynamicActivity.class);
                        break;
                    default:
                        break;
                }
                releaseMenu.hide();
            }
        });
        releaseMenu.setStartView(bottomTab.getReleaseTab());
    }

    //事件：登录成功
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginSuccess(LoginEvent event){
        RongClient.rongGetToken(rongGetTokenResult);  //获取融云Token
        UserClient.getSelfUserInfo(getSelfUserInfoResult); //获取用户信息
    }


    //事件：用户未登录
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUnlogin(UnloginEvent event){
        toastShort(getString(R.string.unlogin_tip));
        openActivity(LoginActivity.class);  //进入登录页
    }

    //事件：Token错误
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTokenError(TokenErrorEvent event){
        UserUtil.deleteLoginUser();    //删除已存用户
        remainHome();   //关闭主页以外所有活动
        EventBus.getDefault().post(new LogoutEvent());  //post登出事件
        toastShort(getString(R.string.user_error));
        openActivity(LoginActivity.class);  //进入登录页
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageCount(MessageCountEvent event){
        bottomTab.setMessageTip(event.getCount() != 0);
    }

    private void rongConnect(String token){
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                Log.e("融云","连接失败");
            }

            @Override
            public void onSuccess(String s) {
                UserInfoWithToken user = UserUtil.getLoginUser();
                if(user == null) return;
                RongIM.getInstance().setCurrentUserInfo(UserUtil.toRongUserInfo(user));
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
