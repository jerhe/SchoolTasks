package com.edu.schooltask.ui.view.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.adapter.FriendAdapter;
import com.edu.schooltask.beans.UserInfo;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/8/26.
 */

public class FriendRecyclerView extends BaseRecyclerView<UserInfo> {
    public FriendRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected BaseQuickAdapter initAdapter(List<UserInfo> list) {
        return new FriendAdapter(list);
    }
}
