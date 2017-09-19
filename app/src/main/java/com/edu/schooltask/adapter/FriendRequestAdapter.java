package com.edu.schooltask.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.FriendRequest;
import com.edu.schooltask.view.useritem.UserItemView;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/8/26.
 */

public class FriendRequestAdapter extends BaseQuickAdapter<FriendRequest, BaseViewHolder> {

    public FriendRequestAdapter(@Nullable List<FriendRequest> data) {
        super(R.layout.item_friend_request, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FriendRequest item) {
        int x = item.getState() % 2; //偶主动奇被动
        String name = item.getUserInfo().getName();
        helper.setText(R.id.fr_message, x == 0 ? "请求添加为好友" : "添加你为好友");
        UserItemView userItemView = helper.getView(R.id.fr_user);
        userItemView.setAll(item.getUserInfo());
        helper.addOnClickListener(R.id.fr_user);
        helper.addOnClickListener(R.id.fr_agree);
        helper.addOnClickListener(R.id.fr_reject);
        switch (item.getState()){
            case 0:
            case 1:
                helper.setVisible(R.id.fr_agree, false);
                helper.setVisible(R.id.fr_reject, false);
                helper.setVisible(R.id.fr_result, true);
                helper.setText(R.id.fr_result, "已同意");
                break;
            case 2:
                helper.setVisible(R.id.fr_agree, false);
                helper.setVisible(R.id.fr_reject, false);
                helper.setVisible(R.id.fr_result, true);
                helper.setText(R.id.fr_result, "等待中");
                break;
            case 3:
                helper.setVisible(R.id.fr_agree, true);
                helper.setVisible(R.id.fr_reject, true);
                helper.setVisible(R.id.fr_result, false);
                break;
            case 4:
            case 5:
                helper.setVisible(R.id.fr_agree, false);
                helper.setVisible(R.id.fr_reject, false);
                helper.setVisible(R.id.fr_result, true);
                helper.setText(R.id.fr_result, "已拒绝");
                break;
            case 6:
                helper.setVisible(R.id.fr_agree, false);
                helper.setVisible(R.id.fr_reject, false);
                helper.setVisible(R.id.fr_result, true);
                helper.setText(R.id.fr_result, "已删除");
                break;
        }
    }
}
