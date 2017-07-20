package com.edu.schooltask.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.IconMenuAdapter;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.beans.UserConfig;
import com.edu.schooltask.item.IconMenuItem;
import com.edu.schooltask.utils.DialogUtil;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import server.api.SchoolTask;

public class MessageAndPrivacyActivity extends BaseActivity {
    List<String> messageArray = new ArrayList<>();
    RecyclerView recyclerView;
    List<IconMenuItem> items = new ArrayList<>();
    IconMenuAdapter adapter;

    IconMenuItem messageItem;
    IconMenuItem fansItem;
    IconMenuItem commentItem;
    IconMenuItem privateMessageItem;

    int message;

    BaseAdapter messageSelectAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return messageArray.size();
        }

        @Override
        public Object getItem(int position) {
            return messageArray.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(MessageAndPrivacyActivity.this)
                    .inflate(R.layout.item_text,null);
            TextView textView = (TextView) view.findViewById(R.id.text);
            textView.setText(messageArray.get(position));
            return view;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_and_privacy);
        recyclerView = getView(R.id.map_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new IconMenuAdapter(items);
        adapter.openLoadAnimation();
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final BaseQuickAdapter adapter, View view, int position) {
                switch (position){
                    case 0:
                        DialogUtil.createListDialog(MessageAndPrivacyActivity.this, messageSelectAdapter,
                                new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                                        message = position;
                                        messageItem.setValue(messageArray.get(position));
                                        adapter.notifyDataSetChanged();
                                        dialog.dismiss();
                                        updateConfig();
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
                updateConfig();
            }
        });

        messageArray.add("所有人");
        messageArray.add("关注的人");

        UserConfig userConfig = mDataCache.getData("userConfig");
        message = userConfig.getMessage();
        messageItem = new IconMenuItem(IconMenuItem.VALUE, 0, "接收私信");
        messageItem.setValue(messageArray.get(message));
        fansItem = new IconMenuItem(IconMenuItem.SWITCH, 0, "新粉丝");
        fansItem.setChecked(userConfig.isFans());
        commentItem = new IconMenuItem(IconMenuItem.SWITCH, 0, "新回复");
        commentItem.setChecked(userConfig.isComment());
        privateMessageItem = new IconMenuItem(IconMenuItem.SWITCH, 0, "新私信");
        privateMessageItem.setChecked(userConfig.isPrivateMessage());

        items.add(messageItem);
        items.add(new IconMenuItem());
        items.add(new IconMenuItem("消息提醒"));
        items.add(fansItem);
        items.add(commentItem);
        items.add(privateMessageItem);
        items.add(new IconMenuItem());
        items.add(new IconMenuItem(IconMenuItem.HORIZONTAL, 0, "清空聊天记录"));
        items.add(new IconMenuItem());
        items.add(new IconMenuItem(IconMenuItem.HORIZONTAL, 0, "黑名单"));
    }

    private void updateConfig(){
        UserConfig userConfig = new UserConfig(message, fansItem.isChecked(), commentItem.isChecked(),
                privateMessageItem.isChecked());
        mDataCache.saveData("userConfig", userConfig);
        SchoolTask.updateUserConfig(userConfig);
    }
}
