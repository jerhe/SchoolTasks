package com.edu.schooltask.fragment.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.activity.LoginActivity;
import com.edu.schooltask.activity.ReleaseActivity;
import com.edu.schooltask.adapter.BannerViewPagerAdapter;
import com.edu.schooltask.adapter.HomeAdapter;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.base.BaseFragment;
import com.edu.schooltask.beans.User;
import com.edu.schooltask.event.TabSelectedEvent;
import com.edu.schooltask.http.HttpUtil;
import com.edu.schooltask.item.HomeItem;
import com.edu.schooltask.item.OrderItem;
import com.edu.schooltask.other.BannerViewPagerPointer;
import com.edu.schooltask.view.ViewPagerTab;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class HomeFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private HomeAdapter adapter;
    private List<HomeItem> items = new ArrayList<>();

    View headView;
    private ViewPagerTab homeTab;   //this
    private ViewPagerTab homeViewPagerTab;  //recyclerview

    public HomeFragment() {
        super(R.layout.fragment_home_page);
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
    protected void init(){
        recyclerView = (RecyclerView)view.findViewById(R.id.home_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HomeAdapter(items, mDataCache);
        adapter.bindToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);
        initBanner();
        initButton();
        initPointer();

        User user1 = new User("1111","用户1","学校1",0);
        OrderItem orderItem1 = new OrderItem(0, "111", "内容 ", 1399, user1);
        User user2 = new User("1111","用户2","学校1",1);
        OrderItem orderItem2 = new OrderItem(0, "111", "内容 ", 1399, user2);
        User user3 = new User("1111","用户3","学校1",0);
        OrderItem orderItem3 = new OrderItem(0, "111", "内容 ", 1399, user3);
        User user4 = new User("1111","用户4","学校1",1);
        OrderItem orderItem4 = new OrderItem(0, "111", "内容 ", 1399, user4);
        HomeItem item1 = new HomeItem(HomeItem.NEAR_TASK_ITEM, orderItem1);
        HomeItem item2 = new HomeItem(HomeItem.NEAR_TASK_ITEM, orderItem2);
        HomeItem item3 = new HomeItem(HomeItem.NEAR_TASK_ITEM, orderItem3);
        HomeItem item4 = new HomeItem(HomeItem.NEAR_TASK_ITEM, orderItem4);

        items.add(item1);
        items.add(item2);
        items.add(item3);
        items.add(item4);
    }

    private void initBanner(){
        View bannerView = LayoutInflater.from(getContext()).inflate(R.layout.rv_banner,null);
        final ViewPager bannerViewPager = (ViewPager) bannerView.findViewById(R.id.home_banner_vp);
        List<ImageView> bannerViewPagerList = new ArrayList<>();
        bannerViewPagerList.addAll(getBanner(bannerViewPager.getContext()));
        BannerViewPagerAdapter bannerViewPagerAdapter = new BannerViewPagerAdapter(bannerViewPagerList);
        bannerViewPager.setAdapter(bannerViewPagerAdapter);
        bannerViewPager.setCurrentItem(Integer.MAX_VALUE / 2);
        final BannerViewPagerPointer bannerViewPagerPointer = new BannerViewPagerPointer(bannerViewPager.getContext(), bannerViewPager,
                (LinearLayout) bannerView.findViewById(R.id.home_banner_vp_pointer), bannerViewPagerList.size());
        bannerViewPager.addOnPageChangeListener(bannerViewPagerPointer);
        new Timer().schedule(new TimerTask() {  //定时滚动图片
            @Override
            public void run() {
                bannerViewPager.post(new Runnable() {
                    @Override
                    public void run() {
                        if(!bannerViewPagerPointer.isDraging)   //不在拖动状态则下一页
                            bannerViewPager.setCurrentItem(bannerViewPager.getCurrentItem()+1);
                    }
                });
            }
        },5000,5000);
        adapter.addHeaderView(bannerView);
    }

    private void initButton(){
        View buttonView = LayoutInflater.from(getContext()).inflate(R.layout.rv_btn,null);
        Button releaseBtn = (Button) buttonView.findViewById(R.id.home_release_btn);
        Button acceptBtn = (Button) buttonView.findViewById(R.id.home_accept_btn);
        releaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(HttpUtil.isNetworkConnected(getContext())){  //网络已连接
                    if(mDataCache.getUser() != null){
                        openActivity(ReleaseActivity.class);
                    }
                    else{
                        toastShort("请先登录");
                        openActivity(LoginActivity.class);
                    }
                }
                else{
                    toastShort("请检查网络连接");
                }
            }
        });
        adapter.addHeaderView(buttonView);
    }

    private void initPointer(){
        final View pointerView = LayoutInflater.from(getContext()).inflate(R.layout.rv_pt,null);
        homeViewPagerTab = (ViewPagerTab) pointerView.findViewById(R.id.home_tab);
        homeViewPagerTab.addTab("附近任务");
        homeViewPagerTab.addTab("二手交易");
        homeViewPagerTab.addTab("最新兼职");
        homeViewPagerTab.setSelect(0);
        homeViewPagerTab.setEventBus(true);
        headView  = adapter.getHeaderLayout();

        homeTab = (ViewPagerTab) view.findViewById(R.id.home_tab);
        homeTab.addTab("附近任务");
        homeTab.addTab("二手交易");
        homeTab.addTab("最新兼职");
        homeTab.setSelect(0);
        homeTab.setEventBus(true);


        adapter.addHeaderView(pointerView);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(pointerView.getY() + headView.getY() <= 0){
                    if(!homeTab.isShown()) homeTab.setVisibility(View.VISIBLE);
                }
                else{
                    if(homeTab.isShown()) homeTab.setVisibility(View.GONE);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTabSelected(TabSelectedEvent event){
        homeTab.setSelect(event.position);
        homeViewPagerTab.setSelect(event.position);
    }

    private List<ImageView> getBanner(Context context){
        List<ImageView> list = new ArrayList<>();
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.background);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        ImageView imageView2 = new ImageView(context);
        imageView2.setImageResource(R.drawable.ic_action_home);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        ImageView imageView3 = new ImageView(context);
        imageView3.setImageResource(R.drawable.ic_action_order);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        list.add(imageView);
        list.add(imageView2);
        list.add(imageView3);
        return list;
    }
}
