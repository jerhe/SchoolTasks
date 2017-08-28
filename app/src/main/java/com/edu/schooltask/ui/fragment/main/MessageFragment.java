package com.edu.schooltask.ui.fragment.main;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.adapter.ViewPagerAdapter;
import com.edu.schooltask.event.LoginSuccessEvent;
import com.edu.schooltask.event.LogoutEvent;
import com.edu.schooltask.event.MessageCountEvent;
import com.edu.schooltask.ui.activity.LoginActivity;
import com.edu.schooltask.ui.base.BaseFragment;
import com.edu.schooltask.ui.fragment.message.FriendFragment;
import com.edu.schooltask.ui.view.ViewPagerTab;
import com.edu.schooltask.utils.UserUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;

/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class MessageFragment extends BaseFragment {

    @BindView(R.id.mp_tab) ViewPagerTab viewPagerTab;
    @BindView(R.id.mp_vp) ViewPager viewPager;
    @BindView(R.id.mp_login_tip) TextView loginTip;

    @OnClick(R.id.mp_login_tip)
    public void loginTip(){
        openActivity(LoginActivity.class);
    }

    ViewPagerAdapter adapter;
    List<Fragment> fragmentList = new ArrayList<>();

    public MessageFragment() {
        super(R.layout.fragment_message_page);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void init() {
        ButterKnife.bind(this, view);
        viewPagerTab.addTab("消息列表");
        viewPagerTab.addTab("好友");
        viewPagerTab.select(0);
        if(UserUtil.hasLogin()) onLoginSuccess(null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginSuccess(LoginSuccessEvent event){
        fragmentList.clear();
        ConversationListFragment conversationListFragment = new ConversationListFragment();
        Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
                .appendPath("conversationList")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false")
                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")
                .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")
                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")
                .build();
        conversationListFragment.setUri(uri);
        FriendFragment friendFragment = new FriendFragment();
        fragmentList.add(conversationListFragment);
        fragmentList.add(friendFragment);
        adapter = new ViewPagerAdapter(getFragmentManager(), fragmentList);
        viewPager.setAdapter(adapter);
        viewPagerTab.setViewPager(viewPager);

        loginTip.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);
        viewPagerTab.setVisibility(View.VISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogout(LogoutEvent event){
        loginTip.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.GONE);
        viewPagerTab.setVisibility(View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageCount(MessageCountEvent event){
        viewPagerTab.setTip(0, event.getCount() != 0);
    }


}
