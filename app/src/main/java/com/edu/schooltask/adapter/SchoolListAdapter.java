package com.edu.schooltask.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/8/12.
 */

public class SchoolListAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public SchoolListAdapter(@Nullable List<String> data) {
        super(R.layout.item_text_match, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.text, item);
    }
}
