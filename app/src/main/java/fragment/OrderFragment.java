package fragment;

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
import view.SwitchTab;

/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class OrderFragment extends Fragment {
    private View view;
    private ViewPager viewPager;
    private SwitchTab switchTab;
    private List<Fragment> fragments = new ArrayList<>();
    private Fragment allOrderFragment;
    private Fragment waitAssessFragment;

    public OrderFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null != view) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (null != parent) {
                parent.removeView(view);
            }
        } else {
            view = inflater.inflate(R.layout.fragment_order_page,container,false);
            init();
        }
        return view;
    }

    private void init(){
        allOrderFragment = new AllOrderFragment();
        waitAssessFragment = new WaitAssessFragment();
        fragments.add(allOrderFragment);
        fragments.add(waitAssessFragment);
        viewPager = (ViewPager) view.findViewById(R.id.op_vp);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getFragmentManager(), fragments);
        viewPager.setAdapter(viewPagerAdapter);
        //ViewPager 切换页面事件的监听器
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switchTab.setPointerPosition(position, positionOffset);
            }
            @Override
            public void onPageSelected(int position) {
                switchTab.setPagePosition(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        switchTab = (SwitchTab) view.findViewById(R.id.op_st);
        //SwitchTab 的点击事件监听器
        switchTab.setOnMenuSelectedListener(new SwitchTab.OnMenuSelectedListener() {
            @Override
            public void onMenuSelected(int position) {
                viewPager.setCurrentItem(position,false);
            }
        });
        switchTab.setPagePosition(0);
    }
}
