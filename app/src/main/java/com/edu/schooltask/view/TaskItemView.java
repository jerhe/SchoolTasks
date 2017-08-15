package com.edu.schooltask.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.activity.ImageActivity;
import com.edu.schooltask.activity.WaitAcceptOrderActivity;
import com.edu.schooltask.beans.UserInfoBase;
import com.edu.schooltask.item.ImageItem;
import com.edu.schooltask.item.TaskItem;
import com.edu.schooltask.utils.GlideUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import server.api.SchoolTask;

/**
 * Created by 夜夜通宵 on 2017/8/14.
 */

public class TaskItemView extends LinearLayout{
    private UserItemReleaseView userItemReleaseView;
    private TextView costText;
    private TextView contentText;
    private ImageRecyclerView imageRecyclerView;

    public TaskItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_task, this);

        userItemReleaseView = (UserItemReleaseView) findViewById(R.id.task_uirv);
        costText = (TextView) findViewById(R.id.task_cost);
        contentText = (TextView) findViewById(R.id.task_content);
        imageRecyclerView = (ImageRecyclerView) findViewById(R.id.task_irv);
    }

    //设置所有信息: isInfo:是否是任务信息页(WaitAcceptOrderActivity)
    public void setAll(final TaskItem taskItem, boolean isInfo){
        userItemReleaseView.setAll(taskItem.getUser(), taskItem.getReleaseTime(),   //用户发布信息
                taskItem.getSchool(), taskItem.getDescription(), !isInfo);
        costText.setText("¥" + taskItem.getCost()); //金额
        contentText.setText(taskItem.getContent()); //内容
        //图片
        String imageUrl = SchoolTask.TASK_IMAGE_URL + taskItem.getOrderId() + "/";
        final List<ImageItem> imageItems = new ArrayList<>();
        for(int i = 0; i< taskItem.getImageNum(); i++){
            imageItems.add(new ImageItem(1,imageUrl + i + ".png"));
        }
        imageRecyclerView.clear();
        imageRecyclerView.addImages(imageItems);
        //点击跳转事件
        if (isInfo){
            //浏览图片
            imageRecyclerView.setImageClickListener(new ImageRecyclerView.ImageClickListener() {
                @Override
                public void onImageClick(int position, ImageItem imageItem) {
                    Intent intent = new Intent(getContext(), ImageActivity.class);
                    intent.putExtra("editable", false);
                    intent.putExtra("index", position);
                    intent.putExtra("images", (Serializable) imageRecyclerView.getImages());
                    getContext().startActivity(intent);
                }
            });
        }
        else{
            imageRecyclerView.setImageClickListener(new ImageRecyclerView.ImageClickListener() {
                @Override
                public void onImageClick(int position, ImageItem imageItem) {
                    openWaitAcceptOrderActivity(taskItem);
                }
            });
            imageRecyclerView.setSpaceClickListener(new ImageRecyclerView.SpaceClickListener() {
                @Override
                public void OnSpaceClick() {
                    openWaitAcceptOrderActivity(taskItem);
                }
            });
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    openWaitAcceptOrderActivity(taskItem);
                }
            });
        }
    }

    private void openWaitAcceptOrderActivity(TaskItem taskItem){
        Intent intent = new Intent(getContext(), WaitAcceptOrderActivity.class);
        intent.putExtra("taskItem", taskItem);
        getContext().startActivity(intent);
    }
}
