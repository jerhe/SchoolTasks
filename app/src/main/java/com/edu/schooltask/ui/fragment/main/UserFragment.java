package com.edu.schooltask.ui.fragment.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.ui.activity.AboutActivity;
import com.edu.schooltask.ui.activity.HelpActivity;
import com.edu.schooltask.ui.activity.LoginActivity;
import com.edu.schooltask.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import com.edu.schooltask.ui.activity.MoneyActivity;
import com.edu.schooltask.ui.activity.SettingActivity;
import com.edu.schooltask.ui.activity.UserActivity;
import com.edu.schooltask.ui.activity.VoucherActivity;
import com.edu.schooltask.adapter.IconMenuAdapter;
import com.edu.schooltask.ui.base.BaseFragment;
import com.edu.schooltask.beans.UserInfoWithToken;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.event.LoginSuccessEvent;
import com.edu.schooltask.event.LogoutEvent;
import com.edu.schooltask.item.IconMenuItem;
import com.edu.schooltask.utils.GlideUtil;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.UserUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import server.api.SchoolTask;
import server.api.user.UpdateUserInfoEvent;


/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class UserFragment extends BaseFragment{
    @BindView(R.id.up_rv) RecyclerView userFunctionRecyclerView;
    @BindView(R.id.up_name) TextView nameText;
    @BindView(R.id.up_head) CircleImageView headImage;
    @BindView(R.id.up_bg) ImageView bgImage;
    @BindView(R.id.up_home) TextView homeBtn;
    @BindView(R.id.up_rv2) RecyclerView systemFunctionRecyclerView;

    @OnClick(R.id.up_bg)
    public void bgClick(){
        if (!UserUtil.hasLogin()) {
            Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
            startActivity(loginIntent);
        }
    }

    private List<IconMenuItem> userIconMenuItemList = new ArrayList<>();
    private List<IconMenuItem> systemIconMenuItemList = new ArrayList<>();

    public UserFragment(){
        super(R.layout.fragment_user_page);
    }

    @Override
    protected void init() {
        ButterKnife.bind(this, view);
        IconMenuAdapter userIconMenuAdapter = new IconMenuAdapter(userIconMenuItemList);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(4, 1);
        userFunctionRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        userFunctionRecyclerView.setAdapter(userIconMenuAdapter);
        userIconMenuItemList.add(new IconMenuItem(IconMenuItem.VERTICAL, R.drawable.ic_action_account, "校园服务"));
        userIconMenuItemList.add(new IconMenuItem(IconMenuItem.VERTICAL, R.drawable.ic_action_account, "代金券"));
        userIconMenuItemList.add(new IconMenuItem(IconMenuItem.VERTICAL, R.drawable.ic_action_account, "任务商店"));
        userIconMenuItemList.add(new IconMenuItem(IconMenuItem.VERTICAL, R.drawable.ic_action_account, "账户"));
        userIconMenuAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (UserUtil.hasLogin()) {
                    switch (position) {
                        case 0:
                            break;
                        case 1:
                            openActivity(VoucherActivity.class);
                            break;
                        case 3:
                            openActivity(MoneyActivity.class);
                            break;
                        default:
                    }
                }
                else {
                    toastShort("请先登录");
                    openActivity(LoginActivity.class);
                }
            }
        });

        IconMenuAdapter systemIconMenuAdapter = new IconMenuAdapter(systemIconMenuItemList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        systemFunctionRecyclerView.setLayoutManager(linearLayoutManager);
        systemFunctionRecyclerView.setAdapter(systemIconMenuAdapter);
        systemIconMenuItemList.add(new IconMenuItem(IconMenuItem.HORIZONTAL, R.drawable.ic_action_set, "设置"));
        systemIconMenuItemList.add(new IconMenuItem(IconMenuItem.HORIZONTAL, R.drawable.ic_action_set, "帮助"));
        systemIconMenuItemList.add(new IconMenuItem(IconMenuItem.HORIZONTAL, R.drawable.ic_action_set, "分享"));
        systemIconMenuItemList.add(new IconMenuItem(IconMenuItem.HORIZONTAL, R.drawable.ic_action_set, "关于"));
        systemIconMenuAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                switch (position) {
                    case 0:
                        openActivity(SettingActivity.class);
                        break;
                    case 1:
                        openActivity(HelpActivity.class);
                        break;
                    case 2: //分享
                        break;
                    case 3:
                        openActivity(AboutActivity.class);
                        break;
                }
            }
        });

        onLoginSuccess(null);
    }

    private void setUserInfo(final UserInfo user){
        nameText.setText(user.getName());
        homeBtn.setVisibility(View.VISIBLE);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserActivity.class);
                intent.putExtra("userId", user.getUserId());
                startActivity(intent);
            }
        });
    }

    private void setUserImage(UserInfoWithToken user){
        GlideUtil.setHead(getContext(), user.getUserId(), headImage);
        GlideUtil.setBackground(getContext(), user.getUserId(), bgImage);
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
        UserInfoWithToken user = UserUtil.getLoginUser();
        if(user != null){
            setUserInfo(user);
            setUserImage(user);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogout(LogoutEvent event){
        UserUtil.deleteLoginUser();
        nameText.setText("登录/注册");
        homeBtn.setVisibility(View.GONE);
        Glide.with(this).load(R.drawable.head).into(headImage);
        Glide.with(this).load(R.drawable.background).into(bgImage);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateUserInfo(UpdateUserInfoEvent event){
        if(event.isOk()){
            UserInfo user = GsonUtil.toUserInfoWithToken(event.getData());
            UserUtil.updateInfo(user);
            setUserInfo(user);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        UserInfoWithToken user = UserUtil.getLoginUser();
        if(user != null){
            Glide.clear(bgImage);
            GlideUtil.setBackground(getContext(), user.getUserId(), bgImage);
            Glide.clear(headImage);
            GlideUtil.setHead(getContext(), user.getUserId(), headImage);
        }
    }
}
