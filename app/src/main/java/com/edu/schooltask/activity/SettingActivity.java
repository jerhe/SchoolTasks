package com.edu.schooltask.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.IconMenuAdapter;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.event.LogoutEvent;
import com.edu.schooltask.item.IconMenuItem;
import com.edu.schooltask.utils.DialogUtil;
import com.edu.schooltask.utils.GlideCacheUtil;
import com.orhanobut.dialogplus.DialogPlus;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingActivity extends BaseActivity {
    @BindView(R.id.setting_rv) RecyclerView recyclerView;

    IconMenuAdapter adapter;
    List<IconMenuItem> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        adapter = new IconMenuAdapter(items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.openLoadAnimation();

        String cacheSize = GlideCacheUtil.getInstance().getCacheSize(this);

        items.add(new IconMenuItem(IconMenuItem.HORIZONTAL, R.drawable.ic_action_set, "个人中心"));
        items.add(new IconMenuItem(IconMenuItem.HORIZONTAL, R.drawable.ic_action_set, "消息隐私"));
        items.add(new IconMenuItem());
        items.add(new IconMenuItem(IconMenuItem.HORIZONTAL, R.drawable.ic_action_set, "清理缓存", cacheSize));
        items.add(new IconMenuItem());
        items.add(new IconMenuItem(IconMenuItem.HORIZONTAL, R.drawable.ic_action_set, "退出账号"));

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                IconMenuItem item = items.get(position);
                switch (position){
                    case 0:
                        openActivity(PersonalCenterActivity.class);
                        break;
                    case 1:
                        openActivity(MessageAndPrivacyActivity.class);
                        break;
                    case 3:
                        GlideCacheUtil glideCacheUtil = GlideCacheUtil.getInstance();
                        glideCacheUtil.clearImageAllCache(SettingActivity.this);
                        String cacheSize = glideCacheUtil.getCacheSize(SettingActivity.this);
                        item.setHint(cacheSize);
                        adapter.notifyDataSetChanged();
                        toastShort("清除完成");
                        break;
                    case 5:
                        DialogUtil.createTextDialog(SettingActivity.this, "提示", "确定要退出登录吗？", "",
                                "退出", new DialogUtil.OnClickListener() {
                                    @Override
                                    public void onClick(DialogPlus dialogPlus) {
                                        EventBus.getDefault().post(new LogoutEvent());
                                        dialogPlus.dismiss();
                                        finish();
                                    }
                                }, "取消").show();
                        break;
                }
            }
        });

    }
}
