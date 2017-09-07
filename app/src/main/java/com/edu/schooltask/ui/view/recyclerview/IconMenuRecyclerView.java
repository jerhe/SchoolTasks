package com.edu.schooltask.ui.view.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.adapter.IconMenuAdapter;
import com.edu.schooltask.item.IconMenuItem;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/8/26.
 */

public class IconMenuRecyclerView extends BaseRecyclerView<IconMenuItem> {
    public IconMenuRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected BaseQuickAdapter initAdapter(List<IconMenuItem> list) {
        return new IconMenuAdapter(list);
    }
}
