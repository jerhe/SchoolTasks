package com.edu.schooltask.view.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.CommentAdapter;
import com.edu.schooltask.beans.Comment;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/8/14.
 */

public class CommentRecyclerView extends BasePageRecyclerView<Comment> {

    public CommentRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected BaseQuickAdapter initAdapter(List<Comment> list) {
        return new CommentAdapter(list);
    }

    @Override
    protected void init() {
        setNestedScrollingEnabled(false);
        setEmptyView(R.layout.empty_comment);
    }
}
