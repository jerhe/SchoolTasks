package com.edu.schooltask.ui.view.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.TaskCommentAdapter;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.beans.comment.TaskComment;
import com.edu.schooltask.ui.view.CommentInputBoard;
import com.edu.schooltask.ui.view.CommentReplyView;

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
        return new TaskCommentAdapter(R.layout.item_task_comment, list);
    }

    @Override
    public void refresh() {
        super.refresh();
        if(commentReplyView != null)
            if(commentReplyView.isShown())
                commentReplyView.refresh();
    }

    public void cancelInputBoard(){
        this.inputBoard = null;
    }

    //////////////////////////////子评论/////////////////////////////////////

    //初始化子评论
    public void initChild(final CommentReplyView childView, OnGetPageDataListener listener,
                          CommentInputBoard inputBoard){
        this.inputBoard = inputBoard;
        commentReplyView = childView;
        commentReplyView.setGetDataPageListener(listener);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(CommentRecyclerView.this.inputBoard != null) {
                    CommentRecyclerView.this.inputBoard.setHint(list.get(position).getUserInfo().getName());
                    CommentRecyclerView.this.inputBoard.show();
                }
                setParentId(list.get(position).getId());
            }
        });
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                TaskComment taskComment = list.get(position);
                setParentId(taskComment.getId());
                childView.refresh();
                showChild(taskComment);
            }
        });
        commentReplyView.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(CommentRecyclerView.this.inputBoard != null){
                    UserInfo userInfo = commentReplyView.getComments().get(position).getUserInfo();
                    setToUserId(userInfo.getUserId());
                    CommentRecyclerView.this.inputBoard.setHint(userInfo.getName());
                    CommentRecyclerView.this.inputBoard.show();
                }
            }
        });
    }

    public void showChild(TaskComment taskComment){
        if(oppositeView != null) oppositeView.setVisibility(INVISIBLE);
        commentReplyView.setParentComment(taskComment);
        commentReplyView.setVisibility(VISIBLE);
    }

    public void hideChild(){
        if(oppositeView != null) oppositeView.setVisibility(VISIBLE);
        if(inputBoard != null) inputBoard.clearHint();
        parentId = 0;
        toUserId = "";
        commentReplyView.setVisibility(GONE);
        commentReplyView.clear();
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
