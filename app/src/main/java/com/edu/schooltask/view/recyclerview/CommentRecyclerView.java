package com.edu.schooltask.view.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.CommentAdapter;
import com.edu.schooltask.beans.TaskComment;
import com.edu.schooltask.utils.DensityUtil;
import com.edu.schooltask.view.CommentInputBoard;
import com.edu.schooltask.view.CommentReplyView;
import com.edu.schooltask.view.CustomLoadMoreView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/8/14.
 */

public class CommentRecyclerView extends BasePageRecyclerView<TaskComment> {

    //子评论
    CommentReplyView commentReplyView;
    long parentId = 0;  //回复的顶层评论id
    String toUserId = "";    //回复的用户id

    CommentInputBoard inputBoard;
    View oppositeView;

    public CommentRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected BaseQuickAdapter initAdapter(List<TaskComment> list) {
        return new CommentAdapter(R.layout.item_task_comment, list);
    }

    @Override
    public void refresh() {
        super.refresh();
        if(commentReplyView != null)
            if(commentReplyView.isShown())
                commentReplyView.refresh();
    }

    //////////////////////////////子评论/////////////////////////////////////

    //初始化子评论
    public void initChild(final CommentReplyView childView, OnGetPageDataListener listener,
                          final CommentInputBoard inputBoard){
        this.inputBoard = inputBoard;
        commentReplyView = childView;
        commentReplyView.setGetDataPageListener(listener);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(inputBoard != null) {
                    inputBoard.setHint(list.get(position).getCommentUser().getName());
                    inputBoard.show();
                }
                setParentId(list.get(position).getId());
            }
        });
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                TaskComment taskComment = list.get(position);
                setParentId(taskComment.getId());
                setToUserId(taskComment.getUserId());
                childView.refresh();
                showChild();
                if(inputBoard != null) inputBoard.setHint(taskComment.getCommentUser().getName());
            }
        });
        commentReplyView.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(inputBoard != null){
                    inputBoard.setHint(commentReplyView.getComments().get(position).getCommentUser().getName());
                    inputBoard.show();
                }
            }
        });
    }

    public void showChild(){
        if(oppositeView != null) oppositeView.setVisibility(INVISIBLE);
        commentReplyView.setVisibility(VISIBLE);
    }

    public void hideChild(){
        if(oppositeView != null) oppositeView.setVisibility(VISIBLE);
        if(inputBoard != null) inputBoard.clearHint();
        parentId = 0;
        toUserId = "";
        commentReplyView.setVisibility(GONE);
    }

    public void setOppositeView(View view){
        oppositeView = view;
        if(commentReplyView != null) commentReplyView.setOppositeView(view);
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

}
