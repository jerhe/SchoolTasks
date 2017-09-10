package com.edu.schooltask.ui.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.ui.activity.ImageActivity;
import com.edu.schooltask.ui.activity.WaitAcceptOrderActivity;
import com.edu.schooltask.item.ImageItem;
import com.edu.schooltask.beans.task.TaskItem;
import com.edu.schooltask.ui.view.recyclerview.ImageRecyclerView;
import com.edu.schooltask.ui.view.useritem.UserItemTaskView;

import java.io.Serializable;
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
    private ImageRecyclerView imageRecyclerView;

    public TaskItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_task_item, this);

        userItemTaskView = (UserItemTaskView) findViewById(R.id.task_uirv);
        costText = (TextView) findViewById(R.id.task_cost);
        contentText = (TextView) findViewById(R.id.task_content);
        imageRecyclerView = (ImageRecyclerView) findViewById(R.id.task_irv);
    }

    //设置所有信息: isInfo:是否是任务信息页(WaitAcceptOrderActivity)
    public void setAll(TaskItem taskItem, boolean isInfo){
        final String orderId = taskItem.getOrderId();
        userItemTaskView.setAll(taskItem.getUserInfo(),taskItem.getReleaseTime(),
                taskItem.getSchool(), taskItem.getDescription(), isInfo);
        costText.setText("¥" + taskItem.getReward()); //金额
        contentText.setText(taskItem.getContent()); //内容
        //图片
        int imageNum = taskItem.getImageNum();
        String imageUrl = SchoolTask.TASK_IMAGE + orderId + "/";
        final List<ImageItem> imageItems = new ArrayList<>();
        for(int i = 0; i < imageNum; i++){
            imageItems.add(new ImageItem(imageNum == 1 ? 3 : 2, imageUrl + i + ".png"));
        }
        int column = imageNum < 3 ? (imageNum == 0 ? 1 : imageNum) : 3;
        imageRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), column));
        imageRecyclerView.clear(false);
        imageRecyclerView.add(imageItems);
        //点击跳转事件
        if (isInfo){
            //浏览图片
            imageRecyclerView.setImageClickListener(new ImageRecyclerView.ImageClickListener() {
                @Override
                public void onImageClick(int position, ImageItem imageItem) {
                    Intent intent = new Intent(getContext(), ImageActivity.class);
                    intent.putExtra("editable", false);
                    intent.putExtra("index", position);
                    intent.putExtra("images", (Serializable) imageRecyclerView.get());
                    getContext().startActivity(intent);
                }
            });
        }
        else{
            imageRecyclerView.setImageClickListener(new ImageRecyclerView.ImageClickListener() {
                @Override
                public void onImageClick(int position, ImageItem imageItem) {
                    openWaitAcceptOrderActivity(orderId);
                }
            });
            imageRecyclerView.setSpaceClickListener(new ImageRecyclerView.SpaceClickListener() {
                @Override
                public void OnSpaceClick() {
                    openWaitAcceptOrderActivity(orderId);
                }
            });
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    openWaitAcceptOrderActivity(orderId);
                }
            });
        }
    }

    public void setLengthLimit(){
        contentText.setEllipsize(TextUtils.TruncateAt.END);
        contentText.setMaxLines(5);
    }

    private void openWaitAcceptOrderActivity(String orderId){
        Intent intent = new Intent(getContext(), WaitAcceptOrderActivity.class);
        intent.putExtra("orderId", orderId);
        getContext().startActivity(intent);
    }

    public String getContent(){
        return contentText.getText().toString();
    }
}
