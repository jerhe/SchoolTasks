package com.edu.schooltask.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.adapter.ViewPagerAdapter;
import com.edu.schooltask.beans.UserHomePageInfo;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.beans.UserInfoWithToken;
import com.edu.schooltask.rong.message.FriendMessage;
import com.edu.schooltask.ui.base.BaseActivity;
import com.edu.schooltask.ui.view.ViewPagerTab;
import com.edu.schooltask.utils.DialogUtil;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.UserUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import server.api.SchoolTask;
import server.api.event.friend.FriendRequestEvent;
import server.api.event.user.GetUserHomePageInfoEvent;

public class UserActivity extends BaseActivity {
    @BindView(R.id.user_layout) CoordinatorLayout layout;
    @BindView(R.id.user_abl) AppBarLayout topLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.toolbar_name) TextView titleText;
    @BindView(R.id.user_friend_btn)  TextView friendButton;
    @BindView(R.id.user_head) CircleImageView headImage;
    @BindView(R.id.user_bg) ImageView bgImage;
    @BindView(R.id.user_name) TextView nameText;
    @BindView(R.id.user_sign) TextView signText;
    @BindView(R.id.user_tab) ViewPagerTab tab;
    @BindView(R.id.user_vp) ViewPager viewPager;

    @OnClick(R.id.user_head)
    public void showUserHead(){
        DialogUtil.createHeadImageDialog(UserActivity.this, user).show();
    }

    @OnClick(R.id.user_friend_btn)
    public void addFriend(){
        if(!isMe){
            SchoolTask.friendRequest(user.getUserId());
        }
    }

    UserInfoWithToken me;
    boolean isMe = true;

    private ViewPagerAdapter adapter;
    private List<Fragment> fragmentList = new ArrayList<>();

    String name;
    UserInfo user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        adapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(adapter);
        tab.addTab("动态");
        tab.addTab("资料");
        tab.setViewPager(viewPager);

        topLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float alpha = (float)-verticalOffset / (topLayout.getHeight() - titleText.getHeight());
                titleText.setAlpha(alpha > 0.5 ? alpha : 0);
                toolbar.setAlpha(alpha);

            }
        });

        Intent intent = getIntent();
        name = intent.getStringExtra("name");

        me = UserUtil.getLoginUser();
        isMe = name.equals(me.getName());
        SchoolTask.getUserHomePageInfo(name);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void setUserInfo(){
        titleText.setText(user.getName());
        nameText.setText(user.getName());
        String sign = user.getSign();
        if(sign.length() == 0) sign = "无";
        signText.setText("简介：" + sign);
        UserUtil.setHead(UserActivity.this, user, headImage);
        UserUtil.setBackground(UserActivity.this, user, bgImage);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetUserHomePageInfo(GetUserHomePageInfoEvent event){
        if(event.isOk()){
            layout.setVisibility(View.VISIBLE);
            layout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            UserHomePageInfo userHomePageInfo = GsonUtil.toUserHomePageInfo(event.getData());
            user = userHomePageInfo.getUserInfo();
            setUserInfo();
            //TODO get other info
        }
        else{
            toastShort(event.getError());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFriendRequest(FriendRequestEvent event){
        if(event.isOk()){
            FriendMessage message = new FriendMessage("已发出好友请求");
            RongIM.getInstance().insertOutgoingMessage(Conversation.ConversationType.SYSTEM, "验证消息",
                    Message.SentStatus.SENT, message, null);
            toastShort("好友请求已发出");
        }
        else{
            toastShort(event.getError());
        }

    }
}
