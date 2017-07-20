package com.edu.schooltask.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.adapter.ViewPagerAdapter;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.beans.User;
import com.edu.schooltask.beans.UserBaseInfo;
import com.edu.schooltask.beans.UserHomePageInfo;
import com.edu.schooltask.utils.DialogUtil;
import com.edu.schooltask.utils.GlideUtil;
import com.edu.schooltask.view.ViewPagerTab;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import server.api.SchoolTask;
import server.api.user.relation.UpdateRelationEvent;
import server.api.user.GetUserHomePageInfoEvent;

public class UserActivity extends BaseActivity {
    private AppBarLayout topLayout;
    private Toolbar toolbar;
    private TextView titleText;
    private TextView followButton;
    private TextView followText;
    private ImageView messageBtn;
    private TextView fansText;
    private CircleImageView headImage;
    private ImageView bgImage;
    private TextView nameText;
    private TextView signText;
    private ViewPagerTab tab;
    private ViewPager viewPager;

    User me;
    boolean isMe;

    private ViewPagerAdapter adapter;
    private List<Fragment> fragmentList = new ArrayList<>();

    UserBaseInfo user;
    int relationType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        EventBus.getDefault().register(this);
        toolbar = getView(R.id.toolbar);
        topLayout = getView(R.id.user_abl);
        titleText = getView(R.id.toolbar_name);
        followButton = getView(R.id.user_follow_btn);
        followText = getView(R.id.user_follow);
        messageBtn = getView(R.id.user_message);
        fansText = getView(R.id.user_fans);
        headImage = getView(R.id.user_head);
        bgImage = getView(R.id.user_bg);
        nameText = getView(R.id.user_name);
        signText = getView(R.id.user_sign);
        tab = getView(R.id.user_tab);
        viewPager = getView(R.id.user_vp);

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
        user = (UserBaseInfo)intent.getSerializableExtra("user");
        titleText.setText(user.getName());

        me = mDataCache.getUser();
        if(me != null){
            if(me.getUserId().equals(user.getUserId())){    //本人
                isMe = true;
                followButton.setVisibility(View.GONE);
                messageBtn.setVisibility(View.GONE);
                SchoolTask.getUserHomePageInfo(user.getUserId(), user.getUserId());
            }
            else{
                isMe = false;
                messageBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        destroyPrivateMessageActivity();
                        Intent intent = new Intent(UserActivity.this, PrivateMessageActivity.class);
                        intent.putExtra("user", user);
                        startActivity(intent);
                    }
                });
                followButton.setVisibility(View.VISIBLE);
                followButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (relationType){
                            case 0:
                                SchoolTask.updateRelation(me.getUserId(), user.getUserId(), 1);
                                break;
                            case 1:
                                SchoolTask.updateRelation(me.getUserId(), user.getUserId(), 0);
                                break;
                        }

                    }
                });
                SchoolTask.getUserHomePageInfo(me.getUserId(), user.getUserId());
            }
        }
        else{
            SchoolTask.getUserHomePageInfo(user.getUserId(), user.getUserId());
        }

        headImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.createHeadImageDialog(UserActivity.this, user.getUserId()).show();
            }
        });
        setUserInfo();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void setUserInfo(){
        nameText.setText(user.getName());
        String sign = user.getSign();
        if(sign.length() == 0) sign = "无";
        signText.setText("简介：" + sign);
        followText.setText("关注 "+user.getFollow());
        fansText.setText("粉丝 "+user.getFans());
        GlideUtil.setHead(UserActivity.this, user.getUserId(), headImage, false);
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
            UserHomePageInfo userHomePageInfo = new Gson().fromJson(new Gson().toJson(event.getData()), new TypeToken<UserHomePageInfo>(){}.getType());
            user = userHomePageInfo.getUserBaseInfo();
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
            relationType = (Integer) event.getData();
            setRelation();
            switch (relationType){
                case 0:
                    toastShort("取消关注");
                    break;
                case 1:
                    toastShort("关注成功");
                    break;
            }
        }
        else{
            toastShort(event.getError());
        }
    }
}
