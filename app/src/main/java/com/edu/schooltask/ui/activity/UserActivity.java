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
import com.edu.schooltask.ui.base.BaseActivity;
import com.edu.schooltask.beans.UserInfoWithToken;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.beans.UserHomePageInfo;
import com.edu.schooltask.utils.DialogUtil;
import com.edu.schooltask.utils.GlideUtil;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.UserUtil;
import com.edu.schooltask.ui.view.ViewPagerTab;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import server.api.SchoolTask;
import server.api.user.relation.UpdateRelationEvent;
import server.api.user.GetUserHomePageInfoEvent;

public class UserActivity extends BaseActivity {
    @BindView(R.id.user_layout) CoordinatorLayout layout;
    @BindView(R.id.user_abl) AppBarLayout topLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.toolbar_name) TextView titleText;
    @BindView(R.id.user_follow)  TextView followText;
    @BindView(R.id.user_fans) TextView fansText;
    @BindView(R.id.user_follow_btn)  TextView followButton;
    @BindView(R.id.user_message) ImageView messageBtn;
    @BindView(R.id.user_head) CircleImageView headImage;
    @BindView(R.id.user_bg) ImageView bgImage;
    @BindView(R.id.user_name) TextView nameText;
    @BindView(R.id.user_sign) TextView signText;
    @BindView(R.id.user_tab) ViewPagerTab tab;
    @BindView(R.id.user_vp) ViewPager viewPager;

    @OnClick(R.id.user_head)
    public void showUserHead(){
        DialogUtil.createHeadImageDialog(UserActivity.this, user.getUserId()).show();
    }
    @OnClick(R.id.user_message)
    public void talk(){
        if(!isMe){
            destroyPrivateMessageActivity();
            Intent intent = new Intent(UserActivity.this, PrivateMessageActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        }
    }
    @OnClick(R.id.user_follow_btn)
    public void follow(){
        if(!isMe){
            switch (relationType){
                case 0:
                    SchoolTask.updateRelation(me.getUserId(), user.getUserId(), 1);
                    break;
                case 1:
                    SchoolTask.updateRelation(me.getUserId(), user.getUserId(), 0);
                    break;
            }
        }
    }

    UserInfoWithToken me;
    boolean isMe = true;

    private ViewPagerAdapter adapter;
    private List<Fragment> fragmentList = new ArrayList<>();

    String userId;
    UserInfo user;
    int relationType;

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
        userId = intent.getStringExtra("userId");

        me = UserUtil.getLoginUser();
        if(me != null){
            isMe = me.getUserId().equals(userId);
            messageBtn.setVisibility(isMe ? View.GONE : View.VISIBLE);
            followButton.setVisibility(isMe ? View.GONE : View.VISIBLE);
            SchoolTask.getUserHomePageInfo(isMe ? me.getUserId() : userId, userId);
        }
        else{
            SchoolTask.getUserHomePageInfo(userId, userId);
        }
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
        followText.setText("关注 "+user.getFollowerCount());
        fansText.setText("粉丝 "+user.getFansCount());
        GlideUtil.setHead(UserActivity.this, user.getUserId(), headImage);
        GlideUtil.setBackground(UserActivity.this, user.getUserId(), bgImage);
    }

    private void setRelation(){
        switch (relationType){
            case 0:
                followButton.setText("关注");
                followButton.setVisibility(View.VISIBLE);
                followButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
                break;
            case 1:
                followButton.setText("已关注");
                followButton.setVisibility(View.VISIBLE);
                followButton.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
                break;
            case 2:
                followButton.setVisibility(View.GONE);  //拉黑不可关注
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetUserHomePageInfo(GetUserHomePageInfoEvent event){
        if(event.isOk()){
            layout.setVisibility(View.VISIBLE);
            layout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            UserHomePageInfo userHomePageInfo = GsonUtil.toUserHomePageInfo(event.getData());
            user = userHomePageInfo.getUserInfo();
            relationType = userHomePageInfo.getRelationType();
            setUserInfo();
            if(!isMe)setRelation();
            //TODO get other info
        }
        else{
            toastShort(event.getError());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateRelation(UpdateRelationEvent event){
        if(event.isOk()){
            relationType = GsonUtil.toInteger(event.getData());
            setRelation();
            switch (relationType){
                case 0:
                    toastShort("取消关注");
                    break;
                case 1:
                    toastShort("关注成功");
                    break;
                case 2:
                    toastShort("拉黑成功");
                    break;
                default:
                    toastShort("错误");
            }
        }
        else{
            toastShort(event.getError());
        }
    }
}
