package com.edu.schooltask.activity;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.PersonalCenterInfo;
import com.edu.schooltask.item.IconMenuItem;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.view.listener.ItemClickListener;
import com.edu.schooltask.view.recyclerview.IconMenuRecyclerView;
import com.edu.schooltask.utils.GsonUtil;

import butterknife.BindView;
import server.api.client.UserClient;
import server.api.result.Result;

public class PersonalCenterActivity extends BaseActivity {
    @BindView(R.id.pc_imrv) IconMenuRecyclerView recyclerView;

    private Result getPersonalCenterInfoResult = new Result(true) {
        @Override
        public void onResponse(int id) {

        }

        @Override
        public void onSuccess(int id, Object data) {
            PersonalCenterInfo personalCenterInfo = GsonUtil.toPersonalCenterInfo(data);
            hasPayPwd = personalCenterInfo.isHasPayPwd();
            credit = String.valueOf(personalCenterInfo.getCredit());
            registerTime = personalCenterInfo.getRegisterTime();
            PersonalCenterActivity.this.setData();
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.startAnimation(fadeInAnimation);
        }

        @Override
        public void onFailed(int id, int code, String error) {
            toastShort(error);
        }
    };

    Animation fadeInAnimation;

    boolean hasPayPwd;
    String credit;
    String registerTime;

    @Override
    public int getLayout() {
        return R.layout.activity_personal_center;
    }

    @Override
    public void init() {
        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        recyclerView.setItemClickListener(new ItemClickListener<IconMenuItem>() {
            @Override
            public void onItemClick(int position, IconMenuItem iconMenuItem) {
                switch (position){
                    case 0:
                        openActivity(UserEditActivity.class);
                        break;
                    case 1:
                        openActivity(UpdateLoginPwdActivity.class);
                        break;
                    case 2:
                        if(hasPayPwd){
                            openActivity(UpdatePayPwdActivity.class);
                        }
                        else{
                            openActivity(SetPayPwdActivity.class);
                        }
                        break;
                }
            }
        });
        UserClient.getPersonalCenterInfo(getPersonalCenterInfoResult);
    }


    private void setData(){
        if(recyclerView.get().size() > 0) recyclerView.clear();
        recyclerView.add(new IconMenuItem(IconMenuItem.HORIZONTAL, R.drawable.ic_user_edit, getString(R.string.editUserInfo)));
        recyclerView.add(new IconMenuItem(IconMenuItem.HORIZONTAL, R.drawable.ic_lock, getString(R.string.loginPassword)));
        recyclerView.add(new IconMenuItem(IconMenuItem.HORIZONTAL, R.drawable.ic_lock2, getString(R.string.payPassword),
                hasPayPwd ? "" : getString(R.string.unset)));
        recyclerView.add(new IconMenuItem());
        recyclerView.add(new IconMenuItem(IconMenuItem.VALUE, 0, getString(R.string.credit), credit));
        recyclerView.add(new IconMenuItem(IconMenuItem.VALUE, 0, getString(R.string.registerTime), registerTime));
    }

}
