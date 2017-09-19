package com.edu.schooltask.fragment.main;

import android.content.Intent;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.activity.FriendActivity;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.beans.UserInfoWithToken;
import com.edu.schooltask.event.UpdateBgEvent;
import com.edu.schooltask.event.UpdateHeadEvent;
import com.edu.schooltask.item.IconMenuItem;
import com.edu.schooltask.activity.AboutActivity;
import com.edu.schooltask.activity.HelpActivity;
import com.edu.schooltask.activity.LoginActivity;
import com.edu.schooltask.activity.MoneyActivity;
import com.edu.schooltask.activity.PersonalCenterActivity;
import com.edu.schooltask.activity.SettingActivity;
import com.edu.schooltask.activity.UserActivity;
import com.edu.schooltask.activity.VoucherActivity;
import com.edu.schooltask.base.BaseFragment;
import com.edu.schooltask.view.listener.ItemClickListener;
import com.edu.schooltask.view.recyclerview.IconMenuRecyclerView;
import com.edu.schooltask.utils.UserUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.rong.imkit.RongIM;


/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class UserFragment extends BaseFragment{
    @BindView(R.id.up_rv) IconMenuRecyclerView userRV;
    @BindView(R.id.up_name) TextView nameText;
    @BindView(R.id.up_head) CircleImageView headImage;
    @BindView(R.id.up_bg) ImageView bgImage;
    @BindView(R.id.up_home) TextView homeBtn;
    @BindView(R.id.up_rv2) IconMenuRecyclerView systemRV;

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
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(4, 1);
        userRV.setLayoutManager(staggeredGridLayoutManager);
        userRV.add(new IconMenuItem(IconMenuItem.VERTICAL, R.drawable.ic_personal_center, getString(R.string.personalCenter)));
        userRV.add(new IconMenuItem(IconMenuItem.VERTICAL, R.drawable.ic_friend, "我的好友"));
        userRV.add(new IconMenuItem(IconMenuItem.VERTICAL, R.drawable.ic_voucher, getString(R.string.voucher)));
        userRV.add(new IconMenuItem(IconMenuItem.VERTICAL, R.drawable.ic_account, getString(R.string.money)));
        userRV.setItemClickListener(new ItemClickListener<IconMenuItem>() {
            @Override
            public void onItemClick(int position, IconMenuItem iconMenuItem) {
                if (UserUtil.hasLogin()) {
                    switch (position) {
                        case 0:
                            openActivity(PersonalCenterActivity.class);
                            break;
                        case 1:
                            openActivity(FriendActivity.class);
                            break;
                        case 2:
                            openActivity(VoucherActivity.class);
                            break;
                        case 3:
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

        systemRV.add(new IconMenuItem(IconMenuItem.HORIZONTAL, R.drawable.ic_setting, "设置"));
        systemRV.add(new IconMenuItem(IconMenuItem.HORIZONTAL, R.drawable.ic_help, "帮助"));
        systemRV.add(new IconMenuItem(IconMenuItem.HORIZONTAL, R.drawable.ic_share, "分享"));
        systemRV.add(new IconMenuItem(IconMenuItem.HORIZONTAL, R.drawable.ic_about, "关于"));
        systemRV.setItemClickListener(new ItemClickListener<IconMenuItem>() {
            @Override
            public void onItemClick(int position, IconMenuItem iconMenuItem) {
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
        UserUtil.setHead(getContext(), user.getHead(), headImage);
        UserUtil.setBackground(getContext(), user.getBg(), bgImage);
    }

    @Override
    protected void onLogin() {
        UserInfoWithToken user = UserUtil.getLoginUser();
        if(user != null){
            setUserInfo(user);
            setUserImage(user);
        }
    }

    @Override
    protected void onLogout() {
        UserUtil.deleteLoginUser(); //删除用户
        nameText.setText("登录/注册");
        homeBtn.setVisibility(View.GONE);
        Glide.with(this).load(R.drawable.rc_default_portrait).into(headImage);
        Glide.with(this).load(R.drawable.background).into(bgImage);
        RongIM.getInstance().logout();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateHead(UpdateHeadEvent event){
        UserInfoWithToken user = UserUtil.getLoginUser();
        if(user != null){
            UserUtil.setHead(getContext(), user.getHead(), headImage);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateBg(UpdateBgEvent event){
        UserInfoWithToken user = UserUtil.getLoginUser();
        if(user != null){
            UserUtil.setBackground(getContext(), user.getBg(), bgImage);
        }
    }
}
