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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import server.api.SchoolTask;
import server.api.event.user.account.SetPayPwdEvent;

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

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pay_pwd);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

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
                    SchoolTask.setPayPwd(password);
                }
                else{
                    toastShort("两次输入的密码不一致");
                    pwdText2.clearPayPassword();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSetPayPwd(SetPayPwdEvent event){
        if(progressDialog != null) progressDialog.dismiss();
        if(event.isOk()){
            pwdText2.setVisibility(View.INVISIBLE);
            preBtn.setVisibility(View.GONE);
            successText.setVisibility(View.VISIBLE);
            inputBoard.hide();
        }
        else{
            toastShort(event.getError());
            pwdText2.clearPayPassword();
        }
    }
}
