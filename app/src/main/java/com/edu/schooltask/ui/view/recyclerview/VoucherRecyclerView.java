package com.edu.schooltask.ui.view.recyclerview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.adapter.VoucherAdapter;
import com.edu.schooltask.beans.Voucher;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/8/17.
 */

public class VoucherRecyclerView extends BaseRecyclerView<Voucher> {

    public VoucherRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected BaseQuickAdapter initAdapter(List<Voucher> list) {
        return new VoucherAdapter(list);
    }
}
