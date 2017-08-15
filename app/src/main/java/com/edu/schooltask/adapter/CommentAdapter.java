package com.edu.schooltask.adapter;

import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.TaskComment;
import com.edu.schooltask.beans.UserInfoBase;
import com.edu.schooltask.utils.DateUtil;
import com.edu.schooltask.utils.GlideUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 夜夜通宵 on 2017/8/14.
 */

public class CommentAdapter extends BaseQuickAdapter<TaskComment, BaseViewHolder> {
    public CommentAdapter(@LayoutRes int layoutResId, @Nullable List<TaskComment> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TaskComment taskComment) {
        CircleImageView headView = helper.getView(R.id.ui_head);
        UserInfoBase commentUser = taskComment.getCommentUser();
        GlideUtil.setHead(headView.getContext(), commentUser.getUserId(),headView, false);
        helper.setText(R.id.ui_school, commentUser.getSchool());
        helper.setText(R.id.ui_name,commentUser.getName());
        helper.setText(R.id.ui_release_time,
                DateUtil.getLong(DateUtil.stringToCalendar(taskComment.getCommentTime())));
        switch (commentUser.getSex()){
            case -1:
                helper.setText(R.id.ui_sex,"");
                break;
            case 0:
                helper.setText(R.id.ui_sex,"♂");
                helper.setTextColor(R.id.ui_sex, Color.parseColor("#1B9DFF"));
                break;
            case 1:
                helper.setText(R.id.ui_sex,"♀");
                helper.setTextColor(R.id.ui_sex, Color.parseColor("#FF0000"));
                break;
        }
        int childCount = taskComment.getChildCount();
        if(taskComment.getParentId() != 0){ //子评论
            UserInfoBase toUser = taskComment.getToUser();
            if(toUser != null){
                helper.setText(R.id.tc_comment, "回复 " + toUser.getName()
                        +"："+taskComment.getComment());
            }
            else{
                helper.setText(R.id.tc_comment, taskComment.getComment());
            }
            helper.setVisible(R.id.tc_child_count, false);
        }
        else{   //顶层评论
            helper.setText(R.id.tc_comment, taskComment.getComment());
            if(childCount != 0){
                helper.setVisible(R.id.tc_child_count, true);
                helper.setText(R.id.tc_child_count, childCount + "条回复");
                helper.addOnClickListener(R.id.tc_child_count);
            }
            else{
                helper.setVisible(R.id.tc_child_count, false);
            }
        }
    }


}
