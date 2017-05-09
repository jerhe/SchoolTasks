package fragment.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edu.schooltask.R;

import java.util.ArrayList;
import java.util.List;

import adapter.ViewPagerAdapter;
import base.BaseFragment;
import fragment.order.AllOrderFragment;
import fragment.order.WaitAssessFragment;
import view.SwitchTab;
import view.ViewPagerTab;

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
        viewPagerTab.setOnTabSelectedListener(new ViewPagerTab.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                viewPager.setCurrentItem(position);
            }
        });
        viewPagerTab.setDefaultTab(0);

        //SwitchTab 的点击事件监听器
        /*viewPagerTab.setOnMenuSelectedListener(new SwitchTab.OnMenuSelectedListener() {
            @Override
            public void onMenuSelected(int position) {
                viewPager.setCurrentItem(position,false);
            }
        });
        switchTab.setPagePosition(0);*/
    }
}
