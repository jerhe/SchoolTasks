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

public class SetPayPwdActivity extends BaseActivity {
    @BindView(R.id.spp_pwd) InputTextView pwdText;
    @BindView(R.id.spp_pwd_2) InputTextView pwdText2;
    @BindView(R.id.spp_ppib) PayPasswordInputBoard inputBoard;
    @BindView(R.id.spp_pre_btn) TextView preBtn;
    @BindView(R.id.spp_success_tip) TextView successText;

    @OnClick(R.id.spp_pre_btn)
    public void pre(){
        preBtn.setVisibility(View.GONE);
        pwdText.clearPayPassword();
        pwdText2.clearPayPassword();
        pwdText.setVisibility(View.VISIBLE);
        pwdText2.setVisibility(View.INVISIBLE);
        inputBoard.setPasswordView(pwdText);
    }

    private Result setPayPwdResult = new Result(true) {
        @Override
        public void onResponse(int id) {
            progressDialog.dismiss();
        }

        @Override
        public void onSuccess(int id, Object data) {
            pwdText2.setVisibility(View.INVISIBLE);
            preBtn.setVisibility(View.GONE);
            successText.setVisibility(View.VISIBLE);
            inputBoard.hide();
        }

        @Override
        public void onFailed(int id, int code, String error) {
            toastShort(error);
            pwdText2.clearPayPassword();
        }
    };

    ProgressDialog progressDialog;

    @Override
    public int getLayout() {
        return R.layout.activity_set_pay_pwd;
    }

    @Override
    public void init() {
        inputBoard.setPasswordView(pwdText);
        pwdText.setPayPasswordFinishedListener(new PayPasswordView.PayPasswordFinishedListener() {
            @Override
            public void onFinished(String password) {
                pwdText.setVisibility(View.INVISIBLE);
                pwdText2.setVisibility(View.VISIBLE);
                inputBoard.setPasswordView(pwdText2);
                preBtn.setVisibility(View.VISIBLE);
            }
        });
        pwdText2.setPayPasswordFinishedListener(new PayPasswordView.PayPasswordFinishedListener() {
            @Override
            public void onFinished(String password) {
                if(password.equals(pwdText.getPayPassword())){
                    progressDialog = ProgressDialog.show(SetPayPwdActivity.this, "", "请稍候...", true, false);
                    AccountClient.setPayPwd(setPayPwdResult, EncriptUtil.getMD5(password));
                }
                else{
                    toastShort("两次输入的密码不一致");
                    pwdText2.clearPayPassword();
                }
            }
        });
    }
}
