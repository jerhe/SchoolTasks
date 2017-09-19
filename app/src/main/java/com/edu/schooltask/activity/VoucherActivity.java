package com.edu.schooltask.activity;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.VoucherAdapter;
import com.edu.schooltask.beans.Voucher;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.view.recyclerview.RefreshRecyclerView;
import com.edu.schooltask.utils.GsonUtil;

import java.util.List;

import server.api.client.VoucherClient;
import server.api.result.Result;


public class VoucherActivity extends BaseActivity {

    private RefreshRecyclerView<Voucher> recyclerView;

    @Override
    public int getLayout() {
        return R.layout.activity_voucher;
    }

    @Override
    public void init() {
        recyclerView = new RefreshRecyclerView<Voucher>(this, true) {
            @Override
            protected BaseQuickAdapter initAdapter(List list) {
                return new VoucherAdapter(list);
            }

            @Override
            protected void requestData(Result result) {
                VoucherClient.getAvailableVouchers(result);
            }

            @Override
            protected void onSuccess(int id, Object data) {
                List<Voucher> vouchers = GsonUtil.toVoucherList(data);
                recyclerView.add(vouchers);
            }

            @Override
            protected void onFailed(int id, int code, String error) {
                toastShort(error);
            }
        };
        recyclerView.setEmptyView(R.layout.empty_voucher);
        addView(recyclerView);
        recyclerView.refresh();
    }
}
