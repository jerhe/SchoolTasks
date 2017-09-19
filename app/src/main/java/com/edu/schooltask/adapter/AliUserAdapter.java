package com.edu.schooltask.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.AliUser;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/9/14.
 */

public class AliUserAdapter extends BaseQuickAdapter<AliUser, BaseViewHolder> {

    public AliUserAdapter(@Nullable List data) {
        super(R.layout.item_ali_user, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AliUser item) {
        helper.setText(R.id.au_id, item.getAliId());
        helper.setText(R.id.au_name, item.getAliName());
        helper.addOnClickListener(R.id.au_delete);
    }


}
