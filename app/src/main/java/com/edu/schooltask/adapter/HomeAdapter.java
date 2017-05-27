package com.edu.schooltask.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.activity.ImageActivity;
import com.edu.schooltask.activity.WaitAcceptOrderActivity;
import com.edu.schooltask.base.BaseActivity;
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
        addItemType(HomeItem.TASK_ITEM, R.layout.item_task);
        addItemType(HomeItem.TASK_INFO_ITEM, R.layout.item_task);
        addItemType(HomeItem.COUNT_ITEM, R.layout.item_task_count);
        addItemType(HomeItem.COMMENT, R.layout.item_task_comment);
    }

    @Override
    protected void convert(final BaseViewHolder helper, HomeItem item) {
        switch (item.getItemType()){
            case 0:
                helper.setText(R.id.lf_text,item.getTip());
                break;
            case 1:
                setTaskItem(helper, item, false);
                break;
            case 2:
                setTaskItem(helper, item, true);
                break;
            case 3:
                TaskCountItem taskCountItem = item.getTaskCountItem();
                helper.setText(R.id.tc_comment_count, "评论("+taskCountItem.getCommentCount()+")");
                helper.setText(R.id.tc_look_count, "浏览:"+taskCountItem.getLookCount());
                break;
            case 4:
                setCommentItem(helper, item);
                break;
        }
    }

    private void setTaskItem(BaseViewHolder helper, final HomeItem item, boolean isInfo){
        TaskItem taskItem = item.getTaskItem();
        helper.setText(R.id.ui_name, taskItem.getName());
        helper.setText(R.id.ui_school,
                isInfo ? taskItem.getSchool() + "(任务)" : taskItem.getSchool());
        helper.setText(R.id.ui_des, taskItem.getDescription());
        helper.setText(R.id.task_cost, "¥"+ taskItem.getCost());
        helper.setText(R.id.task_content, taskItem.getContent());
        helper.setText(R.id.ui_release_time,
                DateUtil.getLong(DateUtil.stringToDate(taskItem.getReleaseTime())));
        //性别
        switch (taskItem.getSex()){
            case 0:
                helper.setText(R.id.ui_sex, "♂");
                helper.setTextColor(R.id.ui_sex, Color.parseColor("#1B9DFF"));
                break;
            case 1:
                helper.setText(R.id.ui_sex, "♀");
                helper.setTextColor(R.id.ui_sex, Color.parseColor("#FF3333"));
                break;
            default:
                helper.setText(R.id.ui_sex, "");
        }
        //头像
        CircleImageView headView = helper.getView(R.id.ui_head);
        GlideUtil.setHead(headView.getContext(), taskItem.getUserId(),headView, false);
        //图片
        String imageUrl = SchoolTask.TASK_IMAGE_URL + taskItem.getOrderId() + "/";
        final List<ImageItem> imageItems = new ArrayList<>();
        for(int i = 0; i< taskItem.getImageNum(); i++){
            imageItems.add(new ImageItem(1,imageUrl + i + ".png"));
        }
        RecyclerView recyclerView = helper.getView(R.id.task_rv);
        ImageAdapter adapter = new ImageAdapter(R.layout.item_image, imageItems);
        recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(), 4));
        recyclerView.setAdapter(adapter);
        if(isInfo){ //任务详情需要添加其他事件
            helper.addOnClickListener(R.id.ui_layout);
            adapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    Intent intent = new Intent(activity, ImageActivity.class);
                    intent.putExtra("editable", false);
                    intent.putExtra("index", position);
                    intent.putExtra("images", (Serializable) imageItems);
                    activity.startActivity(intent);
                }
            });
        }
        else{
            //子RecyclerView设置点击事件 因为父级RecyclerView点击被子RecyclerView拦截
            adapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    Intent intent = new Intent(activity, WaitAcceptOrderActivity.class);
                    intent.putExtra("task", item);
                    activity.startActivity(intent);
                }
            });
            recyclerView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_UP){
                        Intent intent = new Intent(activity, WaitAcceptOrderActivity.class);
                        intent.putExtra("task", item);
                        activity.startActivity(intent);
                    }
                    return false;
                }
            });
        }
    }

    private void setCommentItem(BaseViewHolder helper, HomeItem item){
        TaskComment taskComment = item.getTaskComment();
        CircleImageView headView = helper.getView(R.id.ui_head);
        GlideUtil.setHead(headView.getContext(), taskComment.getUserId(),headView, false);
        helper.setText(R.id.ui_school, taskComment.getUserSchool());
        helper.setText(R.id.ui_name,taskComment.getUserName());
        helper.setText(R.id.ui_release_time,
                DateUtil.getLong(DateUtil.stringToDate(taskComment.getCommentTime())));
        switch (taskComment.getUserSex()){
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
        if(taskComment.getParentId() != 0){
            if(taskComment.getParentId() != item.parentId){
                helper.setText(R.id.tc_comment, "回复 "+taskComment.getToUserName()
                        +"："+taskComment.getComment());
            }
            else{
                helper.setText(R.id.tc_comment, taskComment.getComment());
            }
            helper.setVisible(R.id.tc_child_count, false);
        }
        else{
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
