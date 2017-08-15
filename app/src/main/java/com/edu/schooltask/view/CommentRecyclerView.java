package com.edu.schooltask.view;

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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/8/14.
 */

public class CommentRecyclerView extends RecyclerView {

    //顶层评论
    CommentAdapter adapter;
    List<TaskComment> comments = new ArrayList<>();
    GetCommentListener listener;
    CommentClickListener commentClickListener;
    int page = 0;

    //子评论
    CommentReplyView commentReplyView;
    long parentId = 0;  //回复的顶层评论id
    String toUserId = "";    //回复的用户id

    CommentInputBoard inputBoard;
    View oppositeView;

    public CommentRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.WHITE);
        this.setLayoutManager(new LinearLayoutManager(context));
        adapter = new CommentAdapter(R.layout.item_task_comment, comments);
        adapter.openLoadAnimation();
        this.setAdapter(adapter);
    }

    public void addData(List<TaskComment> taskComments){
        if(page == 0) comments.clear();
        adapter.addData(taskComments);
        page ++;
        adapter.loadMoreComplete();
        if(taskComments.size() == 0) adapter.loadMoreEnd();
        adapter.notifyDataSetChanged();
    }

    public List<TaskComment> getData(){
        return comments;
    }

    public void clear() {
        page = 0;
        comments.clear();
        adapter.notifyDataSetChanged();
    }

    //获取评论
    public void getComment(){
        if(listener != null) listener.getComment(page);
    }

    //刷新评论
    public void refresh(){
        clear();
        getComment();
    }

    public void addHeader(View view){
        adapter.addHeaderView(view);
    }

    public void setGetCommentListener(GetCommentListener listener){
        this.listener = listener;
        adapter.setEnableLoadMore(true);
        adapter.setLoadMoreView(new CustomLoadMoreView());
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                getComment();
            }
        }, this);
        listener.getComment(page);
    }

    public interface GetCommentListener {
        void getComment(int page);
    }


    //////////////////////////////子评论/////////////////////////////////////

    //初始化子评论
    public void initChild(final CommentReplyView childView, final GetCommentListener listener,
                          final CommentInputBoard inputBoard, CommentClickListener l){
        this.inputBoard = inputBoard;
        setCommentClickListener(l);
        commentReplyView = childView;
        commentReplyView.setGetCommentListener(listener);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                inputBoard.setHint(comments.get(position).getCommentUser().getName());
                setParentId(comments.get(position).getId());
                if(commentClickListener != null) commentClickListener.onCommentClick();
            }
        });
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                TaskComment taskComment = comments.get(position);
                setParentId(taskComment.getId());
                setToUserId(taskComment.getUserId());
                childView.refresh();
                showChild();
                inputBoard.setHint(taskComment.getCommentUser().getName());
            }
        });
        childView.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                inputBoard.setHint(childView.getData().get(position).getCommentUser().getName());
                inputBoard.show();
            }
        });
    }


    public void setOnItemClickListener(BaseQuickAdapter.OnItemClickListener listener) {
        adapter.setOnItemClickListener(listener);
    }

    public void showChild(){
        if(oppositeView != null) oppositeView.setVisibility(INVISIBLE);
        commentReplyView.setVisibility(VISIBLE);
    }

    public void hideChild(){
        if(oppositeView != null) oppositeView.setVisibility(VISIBLE);
        inputBoard.clearHint();
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

    /////////////////////////////////////////////////////////////////////////
    public void setCommentClickListener(CommentClickListener l){
        this.commentClickListener = l;
    }

    public interface CommentClickListener{
        void onCommentClick();
    }
}
