package com.edu.schooltask.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.edu.schooltask.R;
import com.edu.schooltask.beans.Voucher;

import java.util.List;

/**
 * Created by 夜夜通宵 on 2017/8/17.
 */

public class VoucherAdapter extends BaseQuickAdapter<Voucher, BaseViewHolder> {
    public VoucherAdapter(@Nullable List<Voucher> data) {
        super(R.layout.item_voucher, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Voucher item) {
        helper.setText(R.id.voucher_money, item.getMoney() + "元");
        helper.setText(R.id.voucher_text, item.getExplanation());
        helper.setText(R.id.voucher_expire, item.getExpireTime() + "到期");
    }
}
