package com.edu.schooltask.activity;

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.adapter.ViewPagerAdapter;
import com.edu.schooltask.beans.UserHomePageInfo;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.beans.UserInfoWithToken;
import com.edu.schooltask.rong.message.FriendMessage;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.view.ViewPagerTab;
import com.edu.schooltask.utils.DialogUtil;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.UserUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import server.api.client.FriendClient;
import server.api.client.UserClient;
import server.api.result.Result;

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

    private Result friendRequestResult = new Result(true) {
        @Override
        public void onResponse(int id) {

        }

        @Override
        public void onSuccess(int id, Object data) {
            FriendMessage message = new FriendMessage("已发出好友请求");
            RongIM.getInstance().insertOutgoingMessage(Conversation.ConversationType.SYSTEM, "验证消息",
                    Message.SentStatus.SENT, message, null);
            toastShort("好友请求已发出");
        }

        @Override
        public void onFailed(int id, int code, String error) {
            toastShort(error);
        }
    };
    private Result getUserHomePageInfoResult = new Result() {
        @Override
        public void onResponse(int id) {

        }

        @Override
        public void onSuccess(int id, Object data) {
            layout.setVisibility(View.VISIBLE);
            layout.startAnimation(fadeInAnimation);
            UserHomePageInfo userHomePageInfo = GsonUtil.toUserHomePageInfo(data);
            user = userHomePageInfo.getUserInfo();
            setUserInfo();
            //TODO get other info
        }

        @Override
        public void onFailed(int id, int code, String error) {
                toastShort(error);
        }
    };

    @OnClick(R.id.user_friend_btn)
    public void addFriend(){
        if(!isMe){
            FriendClient.friendRequest(friendRequestResult, user.getUserId());
        }
    }

    Animation fadeInAnimation;

    UserInfoWithToken me;
    boolean isMe = true;

    private ViewPagerAdapter adapter;
    private List<Fragment> fragmentList = new ArrayList<>();

    String name;
    UserInfo user;

    @Override
    public int getLayout() {
        return R.layout.activity_user;
    }

    @Override
    public void init() {
        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
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
        UserClient.getUserHomePageInfo(getUserHomePageInfoResult, name);
    }

    private void setUserInfo(){
        titleText.setText(user.getName());
        nameText.setText(user.getName());
        String sign = user.getSign();
        if(sign.length() == 0) sign = "无";
        signText.setText("简介：" + sign);
        UserUtil.setHead(UserActivity.this, user.getHead(), headImage);
        UserUtil.setBackground(UserActivity.this, user.getBg(), bgImage);
    }
}
