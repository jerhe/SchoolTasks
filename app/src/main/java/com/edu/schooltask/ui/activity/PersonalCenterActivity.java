package com.edu.schooltask.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.IconMenuAdapter;
import com.edu.schooltask.ui.base.BaseActivity;
import com.edu.schooltask.beans.PersonalCenterInfo;
import com.edu.schooltask.item.IconMenuItem;
import com.edu.schooltask.utils.GsonUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import server.api.SchoolTask;
import server.api.event.user.GetPersonalCenterInfoEvent;

public class PersonalCenterActivity extends BaseActivity {
    @BindView(R.id.pc_rv) RecyclerView recyclerView;

    List<IconMenuItem> items = new ArrayList<>();
    IconMenuAdapter adapter;

    Animation fadeInAnimation;

    boolean hasPayPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_center);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        adapter = new IconMenuAdapter(items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                switch (position){
                    case 0:
                        openActivity(UserEditActivity.class);
                        break;
                    case 1:
                        Intent loginPwdIntent = new Intent(PersonalCenterActivity.this, UpdatePwdActivity.class);
                        loginPwdIntent.putExtra("relationType",0);
                        startActivity(loginPwdIntent);
                        break;
                    case 2:
                        if(hasPayPwd){
                            Intent intent = new Intent(PersonalCenterActivity.this, UpdatePwdActivity.class);
                            intent.putExtra("relationType",1);
                            startActivity(intent);
                        }
                        else{
                            openActivity(SetPayPwdActivity.class);
                        }
                        break;
                }
            }
        });
        SchoolTask.getPersonalCenterInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void setData(int credit, String registerTime){
        items.add(new IconMenuItem(IconMenuItem.HORIZONTAL, R.drawable.ic_user_edit, getString(R.string.editUserInfo)));
        items.add(new IconMenuItem(IconMenuItem.HORIZONTAL, R.drawable.ic_lock, getString(R.string.updateLoginPassword)));
        items.add(new IconMenuItem(IconMenuItem.HORIZONTAL, R.drawable.ic_lock2, getString(R.string.updatePayPassword),
                hasPayPwd ? "" : getString(R.string.unset)));
        items.add(new IconMenuItem());
        items.add(new IconMenuItem(IconMenuItem.VALUE, 0, getString(R.string.credit), credit+""));
        items.add(new IconMenuItem(IconMenuItem.VALUE, 0, getString(R.string.registerTime), registerTime));
        adapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetPersonalCenterInfo(GetPersonalCenterInfoEvent event){
        if(event.isOk()){
            PersonalCenterInfo personalCenterInfo = GsonUtil.toPersonalCenterInfo(event.getData());
            hasPayPwd = personalCenterInfo.isHasPayPwd();
            setData(personalCenterInfo.getCredit(), personalCenterInfo.getRegisterTime());
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.startAnimation(fadeInAnimation);
        }
        else{
            toastShort(event.getError());
        }
    }
}
