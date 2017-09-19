package com.edu.schooltask.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.task.TaskItem;
import com.edu.schooltask.view.task.TaskItemView;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/8/14.
 */

public class TaskItemAdapter extends BaseQuickAdapter<TaskItem, BaseViewHolder> {
    public TaskItemAdapter(@Nullable List<TaskItem> data) {
        super(R.layout.item_task_item_view, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TaskItem item) {
        TaskItemView taskItemView = helper.getView(R.id.tv_tiv);
        taskItemView.setAll(item, false);
    }
}
