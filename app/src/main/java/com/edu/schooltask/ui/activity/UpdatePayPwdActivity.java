package com.edu.schooltask.ui.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.ui.base.BaseActivity;
import com.edu.schooltask.ui.view.InputTextView;
import com.edu.schooltask.ui.view.PayPasswordView;
import com.edu.schooltask.ui.view.PayPasswordInputBoard;
import com.edu.schooltask.utils.EncriptUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import server.api.SchoolTask;
import server.api.event.user.account.CheckPayPasswordEvent;
import server.api.event.user.account.UpdatePayPasswordEvent;

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

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pay_pwd);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        oldPayPassword.setPayPasswordFinishedListener(new PayPasswordView.PayPasswordFinishedListener() {
            @Override
            public void onFinished(String password) {
                progressDialog = ProgressDialog.show(UpdatePayPwdActivity.this, "", "验证中...", true, false);
                SchoolTask.checkPayPassword(password);
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
                SchoolTask.updatePayPwd(EncriptUtil.getMD5(old), EncriptUtil.getMD5(password));
            }
        });
        inputBoard.setPasswordView(oldPayPassword);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCheckPayPassword(CheckPayPasswordEvent event){
        if(progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
        if(event.isOk()){
            oldPayPassword.setVisibility(View.INVISIBLE);
            newPayPassword.setVisibility(View.VISIBLE);
            inputBoard.setPasswordView(newPayPassword);
        }
        else{
            oldPayPassword.clearPayPassword();
            toastShort(event.getError());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdatePayPassword(UpdatePayPasswordEvent event){
        if(progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
        if(event.isOk()){
            newAgainPayPassword.setVisibility(View.INVISIBLE);
            preBtn.setVisibility(View.GONE);
            successText.setVisibility(View.VISIBLE);
            inputBoard.hide();
        }
        else{
            newAgainPayPassword.clearPayPassword();
            toastShort(event.getError());
        }
    }

}
