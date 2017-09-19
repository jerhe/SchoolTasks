package com.edu.schooltask.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.Comment;
import com.edu.schooltask.view.CommentView;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/8/14.
 */

public class CommentAdapter extends BaseQuickAdapter<Comment, BaseViewHolder> {
    public CommentAdapter(@Nullable List<Comment> data) {
        super(R.layout.item_comment, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Comment comment) {
        CommentView commentView = helper.getView(R.id.comment_cv);
        commentView.setAll(helper, comment, false);
    }


}
