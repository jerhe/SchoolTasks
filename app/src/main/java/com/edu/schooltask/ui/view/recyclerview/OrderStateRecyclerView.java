package com.edu.schooltask.ui.view.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.OrderStateAdapter;
import com.edu.schooltask.item.OrderStateItem;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/8/16.
 */

public class OrderStateRecyclerView extends BaseRecyclerView<OrderStateItem> {

    public OrderStateRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected BaseQuickAdapter initAdapter(List<OrderStateItem> list) {
        return new OrderStateAdapter(R.layout.item_order_state, list);
    }


}
