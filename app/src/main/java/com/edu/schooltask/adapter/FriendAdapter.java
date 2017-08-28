package com.edu.schooltask.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.ui.view.useritem.UserItemView;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/8/26.
 */

public class FriendAdapter extends BaseQuickAdapter<UserInfo, BaseViewHolder> {
    public FriendAdapter(@Nullable List<UserInfo> data) {
        super(R.layout.item_friend, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserInfo item) {
        UserItemView userItemView = helper.getView(R.id.friend_user);
        userItemView.setAll(item);
    }
}
