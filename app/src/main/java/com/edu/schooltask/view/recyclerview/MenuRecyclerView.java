package com.edu.schooltask.view.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.TextListAdapter;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/9/8.
 */

public class MenuRecyclerView extends BaseRecyclerView<String> {

    public MenuRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected BaseQuickAdapter initAdapter(List<String> list) {
        return new TextListAdapter(R.layout.item_menu, list);
    }
}
