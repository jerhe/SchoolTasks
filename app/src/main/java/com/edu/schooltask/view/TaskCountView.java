package com.edu.schooltask.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.beans.TaskCount;

/**
 * Created by 夜夜通宵 on 2017/8/14.
 */

public class TaskCountView extends RelativeLayout {
    private TextView commentCountText;
    private TextView lookCountText;

    public TaskCountView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_task_count, this);

        commentCountText = (TextView) findViewById(R.id.tc_comment_count);
        lookCountText = (TextView) findViewById(R.id.tc_look_count);
    }

    public void setCommentCount(int count){
        commentCountText.setText("评论 " + count);
    }

    public void setLookCount(int count){
        lookCountText.setText("浏览 " + count);
    }

    public void setCount(TaskCount taskCount){
        setCommentCount(taskCount.getCommentCount());
        setLookCount(taskCount.getLookCount());
    }

}
