package com.edu.schooltask.ui.view.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.adapter.TaskItemAdapter;
import com.edu.schooltask.beans.task.TaskItem;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/9/5.
 */

public class TaskItemRecyclerView extends BasePageRecyclerView<TaskItem> {

    public TaskItemRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected BaseQuickAdapter initAdapter(List<TaskItem> list) {
        return new TaskItemAdapter(list);
    }
}
