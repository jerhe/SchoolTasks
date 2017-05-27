package com.edu.schooltask.fragment.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alexvasilkov.gestures.commons.circle.CircleGestureImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.activity.LoginActivity;
import com.edu.schooltask.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import com.edu.schooltask.activity.MoneyActivity;
import com.edu.schooltask.activity.UserActivity;
import com.edu.schooltask.adapter.FunctionAdapter;
import com.edu.schooltask.base.BaseFragment;
import com.edu.schooltask.beans.User;
import com.edu.schooltask.event.LoginSuccessEvent;
import com.edu.schooltask.event.LogoutEvent;
import com.edu.schooltask.item.FunctionItem;
import com.edu.schooltask.utils.GlideUtil;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import de.hdodenhof.circleimageview.CircleImageView;
import server.api.user.UploadBGEvent;
import server.api.user.UploadHeadEvent;


/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class UserFragment extends BaseFragment {
    private RecyclerView userFunctionRecyclerView;
    private List<FunctionItem> userFunctionItemList = new ArrayList<>();
    private RecyclerView systemFunctionRecyclerView;
    private List<FunctionItem> systemFunctionItemList = new ArrayList<>();

    private TextView nameText;
    private CircleImageView headImage;
    private ImageView bgImage;
    private TextView homeBtn;
    public UserFragment(){
        super(R.layout.fragment_user_page);
    }

    @Override
    protected void init(){
        //头像
        headImage = (CircleImageView) view.findViewById(R.id.up_head);
        nameText = (TextView) view.findViewById(R.id.up_name);
        bgImage = (ImageView) view.findViewById(R.id.up_bg);
        homeBtn = (TextView) view.findViewById(R.id.up_home);

        userFunctionRecyclerView = (RecyclerView)view.findViewById(R.id.up_rv);
        FunctionAdapter userFunctionAdapter = new FunctionAdapter(R.layout.item_user_function, userFunctionItemList);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(4,1);
        userFunctionRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        userFunctionRecyclerView.setAdapter(userFunctionAdapter);
        userFunctionItemList.add(new FunctionItem(R.drawable.ic_action_account, "XX"));
        userFunctionItemList.add(new FunctionItem(R.drawable.ic_action_account, "XX"));
        userFunctionItemList.add(new FunctionItem(R.drawable.ic_action_account, "积分"));
        userFunctionItemList.add(new FunctionItem(R.drawable.ic_action_account, "钱包"));
        userFunctionAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                User user = mDataCache.getUser();
                if(user == null){
                    toastShort("请先登录");
                    openActivity(LoginActivity.class);
                }
                else{
                    switch (position){
                        case 0:

                            break;
                        case 1:
                            break;
                        case 3:
                            openActivity(MoneyActivity.class);
                            break;
                        default:
                    }
                }
            }
        });


        systemFunctionRecyclerView = (RecyclerView)view.findViewById(R.id.up_rv2);
        FunctionAdapter systemFunctionAdapter = new FunctionAdapter(R.layout.item_system_function, systemFunctionItemList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        systemFunctionRecyclerView.setLayoutManager(linearLayoutManager);
        systemFunctionRecyclerView.setAdapter(systemFunctionAdapter);
        systemFunctionItemList.add(new FunctionItem(R.drawable.ic_action_set, "设置"));
        systemFunctionItemList.add(new FunctionItem(R.drawable.ic_action_set, "设置"));
        systemFunctionItemList.add(new FunctionItem(R.drawable.ic_action_set, "设置"));
        systemFunctionItemList.add(new FunctionItem(R.drawable.ic_action_set, "关于"));

        bgImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDataCache.getUser() == null){
                    Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(loginIntent);
                }
            }
        });

        onLoginSuccess(null);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginSuccess(LoginSuccessEvent event){
        final User user = mDataCache.getUser();
        if(user != null){
            nameText.setText(user.getName());
            homeBtn.setVisibility(View.VISIBLE);
            homeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), UserActivity.class);
                    intent.putExtra("user",user);
                    startActivity(intent);
                }
            });
            GlideUtil.setHead(getContext(), user.getUserId(), headImage, false);
            GlideUtil.setBackground(getContext(), user.getUserId(), bgImage, false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogout(LogoutEvent event){
        mDataCache.removeUser();
        nameText.setText("登录/注册");
        homeBtn.setVisibility(View.GONE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUploadHead(UploadHeadEvent event){
        if(event.isOk()){
            User user = mDataCache.getUser();
            GlideUtil.setHead(getContext(), user.getUserId(), headImage, true);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUploadBG(UploadBGEvent event){
        if(event.isOk()){
            User user = mDataCache.getUser();
            GlideUtil.setBackground(getContext(), user.getUserId(), bgImage, true);
        }
    }
}
