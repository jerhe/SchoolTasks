package com.edu.schooltask.activity;

import android.view.View;
import android.widget.Switch;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.MessageConfig;
import com.edu.schooltask.event.LogoutEvent;
import com.edu.schooltask.item.IconMenuItem;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.view.listener.ItemChildClickListener;
import com.edu.schooltask.view.listener.ItemClickListener;
import com.edu.schooltask.view.recyclerview.IconMenuRecyclerView;
import com.edu.schooltask.utils.DialogUtil;
import com.edu.schooltask.utils.GlideCacheUtil;
import com.edu.schooltask.utils.UserUtil;
import com.orhanobut.dialogplus.DialogPlus;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;

public class SettingActivity extends BaseActivity {
    @BindView(R.id.setting_imrv) IconMenuRecyclerView recyclerView;

    IconMenuItem vibrateItem;
    IconMenuItem ringItem;

    @Override
    public int getLayout() {
        return R.layout.activity_setting;
    }

    @Override
    public void init() {
        MessageConfig messageConfig = mDataCache.getMessageConfig();
        vibrateItem  = new IconMenuItem(IconMenuItem.SWITCH, 0, getString(R.string.vibrate));
        vibrateItem.setChecked(messageConfig.isVibrate());
        ringItem = new IconMenuItem(IconMenuItem.SWITCH, 0, getString(R.string.ring));
        ringItem.setChecked(messageConfig.isRing());
        String cacheSize = GlideCacheUtil.getInstance().getCacheSize(this);
        cacheSize = cacheSize.replace("Byte", "B");

        recyclerView.add(new IconMenuItem());
        recyclerView.add(new IconMenuItem(getString(R.string.message_notify)));
        recyclerView.add(vibrateItem);
        recyclerView.add(ringItem);
        recyclerView.add(new IconMenuItem());
        recyclerView.add(new IconMenuItem(IconMenuItem.HORIZONTAL, 0, getString(R.string.clearCache), cacheSize));
        recyclerView.add(new IconMenuItem());
        if(UserUtil.hasLogin())
            recyclerView.add(new IconMenuItem(IconMenuItem.HORIZONTAL, 0, getString(R.string.logout)));

        recyclerView.setItemClickListener(new ItemClickListener<IconMenuItem>() {
            @Override
            public void onItemClick(int position, IconMenuItem item) {
                switch (position){
                    case 5:
                        GlideCacheUtil glideCacheUtil = GlideCacheUtil.getInstance();
                        glideCacheUtil.clearImageAllCache(SettingActivity.this);
                        item.setHint(getString(R.string.clearSuccess));
                        recyclerView.notifyDataSetChanged();
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
        recyclerView.setItemChildClickListener(new ItemChildClickListener<IconMenuItem>() {
            @Override
            public void onItemChildClick(View view, int position, IconMenuItem item) {
                Switch mSwitch = (Switch) view;
                item.setChecked(mSwitch.isChecked());
                MessageConfig messageConfig = new MessageConfig(vibrateItem.isChecked(), ringItem.isChecked());
                mDataCache.saveMessageConfig(messageConfig);
            }
        });
    }
}
