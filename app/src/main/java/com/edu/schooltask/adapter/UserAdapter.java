package com.edu.schooltask.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.UserBaseInfo;
import com.edu.schooltask.utils.GlideUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 夜夜通宵 on 2017/6/3.
 */

public class UserAdapter extends BaseQuickAdapter<UserBaseInfo, BaseViewHolder> {
    public UserAdapter(int layoutResId, List<UserBaseInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserBaseInfo item) {
        CircleImageView headView = helper.getView(R.id.user_head);
        GlideUtil.setHead(headView.getContext(), item.getUserId(), headView, false);
        helper.setText(R.id.user_name, item.getName());
        helper.setText(R.id.user_sign, item.getSign());
    }
}
