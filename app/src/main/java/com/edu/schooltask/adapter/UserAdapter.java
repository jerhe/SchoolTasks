package com.edu.schooltask.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.ui.view.useritem.UserItemView;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/6/3.
 */

public class UserAdapter extends BaseQuickAdapter<UserInfo, BaseViewHolder> {
    public UserAdapter(List<UserInfo> data) {
        super(R.layout.item_user_item_view, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserInfo item) {
        UserItemView userItemView = helper.getView(R.id.uiv_uiv);
        userItemView.setAll(item);
    }
}
