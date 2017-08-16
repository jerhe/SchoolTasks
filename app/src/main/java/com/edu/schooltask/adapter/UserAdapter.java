package com.edu.schooltask.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.UserInfoBase;
import com.edu.schooltask.utils.GlideUtil;
import com.edu.schooltask.view.UserItemView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 夜夜通宵 on 2017/6/3.
 */

public class UserAdapter extends BaseQuickAdapter<UserInfoBase, BaseViewHolder> {
    public UserAdapter(List<UserInfoBase> data) {
        super(R.layout.item_user_item_view, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserInfoBase item) {
        UserItemView userItemView = helper.getView(R.id.uiv_uiv);
        userItemView.setAll(item);
    }
}
