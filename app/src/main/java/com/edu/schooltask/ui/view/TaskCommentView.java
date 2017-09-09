package com.edu.schooltask.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.beans.comment.TaskComment;
import com.edu.schooltask.ui.view.useritem.UserItemCommentView;
import com.edu.schooltask.utils.StringUtil;

/**
 * Created by 夜夜通宵 on 2017/9/9.
 */

public class TaskCommentView extends LinearLayout {

    UserItemCommentView userView;
    TextView commentText;
    TextView childCountText;

    public TaskCommentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_task_comment, this);
        userView = (UserItemCommentView) findViewById(R.id.tc_uicv);
        commentText = (TextView) findViewById(R.id.tc_comment);
        commentText.setMovementMethod(LinkMovementMethod.getInstance());   //设置可点击
        //commentText.setFocusable(false);   //点击冲突
        childCountText = (TextView) findViewById(R.id.tc_child_count);
    }

    public void setAll(BaseViewHolder helper, TaskComment taskComment, boolean isChildView){
        //用户信息
        UserInfo userInfo = taskComment.getUserInfo();
        userView.setAll(userInfo, taskComment.getCreateTime(), userInfo.getSchool());
        //评论信息
        if(taskComment.getParentId() != 0 || isChildView){ //子评论
            String toName = taskComment.getToName();
            if(StringUtil.isEmpty(toName)){
                commentText.setText(StringUtil.atString(getContext(), taskComment.getComment()));
            }
            else{
                String replyText = "回复@" + toName +" ："+taskComment.getComment();
                commentText.setText(StringUtil.atString(getContext(), replyText));
            }
            childCountText.setVisibility(GONE);
        }
        else{   //顶层评论
            commentText.setText(StringUtil.atString(getContext(), taskComment.getComment()));
            if(taskComment.getReplyCount() != 0){
                childCountText.setVisibility(VISIBLE);
                childCountText.setText(taskComment.getReplyCount() + "条回复");
                if(helper != null) helper.addOnClickListener(R.id.tc_child_count);
            }
            else{
                childCountText.setVisibility(GONE);
            }
        }
    }
}
