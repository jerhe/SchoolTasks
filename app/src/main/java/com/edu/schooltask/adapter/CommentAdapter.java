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
import com.edu.schooltask.ui.view.useritem.UserItemCommentView;
import com.edu.schooltask.utils.StringUtil;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/8/14.
 */

public class CommentAdapter extends BaseQuickAdapter<TaskComment, BaseViewHolder> {
    public CommentAdapter(@LayoutRes int layoutResId, @Nullable List<TaskComment> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TaskComment taskComment) {
        //用户信息
        UserItemCommentView userView = helper.getView(R.id.tc_uicv);
        UserInfo userInfo = taskComment.getUserInfo();
        userView.setAll(userInfo.getUserId(), userInfo.getName(), userInfo.getSex(),
                taskComment.getCreateTime(), userInfo.getSchool());
        //评论信息
        TextView textView = helper.getView(R.id.tc_comment);
        textView.setMovementMethod(LinkMovementMethod.getInstance());   //设置可点击
        textView.setFocusable(false);   //点击冲突
        if(taskComment.getParentId() != 0){ //子评论
            String toName = taskComment.getToName();
            if(StringUtil.isEmpty(toName)){
                helper.setText(R.id.tc_comment,
                        StringUtil.atString(helper.itemView.getContext(), taskComment.getComment()));
            }
            else{
                String replyText = "回复@" + toName +" ："+taskComment.getComment();
                helper.setText(R.id.tc_comment, StringUtil.atString(helper.itemView.getContext(), replyText));
            }
            helper.setVisible(R.id.tc_child_count, false);
        }
        else{   //顶层评论
            helper.setText(R.id.tc_comment, StringUtil.atString(helper.itemView.getContext(), taskComment.getComment()));
            if(taskComment.getReplyCount() != 0){
                helper.setVisible(R.id.tc_child_count, true);
                helper.setText(R.id.tc_child_count, taskComment.getReplyCount() + "条回复");
                helper.addOnClickListener(R.id.tc_child_count);
            }
            else{
                helper.setVisible(R.id.tc_child_count, false);
            }
        }
    }


}
