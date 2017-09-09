package com.edu.schooltask.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.comment.TaskComment;
import com.edu.schooltask.ui.view.recyclerview.CommentRecyclerView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/8/15.
 */

public class CommentReplyView extends LinearLayout {
    private Toolbar toolbar;
    private TextView titleText;
    private PullRefreshLayout refreshLayout;
    private CommentRecyclerView commentRecyclerView;

    View oppositeView;
    TaskCommentView parentCommentView;

    HashMap<String, String> userMap = new HashMap<>();  //存储用户id和昵称

    public CommentReplyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_comment_reply, this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        titleText = (TextView) findViewById(R.id.toolbar_title);
        refreshLayout = (PullRefreshLayout) findViewById(R.id.cp_prl);
        commentRecyclerView = (CommentRecyclerView) findViewById(R.id.cp_rv);
        parentCommentView = new TaskCommentView(getContext(), null);
        commentRecyclerView.addHeader(parentCommentView);
        commentRecyclerView.addHeader(LayoutInflater.from(getContext()).inflate(R.layout.divide, null));

        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibility(GONE);
                if(oppositeView != null) oppositeView.setVisibility(VISIBLE);
            }
        });
        titleText.setText("查看回复");
        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                commentRecyclerView.refresh();
            }
        });


    }

    public void setRefresh(boolean refreshing){
        refreshLayout.setRefreshing(refreshing);
    }

    public void setOppositeView(View view){
        oppositeView = view;
    }

    //以下皆为调用commentRecyclerView接口

    public void setGetDataPageListener(CommentRecyclerView.OnGetPageDataListener l){
        commentRecyclerView.setOnGetPageDataListener(l);
    }

    public void setOnItemClickListener(BaseQuickAdapter.OnItemClickListener l){
        commentRecyclerView.setOnItemClickListener(l);
    }

    public void refresh(){
        commentRecyclerView.refresh();
    }

    public void addComments(List<TaskComment> comments){
        commentRecyclerView.add(comments);
    }

    public List<TaskComment> getComments(){
        return commentRecyclerView.get();
    }

    public void addUserMap(String userId, String name){
        userMap.put(userId, name);
    }

    public String getUserName(String userId){
        return userMap.get(userId);
    }

    public void clear(){
        commentRecyclerView.clear(true);
        userMap.clear();
    }

    public void setParentComment(TaskComment taskComment){
        parentCommentView.setAll(null, taskComment, true);
    }
}
