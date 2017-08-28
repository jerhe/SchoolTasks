package com.edu.schooltask.ui.view.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.adapter.FriendRequestAdapter;
import com.edu.schooltask.beans.FriendRequest;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/8/26.
 */

public class FriendRequestRecyclerView extends BaseRecyclerView<FriendRequest> {
    public FriendRequestRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected BaseQuickAdapter initAdapter(List<FriendRequest> list) {
        return new FriendRequestAdapter(list);
    }
}
