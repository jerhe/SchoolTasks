package com.edu.schooltask.activity;

import android.app.ProgressDialog;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.edu.schooltask.R;
import com.edu.schooltask.adapter.AliUserAdapter;
import com.edu.schooltask.beans.AliUser;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.view.recyclerview.RefreshRecyclerView;
import com.edu.schooltask.utils.DialogUtil;
import com.edu.schooltask.utils.GsonUtil;
import com.orhanobut.dialogplus.DialogPlus;

import java.util.List;

import server.api.client.AccountClient;
import server.api.result.Result;

public class AliUserActivity extends BaseActivity {

    private RefreshRecyclerView recyclerView;


    private Result deleteAliUserResult = new Result(true) {
        @Override
        public void onResponse(int id) {
            progressDialog.dismiss();
        }

        @Override
        public void onSuccess(int id, Object data) {
            recyclerView.refresh();
        }

        @Override
        public void onFailed(int id, int code, String error) {
            toastShort(error);
        }
    };

    private ProgressDialog progressDialog;

    @Override
    public int getLayout() {
        return R.layout.activity_ali_user;
    }

    @Override
    public void init() {
        recyclerView = new RefreshRecyclerView<AliUser>(this, true) {
            @Override
            public BaseQuickAdapter initAdapter(List list) {
                return new AliUserAdapter(list);
            }

            @Override
            public void requestData(Result result) {
                AccountClient.getAliUserList(result);
            }

            @Override
            public void onSuccess(int id, Object data) {
                recyclerView.add(GsonUtil.toAliUserList(data));
            }

            @Override
            public void onFailed(int id, int code, String error) {
                toastShort(error);
            }

            @Override
            public void onChildClick(View view, int position, final AliUser aliUser) {
                DialogUtil.createTextDialog(AliUserActivity.this, "提示", "确定要删除该账号吗？", "",
                    new DialogUtil.OnClickListener() {
                        @Override
                        public void onClick(DialogPlus dialogPlus) {
                            progressDialog = ProgressDialog.show(AliUserActivity.this, "", "删除中...", true, true);
                            AccountClient.deleteAliUser(deleteAliUserResult, aliUser.getId());
                        }
                    }).show();
            }
        };
        recyclerView.refresh();
        addView(recyclerView);
    }
}
