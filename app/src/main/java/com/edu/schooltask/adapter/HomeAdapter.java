package com.edu.schooltask.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.activity.ImageActivity;
import com.edu.schooltask.activity.WaitAcceptOrderActivity;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.beans.UserInfoBase;
import com.edu.schooltask.data.DataCache;
import com.edu.schooltask.item.HomeItem;
import com.edu.schooltask.item.ImageItem;
import com.edu.schooltask.item.TaskItem;
import com.edu.schooltask.beans.TaskComment;
import com.edu.schooltask.item.TaskCountItem;
import com.edu.schooltask.utils.DateUtil;
import com.edu.schooltask.utils.GlideUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import server.api.SchoolTask;

/**
 * Created by 夜夜通宵 on 2017/5/11.
 */

public class HomeAdapter extends BaseMultiItemQuickAdapter<HomeItem, BaseViewHolder> {

    private DataCache mDataCache;
    private BaseActivity activity;

    public HomeAdapter(List<HomeItem> data, DataCache mDataCache, BaseActivity activity) {
        super(data);
        this.mDataCache = mDataCache;
        this.activity = activity;
        addItemType(HomeItem.LOAD_TIP, R.layout.rv_load_tip);
        addItemType(HomeItem.COMMENT, R.layout.item_task_comment);
    }

    @Override
    protected void convert(final BaseViewHolder helper, HomeItem item) {
        switch (item.getItemType()){
            case 0:
                helper.setText(R.id.lf_text,item.getTip());
                break;
            case 1:
                setCommentItem(helper, item);
                break;
        }
    }

    private void setCommentItem(BaseViewHolder helper, HomeItem item){
        TaskComment taskComment = item.getTaskComment();
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
        helper.addOnClickListener(R.id.ui_layout);
    }
}
