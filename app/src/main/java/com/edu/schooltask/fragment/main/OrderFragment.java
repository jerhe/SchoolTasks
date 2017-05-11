package com.edu.schooltask.fragment.main;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.edu.schooltask.R;

import java.util.ArrayList;
import java.util.List;

import com.edu.schooltask.adapter.ViewPagerAdapter;
import com.edu.schooltask.base.BaseFragment;
import com.edu.schooltask.fragment.order.AllOrderFragment;
import com.edu.schooltask.fragment.order.WaitAssessFragment;

import com.edu.schooltask.view.ViewPagerTab;

/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class OrderFragment extends BaseFragment {
    private ViewPager viewPager;
    private ViewPagerTab viewPagerTab;
    private List<Fragment> fragments = new ArrayList<>();
    private Fragment allOrderFragment;
    private Fragment waitAssessFragment;

    public OrderFragment() {
        super(R.layout.fragment_order_page);
    }

    @Override
    protected void init(){
        allOrderFragment = new AllOrderFragment();
        waitAssessFragment = new WaitAssessFragment();
        fragments.add(allOrderFragment);
        fragments.add(waitAssessFragment);
        viewPager = (ViewPager) view.findViewById(R.id.op_vp);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getFragmentManager(), fragments);
        viewPager.setAdapter(viewPagerAdapter);

        viewPagerTab = (ViewPagerTab) view.findViewById(R.id.op_vpt);
        viewPagerTab.setViewPager(viewPager);
        viewPagerTab.addTab("所有订单");
        viewPagerTab.addTab("待评价");
        viewPagerTab.setSelect(0);
    }
}
