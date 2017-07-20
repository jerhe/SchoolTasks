package com.edu.schooltask.fragment.main;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.edu.schooltask.R;

import com.edu.schooltask.adapter.ViewPagerAdapter;
import com.edu.schooltask.base.BaseFragment;
import com.edu.schooltask.fragment.message.FansFragment;
import com.edu.schooltask.fragment.message.FollowFragment;
import com.edu.schooltask.view.ViewPagerTab;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class MessageFragment extends BaseFragment {

    ViewPagerTab viewPagerTab;
    ViewPager viewPager;
    ViewPagerAdapter adapter;

    List<Fragment> fragmentList = new ArrayList<>();

    public MessageFragment() {
        super(R.layout.fragment_message_page);
    }

    @Override
    protected void init() {
        viewPagerTab = getView(R.id.mp_tab);
        viewPager = getView(R.id.mp_vp);

        Fragment messageFragment = new com.edu.schooltask.fragment.message.MessageFragment();
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
        viewPagerTab.setSelect(0);
    }


}
