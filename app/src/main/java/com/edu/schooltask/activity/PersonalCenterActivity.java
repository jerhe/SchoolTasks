package com.edu.schooltask.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.IconMenuAdapter;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.beans.PersonalCenterInfo;
import com.edu.schooltask.item.IconMenuItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import server.api.SchoolTask;
import server.api.user.GetPersonalCenterInfoEvent;

public class PersonalCenterActivity extends BaseActivity {

    RecyclerView recyclerView;
    List<IconMenuItem> items = new ArrayList<>();
    IconMenuAdapter adapter;

    boolean hasPayPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_center);
        EventBus.getDefault().register(this);
        recyclerView = getView(R.id.pc_rv);
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
        items.add(new IconMenuItem(IconMenuItem.HORIZONTAL, R.drawable.ic_action_set, "编辑资料"));
        items.add(new IconMenuItem(IconMenuItem.HORIZONTAL, R.drawable.ic_action_set, "登录密码"));
        items.add(new IconMenuItem(IconMenuItem.HORIZONTAL, R.drawable.ic_action_set, "支付密码",
                hasPayPwd ? "":"未设置"));
        items.add(new IconMenuItem());
        items.add(new IconMenuItem(IconMenuItem.VALUE,   "信用积分", credit+""));
        items.add(new IconMenuItem(IconMenuItem.VALUE,  "注册时间", registerTime));
        adapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetPersonalCenterInfo(GetPersonalCenterInfoEvent event){
        if(event.isOk()){
            PersonalCenterInfo personalCenterInfo = new Gson().fromJson(new Gson().toJson(event.getData()), new TypeToken<PersonalCenterInfo>(){}.getType());
            hasPayPwd = personalCenterInfo.isHasPayPwd();
            setData(personalCenterInfo.getCredit(), personalCenterInfo.getRegisterTime());
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
        }
        else{
            toastShort(event.getError());
        }
    }
}
