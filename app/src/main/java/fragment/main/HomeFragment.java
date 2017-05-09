package fragment.main;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.edu.schooltask.LoginActivity;
import com.edu.schooltask.R;
import com.edu.schooltask.ReleaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import adapter.BannerViewPagerAdapter;
import base.BaseFragment;
import beans.User;
import http.HttpUtil;
import listener.BannerViewPagerPointer;

/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class HomeFragment extends BaseFragment {
    private User user;
    private ViewPager bannerViewPager;
    private List<ImageView> bannerViewPagerList = new ArrayList<>();

    private Button releaseBtn;
    private Button acceptBtn;

    public HomeFragment() {
        super(R.layout.fragment_home_page);
    }

    @Override
    protected void init(){
        //图片轮播
        bannerViewPager = (ViewPager) view.findViewById(R.id.home_vp);
        bannerViewPagerList.addAll(getBanner());
        BannerViewPagerAdapter bannerViewPagerAdapter = new BannerViewPagerAdapter(bannerViewPagerList);
        bannerViewPager.setAdapter(bannerViewPagerAdapter);
        bannerViewPager.setCurrentItem(Integer.MAX_VALUE / 2);
        final BannerViewPagerPointer bannerViewPagerPointer = new BannerViewPagerPointer(getContext(), bannerViewPager,
                (LinearLayout) view.findViewById(R.id.home_vp_pointer), bannerViewPagerList.size());
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

        //发布接受任务
        releaseBtn = (Button) view.findViewById(R.id.home_release_btn);
        acceptBtn = (Button) view.findViewById(R.id.home_accept_btn);
        releaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(HttpUtil.isNetworkConnected(getContext())){  //网络已连接
                    if(user != null){
                        Intent intent = new Intent(getActivity(), ReleaseActivity.class);
                        startActivity(intent);
                    }
                    else{
                        toastShort("请先登录");
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    }
                }
                else{
                    toastShort("请检查网络连接");
                }
            }
        });
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(HttpUtil.isNetworkConnected(getContext())){  //网络已连接
                    if(user != null){
                        //TODO
                    }
                    else{
                        toastShort("请先登录");
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    }
                }
                else{
                    toastShort("请检查网络连接");
                }
            }
        });
    }


    //TEST
    private List<ImageView> getBanner(){
        List<ImageView> list = new ArrayList<>();
        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(R.drawable.background);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        ImageView imageView2 = new ImageView(getContext());
        imageView2.setImageResource(R.drawable.ic_action_home);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        ImageView imageView3 = new ImageView(getContext());
        imageView3.setImageResource(R.drawable.ic_action_order);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        list.add(imageView);
        list.add(imageView2);
        list.add(imageView3);
        return list;
    }

    public void updateUser(User user){
        this.user = user;
    }
}
