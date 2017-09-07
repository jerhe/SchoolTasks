package com.edu.schooltask.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Switch;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.IconMenuAdapter;
import com.edu.schooltask.beans.MessageConfig;
import com.edu.schooltask.ui.base.BaseActivity;
import com.edu.schooltask.event.LogoutEvent;
import com.edu.schooltask.item.IconMenuItem;
import com.edu.schooltask.utils.DialogUtil;
import com.edu.schooltask.utils.GlideCacheUtil;
import com.edu.schooltask.utils.UserUtil;
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

    IconMenuItem vibrateItem;
    IconMenuItem ringItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        adapter = new IconMenuAdapter(items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        MessageConfig messageConfig = mDataCache.getMessageConfig();
        vibrateItem  = new IconMenuItem(IconMenuItem.SWITCH, 0, getString(R.string.vibrate));
        vibrateItem.setChecked(messageConfig.isVibrate());
        ringItem = new IconMenuItem(IconMenuItem.SWITCH, 0, getString(R.string.ring));
        ringItem.setChecked(messageConfig.isRing());
        String cacheSize = GlideCacheUtil.getInstance().getCacheSize(this);
        cacheSize = cacheSize.replace("Byte", "B");

        items.add(new IconMenuItem());
        items.add(new IconMenuItem(getString(R.string.message_notify)));
        items.add(vibrateItem);
        items.add(ringItem);
        items.add(new IconMenuItem());
        items.add(new IconMenuItem(IconMenuItem.HORIZONTAL, 0, getString(R.string.clearCache), cacheSize));
        items.add(new IconMenuItem());
        if(UserUtil.hasLogin())
            items.add(new IconMenuItem(IconMenuItem.HORIZONTAL, 0, getString(R.string.logout)));

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                IconMenuItem item = items.get(position);
                switch (position){
                    case 5:
                        GlideCacheUtil glideCacheUtil = GlideCacheUtil.getInstance();
                        glideCacheUtil.clearImageAllCache(SettingActivity.this);
                        item.setHint(getString(R.string.clearSuccess));
                        adapter.notifyDataSetChanged();
                        break;
                    case 7:
                        DialogUtil.createTextDialog(SettingActivity.this, getString(R.string.tip),
                                getString(R.string.logout_tip), "", new DialogUtil.OnClickListener() {
                                    @Override
                                    public void onClick(DialogPlus dialogPlus) {
                                        EventBus.getDefault().post(new LogoutEvent());
                                        dialogPlus.dismiss();
                                        finish();
                                    }
                                }).show();
                        break;
                }
            }
        });

        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                IconMenuItem item = items.get(position);
                Switch mSwitch = (Switch) view;
                item.setChecked(mSwitch.isChecked());
                MessageConfig messageConfig = new MessageConfig(vibrateItem.isChecked(), ringItem.isChecked());
                mDataCache.saveMessageConfig(messageConfig);
            }
        });
    }
}
