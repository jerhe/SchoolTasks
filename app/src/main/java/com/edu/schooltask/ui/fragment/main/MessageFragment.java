package com.edu.schooltask.ui.fragment.main;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.edu.schooltask.R;

import com.edu.schooltask.adapter.ViewPagerAdapter;
import com.edu.schooltask.ui.base.BaseFragment;
import com.edu.schooltask.ui.fragment.message.FansFragment;
import com.edu.schooltask.ui.fragment.message.FollowFragment;
import com.edu.schooltask.ui.view.ViewPagerTab;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class MessageFragment extends BaseFragment {

    @BindView(R.id.mp_tab) ViewPagerTab viewPagerTab;
    @BindView(R.id.mp_vp) ViewPager viewPager;
    ViewPagerAdapter adapter;

    List<Fragment> fragmentList = new ArrayList<>();

    public MessageFragment() {
        super(R.layout.fragment_message_page);
    }

    @Override
    protected void init() {
        ButterKnife.bind(this, view);
        Fragment messageFragment = new com.edu.schooltask.ui.fragment.message.MessageFragment();
        Fragment followFragment = new FollowFragment();
        Fragment fansFragment = new FansFragment();
        fragmentList.add(messageFragment);
        fragmentList.add(followFragment);
        fragmentList.add(fansFragment);
        adapter = new ViewPagerAdapter(getFragmentManager(), fragmentList);
        viewPager.setAdapter(adapter);

        viewPagerTab.setViewPager(viewPager);
        viewPagerTab.addTab("消息列表");
        viewPagerTab.addTab("关注");
        viewPagerTab.addTab("粉丝");
        viewPagerTab.select(0);
    }


}
