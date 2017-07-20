package com.edu.schooltask.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/5/28.
 */

public class SchoolAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public SchoolAdapter(int layoutResId, List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.school_name, item);
    }
}
