package com.edu.schooltask.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.view.useritem.UserItemView;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/8/26.
 */

public class UserAdapter extends BaseQuickAdapter<UserInfo, BaseViewHolder> {
    public UserAdapter(@Nullable List<UserInfo> data) {
        super(R.layout.item_user, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserInfo item) {
        UserItemView userItemView = helper.getView(R.id.user);
        userItemView.setAll(item);
    }
}
