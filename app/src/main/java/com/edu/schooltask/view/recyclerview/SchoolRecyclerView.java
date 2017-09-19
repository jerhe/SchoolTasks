package com.edu.schooltask.view.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.adapter.SchoolListAdapter;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/9/8.
 */

public class SchoolRecyclerView extends BaseRecyclerView<String> {

    public SchoolRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected BaseQuickAdapter initAdapter(List<String> list) {
        return new SchoolListAdapter(list);
    }
}
