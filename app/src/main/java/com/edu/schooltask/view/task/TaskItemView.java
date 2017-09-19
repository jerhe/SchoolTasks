package com.edu.schooltask.view.task;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.adapter.NineAdapter;
import com.edu.schooltask.beans.task.TaskItem;
import com.edu.schooltask.view.useritem.UserItemTaskView;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;

import java.util.ArrayList;
import java.util.List;

import server.api.SchoolTask;

/**
 * Created by 夜夜通宵 on 2017/8/14.
 */

public class TaskItemView extends LinearLayout{
    private UserItemTaskView userItemTaskView;
    private TextView costText;
    private TextView contentText;
    private NineGridView nineGridView;

    public TaskItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_task_item, this);
        userItemTaskView = (UserItemTaskView) findViewById(R.id.task_uirv);
        costText = (TextView) findViewById(R.id.task_cost);
        contentText = (TextView) findViewById(R.id.task_content);
        nineGridView = (NineGridView) findViewById(R.id.task_image);
    }

    //设置所有信息: isInfo:是否是任务信息页(WaitAcceptOrderActivity)
    public void setAll(final TaskItem taskItem, boolean isInfo){
        String orderId = taskItem.getOrderId();
        userItemTaskView.setAll(taskItem.getUserInfo(),taskItem.getReleaseTime(),
                taskItem.getSchool(), taskItem.getDescription(), isInfo);
        costText.setText("¥" + taskItem.getReward()); //金额
        contentText.setText(taskItem.getContent()); //内容
        //图片
        int imageNum = taskItem.getImageNum();
        if(imageNum == 0){
            nineGridView.setVisibility(GONE);
        }
        else{
            nineGridView.setVisibility(VISIBLE);
            List<ImageInfo> imageInfos = new ArrayList<>();
            for(int i = 0; i < taskItem.getImageNum(); i++){
                ImageInfo imageInfo = new ImageInfo();
                String url = SchoolTask.TASK_IMAGE + orderId + "/" + i + ".png";
                imageInfo.setThumbnailUrl(url);
                imageInfo.setBigImageUrl(url);
                imageInfos.add(imageInfo);
            }
            nineGridView.setAdapter(new NineAdapter(getContext(), imageInfos));
        }
        //点击跳转事件
        if (!isInfo) setLengthLimit();
    }

    public void setLengthLimit(){
        contentText.setEllipsize(TextUtils.TruncateAt.END);
        contentText.setMaxLines(5);
    }

    public String getContent(){
        return contentText.getText().toString();
    }
}
