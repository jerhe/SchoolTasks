package com.edu.schooltask.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.edu.schooltask.R;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.beans.User;
import com.edu.schooltask.view.InputText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import server.api.SchoolTask;
import server.api.pwd.SetPaypwdEvent;

public class SetPayPwdActivity extends BaseActivity {
    private InputText pwdText;
    private InputText pwdText2;
    private Button confirmBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pay_pwd);
        EventBus.getDefault().register(this);
        pwdText = (InputText) findViewById(R.id.spp_pwd);
        pwdText2 = (InputText) findViewById(R.id.spp_pwd_2);
        confirmBtn = (Button) findViewById(R.id.spp_confirm_btn);
        pwdText.setInputFilter(5);
        pwdText2.setInputFilter(5);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmBtn.setEnabled(false);
                setPayPwd();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSetPayPwd(SetPaypwdEvent event){
        if(event.isOk()){
            toastShort("设置成功");
            finish();
        }
        else{
            confirmBtn.setEnabled(true);
            toastShort(event.getError());
        }
    }

    private void setPayPwd(){
        String pwd1 = pwdText.getText();
        String pwd2 = pwdText2.getText();
        if(pwd1.length() == 0){
            toastShort("请输入支付密码");
            pwdText.requestFocus();
            return;
        }
        if(pwd2.length() == 0){
            toastShort("请再次输入支付密码");
            pwdText.requestFocus();
            return;
        }
        if(pwd1.length() != 6){
            toastShort("支付密码必须为6位数字，请重新输入");
            pwdText.requestFocus();
            return;
        }
        if(!pwd1.equals(pwd2)){
            toastShort("两次输入的密码不一致");
            return;
        }
        User user = mDataCache.getUser();
        if(user == null){
            return;
        }
        else{
            confirmBtn.setText("提交中...");
            SchoolTask.setPayPwd(pwd1);
        }
    }
}
