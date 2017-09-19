package com.edu.schooltask.activity;

import android.app.ProgressDialog;
import android.view.View;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.view.InputTextView;
import com.edu.schooltask.view.PayPasswordInputBoard;
import com.edu.schooltask.view.PayPasswordView;
import com.edu.schooltask.utils.EncriptUtil;

import butterknife.BindView;
import butterknife.OnClick;
import server.api.client.AccountClient;
import server.api.result.Result;

public class UpdatePayPwdActivity extends BaseActivity {

    @BindView(R.id.upp_pay_pwd_old) InputTextView oldPayPassword;
    @BindView(R.id.upp_pay_pwd_new) InputTextView newPayPassword;
    @BindView(R.id.upp_pay_pwd_new_again) InputTextView newAgainPayPassword;
    @BindView(R.id.upp_ppib) PayPasswordInputBoard inputBoard;
    @BindView(R.id.upp_pre_btn) TextView preBtn;
    @BindView(R.id.upp_success_tip) TextView successText;

    @OnClick(R.id.upp_pre_btn)
    public void pre(){
        preBtn.setVisibility(View.GONE);
        newPayPassword.clearPayPassword();
        newAgainPayPassword.clearPayPassword();
        newPayPassword.setVisibility(View.VISIBLE);
        newAgainPayPassword.setVisibility(View.INVISIBLE);
        inputBoard.setPasswordView(newPayPassword);
    }

    private Result checkPayPasswordResult = new Result(true){
        @Override
        public void onResponse(int id) {
            progressDialog.dismiss();
        }

        @Override
        public void onSuccess(int id, Object data) {
                oldPayPassword.setVisibility(View.INVISIBLE);
                newPayPassword.setVisibility(View.VISIBLE);
                inputBoard.setPasswordView(newPayPassword);
        }

        @Override
        public void onFailed(int id, int code, String error) {
                oldPayPassword.clearPayPassword();
                toastShort(error);
        }
    };
    private Result updatePayPasswordResult = new Result(true) {
        @Override
        public void onResponse(int id) {
            progressDialog.dismiss();
        }

        @Override
        public void onSuccess(int id, Object data) {
            newAgainPayPassword.setVisibility(View.INVISIBLE);
            preBtn.setVisibility(View.GONE);
            successText.setVisibility(View.VISIBLE);
            inputBoard.hide();
        }

        @Override
        public void onFailed(int id, int code, String error) {
            newAgainPayPassword.clearPayPassword();
            toastShort(error);
        }
    };

    ProgressDialog progressDialog;

    @Override
    public int getLayout() {
        return R.layout.activity_update_pay_pwd;
    }

    @Override
    public void init() {
        oldPayPassword.setPayPasswordFinishedListener(new PayPasswordView.PayPasswordFinishedListener() {
            @Override
            public void onFinished(String password) {
                progressDialog = ProgressDialog.show(UpdatePayPwdActivity.this, "", "验证中...", true, false);
                AccountClient.checkPayPassword(checkPayPasswordResult, EncriptUtil.getMD5(password));
            }
        });
        newPayPassword.setPayPasswordFinishedListener(new PayPasswordView.PayPasswordFinishedListener() {
            @Override
            public void onFinished(String password) {
                newPayPassword.setVisibility(View.INVISIBLE);
                newAgainPayPassword.setVisibility(View.VISIBLE);
                inputBoard.setPasswordView(newAgainPayPassword);
                preBtn.setVisibility(View.VISIBLE);
            }
        });
        newAgainPayPassword.setPayPasswordFinishedListener(new PayPasswordView.PayPasswordFinishedListener() {
            @Override
            public void onFinished(String password) {
                String old = oldPayPassword.getPayPassword();
                if(password.equals(old)){
                    newAgainPayPassword.clearPayPassword();
                    toastShort("不能与旧的支付密码相同");
                    return;
                }
                if(! password.equals(newPayPassword.getPayPassword())){
                    newAgainPayPassword.clearPayPassword();
                    toastShort("两次输入的密码不一致");
                    return;
                }
                progressDialog = ProgressDialog.show(UpdatePayPwdActivity.this, "", "请稍候...", true, false);
                AccountClient.updatePayPwd(updatePayPasswordResult, EncriptUtil.getMD5(old),
                        EncriptUtil.getMD5(password));
            }
        });
        inputBoard.setPasswordView(oldPayPassword);
    }

}
