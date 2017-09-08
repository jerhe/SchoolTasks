package com.edu.schooltask.ui.fragment.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.beans.UserInfoWithToken;
import com.edu.schooltask.event.LoginSuccessEvent;
import com.edu.schooltask.event.LogoutEvent;
import com.edu.schooltask.item.IconMenuItem;
import com.edu.schooltask.ui.activity.AboutActivity;
import com.edu.schooltask.ui.activity.HelpActivity;
import com.edu.schooltask.ui.activity.LoginActivity;
import com.edu.schooltask.ui.activity.MoneyActivity;
import com.edu.schooltask.ui.activity.PersonalCenterActivity;
import com.edu.schooltask.ui.activity.SettingActivity;
import com.edu.schooltask.ui.activity.UserActivity;
import com.edu.schooltask.ui.activity.VoucherActivity;
import com.edu.schooltask.ui.base.BaseFragment;
import com.edu.schooltask.ui.view.recyclerview.IconMenuRecyclerView;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.UserUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.rong.imkit.RongIM;
import server.api.event.user.RefreshHeadBGEvent;
import server.api.event.user.UpdateUserInfoEvent;


/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class UserFragment extends BaseFragment{
    @BindView(R.id.up_rv) IconMenuRecyclerView userRecyclerView;
    @BindView(R.id.up_name) TextView nameText;
    @BindView(R.id.up_head) CircleImageView headImage;
    @BindView(R.id.up_bg) ImageView bgImage;
    @BindView(R.id.up_home) TextView homeBtn;
    @BindView(R.id.up_rv2) IconMenuRecyclerView systemRecyclerView;

    @OnClick(R.id.up_bg)
    public void bgClick(){
        if (!UserUtil.hasLogin()) {
            Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
            startActivity(loginIntent);
        }
    }

    public UserFragment(){
        super(R.layout.fragment_user_page);
    }

    @Override
    protected void init() {
        ButterKnife.bind(this, view);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, 1);
        userRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        userRecyclerView.add(new IconMenuItem(IconMenuItem.VERTICAL, R.drawable.ic_personal_center, getString(R.string.personalCenter)));
        userRecyclerView.add(new IconMenuItem(IconMenuItem.VERTICAL, R.drawable.ic_voucher, getString(R.string.voucher)));
        userRecyclerView.add(new IconMenuItem(IconMenuItem.VERTICAL, R.drawable.ic_account, getString(R.string.money)));
        userRecyclerView.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (UserUtil.hasLogin()) {
                    switch (position) {
                        case 0:
                            openActivity(PersonalCenterActivity.class);
                            break;
                        case 1:
                            openActivity(VoucherActivity.class);
                            break;
                        case 2:
                            openActivity(MoneyActivity.class);
                            break;
                        default:
                    }
                }
                else {
                    toastShort(getString(R.string.unlogin_tip));
                    openActivity(LoginActivity.class);
                }
            }
        });

        systemRecyclerView.add(new IconMenuItem(IconMenuItem.HORIZONTAL, R.drawable.ic_setting, "设置"));
        systemRecyclerView.add(new IconMenuItem(IconMenuItem.HORIZONTAL, R.drawable.ic_help, "帮助"));
        systemRecyclerView.add(new IconMenuItem(IconMenuItem.HORIZONTAL, R.drawable.ic_share, "分享"));
        systemRecyclerView.add(new IconMenuItem(IconMenuItem.HORIZONTAL, R.drawable.ic_about, "关于"));
        systemRecyclerView.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
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
                intent.putExtra("name", user.getName());
                startActivity(intent);
            }
        });
    }

    private void setUserImage(UserInfoWithToken user){
        UserUtil.setHead(getContext(), user, headImage);
        UserUtil.setBackground(getContext(), user, bgImage);
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
        UserUtil.deleteLoginUser(); //删除用户
        nameText.setText("登录/注册");
        homeBtn.setVisibility(View.GONE);
        Glide.with(this).load(R.drawable.rc_default_portrait).into(headImage);
        Glide.with(this).load(R.drawable.background).into(bgImage);
        RongIM.getInstance().logout();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateUserInfo(UpdateUserInfoEvent event){
        if(event.isOk()){
            UserInfo user = GsonUtil.toUserInfoWithToken(event.getData());
            UserUtil.updateInfo(user);
            setUserInfo(user);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateHeadBG(RefreshHeadBGEvent event){
        if (event.isOk()){
            setUserImage(UserUtil.getLoginUser());
        }
    }
}
