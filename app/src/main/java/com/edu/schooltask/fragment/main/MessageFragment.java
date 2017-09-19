package com.edu.schooltask.fragment.main;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.edu.schooltask.R;
import com.edu.schooltask.adapter.ViewPagerAdapter;
import com.edu.schooltask.event.MessageCountEvent;
import com.edu.schooltask.activity.LoginActivity;
import com.edu.schooltask.base.BaseFragment;
import com.edu.schooltask.view.ImageTextView;
import com.edu.schooltask.view.ViewPagerTab;
import com.edu.schooltask.utils.UserUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;

/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class MessageFragment extends BaseFragment {

    @BindView(R.id.mp_login_tip) ImageTextView loginTip;
    @BindView(R.id.mp_vp) ViewPager viewPager;

    @OnClick(R.id.mp_login_tip)
    public void loginTip(){
        openActivity(LoginActivity.class);
    }

    private List<Fragment> fragmentList = new ArrayList<>();

    public MessageFragment() {
        super(R.layout.fragment_message_page);
    }

    @Override
    protected void init() {
        if(UserUtil.hasLogin()) onLogin();
    }

    @Override
    protected void onLogin() {
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
        fragmentList.add(conversationListFragment);
        viewPager.setAdapter(new ViewPagerAdapter(getFragmentManager(), fragmentList));
        viewPager.setVisibility(View.VISIBLE);
        loginTip.setVisibility(View.GONE);
    }

    @Override
    protected void onLogout() {
        loginTip.setVisibility(View.VISIBLE);
        viewPager.setVisibility(View.GONE);
    }

}
