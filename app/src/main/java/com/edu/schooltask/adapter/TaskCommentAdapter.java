package com.edu.schooltask.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.comment.TaskComment;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.other.CustomClickableSpan;
import com.edu.schooltask.ui.view.TaskCommentView;
import com.edu.schooltask.ui.view.useritem.UserItemCommentView;
import com.edu.schooltask.utils.StringUtil;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/8/14.
 */

public class TaskCommentAdapter extends BaseQuickAdapter<TaskComment, BaseViewHolder> {
    public TaskCommentAdapter(@LayoutRes int layoutResId, @Nullable List<TaskComment> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TaskComment taskComment) {
        TaskCommentView taskCommentView = helper.getView(R.id.tc_tcv);
        taskCommentView.setAll(helper, taskComment, false);
    }


}
