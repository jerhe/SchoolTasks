package com.edu.schooltask.fragment.main;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.activity.DynamicListActivity;
import com.edu.schooltask.activity.LoginActivity;
import com.edu.schooltask.activity.ReleaseTaskActivity;
import com.edu.schooltask.activity.TaskListActivity;
import com.edu.schooltask.activity.WaitAcceptOrderActivity;
import com.edu.schooltask.adapter.TaskItemAdapter;
import com.edu.schooltask.base.BaseFragment;
import com.edu.schooltask.beans.BaseList;
import com.edu.schooltask.beans.UserInfoWithToken;
import com.edu.schooltask.beans.task.TaskItem;
import com.edu.schooltask.event.RefreshHomeEvent;
import com.edu.schooltask.item.IconMenuItem;
import com.edu.schooltask.utils.DensityUtil;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.UserUtil;
import com.edu.schooltask.view.listener.ItemClickListener;
import com.edu.schooltask.view.recyclerview.IconMenuRecyclerView;
import com.edu.schooltask.view.recyclerview.RefreshPageRecyclerView;
import com.youth.banner.Banner;
import com.youth.banner.loader.ImageLoader;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import server.api.client.TaskClient;
import server.api.result.Result;

/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class HomeFragment extends BaseFragment{

    private RefreshPageRecyclerView<TaskItem> recyclerView;
    private Banner banner;
    private IconMenuRecyclerView iconMenuRV;

    List<String> images = new ArrayList<>();

    public HomeFragment() {
        super(R.layout.fragment_home_page);
    }

    @Override
    protected void init(){
        recyclerView = new RefreshPageRecyclerView<TaskItem>(getContext(), false) {
            @Override
            protected BaseQuickAdapter initAdapter(List<TaskItem> list) {
                return new TaskItemAdapter(list);
            }

            @Override
            protected void requestPageData(Result result, int page) {
                getSchoolTask(result, page);
            }

            @Override
            protected void onSuccess(int id, Object data) {
                BaseList<TaskItem> baseList = GsonUtil.toTaskItemList(data);
                recyclerView.add(baseList.getList(), baseList.isMore());
            }

            @Override
            protected void onFailed(int id, int code, String error) {
                toastShort(error);
            }

            @Override
            protected void onItemClick(int position, TaskItem taskItem) {
                openActivity(WaitAcceptOrderActivity.class, "taskItem", taskItem);
            }
        };
        recyclerView.setEmptyView(R.layout.empty_task);
        recyclerView.setEmptyAndHeader(true);
        recyclerView.setBackgroundColor(Color.WHITE);
        addView(recyclerView);
        initBanner();
        initMenu();
        View titleText = inflate(R.layout.layout_home_title);
        recyclerView.addHeader(titleText);
        recyclerView.refresh();
    }

    private void initBanner(){
        banner = new Banner(getContext(), null);
        banner.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                DensityUtil.dipToPx(getContext(), 200)));
        banner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                Glide.with(context).load(path).into(imageView);
            }
        });
        images.add("http://oqqzw04zt.bkt.clouddn.com/banner1.jpg");
        banner.setImages(images);
        banner.start();
        recyclerView.addHeader(banner);
    }

    private void initMenu(){
        iconMenuRV = new IconMenuRecyclerView(getContext(), null);
        iconMenuRV.setBackgroundColor(Color.WHITE);
        iconMenuRV.setLayoutManager(new StaggeredGridLayoutManager(4, 1));
        List<IconMenuItem> list = new ArrayList<>();
        list.add(new IconMenuItem(IconMenuItem.VERTICAL_LARGE, R.drawable.ic_release_task,
                R.drawable.shape_ring_release, "发布任务"));
        list.add(new IconMenuItem(IconMenuItem.VERTICAL_LARGE, R.drawable.ic_task_list,
                R.drawable.shape_ring_task_list, "任务栏"));
        list.add(new IconMenuItem(IconMenuItem.VERTICAL_LARGE, R.drawable.ic_market,
                R.drawable.shape_ring_market, "校园市场"));
        list.add(new IconMenuItem(IconMenuItem.VERTICAL_LARGE, R.drawable.ic_dynamic,
                R.drawable.shape_ring_dynamic, "校圈"));
        iconMenuRV.add(list);
        iconMenuRV.setItemClickListener(new ItemClickListener<IconMenuItem>() {
            @Override
            public void onItemClick(int position, IconMenuItem iconMenuItem) {
                switch (position){
                    case 0:
                        if(!UserUtil.hasLogin()){
                            toastShort(getString(R.string.unlogin_tip));
                            openActivity(LoginActivity.class);
                            return;
                        }
                        openActivity(ReleaseTaskActivity.class);
                        break;
                    case 1:
                        openActivity(TaskListActivity.class);
                        break;
                    case 2:
                        toastShort("敬请期待...");
                        break;
                    case 3:
                        openActivity(DynamicListActivity.class);
                        break;
                }
            }
        });
        recyclerView.addHeader(iconMenuRV);
    }


    private void getSchoolTask(Result result, int page){
        UserInfoWithToken user = UserUtil.getLoginUser();
        if(user != null){
            TaskClient.getTaskList(result, user.getSchool(), page);
        }
        else{   //用户未登录则获取最新任务
            TaskClient.getTaskList(result, "", page);
        }
    }

    @Override
    protected void onLogin() {
        recyclerView.refresh();
    }

    @Override
    protected void onLogout() {
        recyclerView.refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshHome(RefreshHomeEvent event){
        recyclerView.refresh();
    }
}
