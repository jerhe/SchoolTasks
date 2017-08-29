package com.edu.schooltask.ui.activity;

import android.os.Bundle;

import com.edu.schooltask.R;
import com.edu.schooltask.ui.base.BaseActivity;
import com.edu.schooltask.filter.NumberFilter;
import com.edu.schooltask.ui.view.Inputtextview.InputTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import server.api.SchoolTask;
import server.api.event.user.account.SetPayPwdEvent;

public class SetPayPwdActivity extends BaseActivity {
    @BindView(R.id.spp_pwd)
    InputTextView pwdText;
    @BindView(R.id.spp_pwd_2)
    InputTextView pwdText2;

    @OnClick(R.id.spp_confirm_btn)
    public void setPayPwd(){
        String pwd1 = pwdText.getText();
        String pwd2 = pwdText2.getText();
        if(pwd1.length() == 0){
            toastShort(getString(R.string.inputTip, pwdText.getName()));
            pwdText.requestFocus();
            return;
        }
        if(pwd2.length() == 0){
            toastShort(getString(R.string.inputAgainTip, pwdText.getName()));
            pwdText.requestFocus();
            return;
        }
        if(pwd1.length() != 6){
            toastShort(getString(R.string.inputError, pwdText.getName()));
            pwdText.requestFocus();
            return;
        }
        if(!pwd1.equals(pwd2)){
            toastShort(getString(R.string.inputAgainError));
            return;
        }
        SchoolTask.setPayPwd(pwd1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pay_pwd);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        pwdText.setInputFilter(new NumberFilter());
        pwdText2.setInputFilter(new NumberFilter());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSetPayPwd(SetPayPwdEvent event){
        if(event.isOk()){
            toastShort(getString(R.string.setSuccess));
            finish();
        }
        else{
            toastShort(event.getError());
        }
    }
}
