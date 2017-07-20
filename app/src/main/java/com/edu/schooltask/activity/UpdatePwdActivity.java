package com.edu.schooltask.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.edu.schooltask.R;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.event.LogoutEvent;
import com.edu.schooltask.utils.TextUtil;
import com.edu.schooltask.view.InputText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import server.api.SchoolTask;
import server.api.account.UpdatePaypwdEvent;
import server.api.user.UpdateLoginPwdEvent;

public class UpdatePwdActivity extends BaseActivity {
    InputText oldText;
    InputText newText;
    InputText newAgainText;
    Button button;

    int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pwd);
        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        type = intent.getIntExtra("relationType",0);
        setTitle(type == 0 ? "修改登录密码" : "修改支付密码");
        oldText = getView(R.id.up_old);
        newText = getView(R.id.up_new);
        newAgainText = getView(R.id.up_new_again);
        button = getView(R.id.up_btn);

        if(type == 0){
            oldText.setInputFilter(3);
            newText.setInputFilter(3);
            newAgainText.setInputFilter(3);
        }
        else{
            oldText.setInputFilter(5);
            newText.setInputFilter(5);
            newAgainText.setInputFilter(5);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPwd = oldText.getText();
                String newPwd = newText.getText();
                String newAgainPwd = newAgainText.getText();
                if(oldPwd.length() == 0){
                    toastShort("请输入原密码");
                    return;
                }
                if(newPwd.length() == 0){
                    toastShort("请输入新密码");
                    return;
                }
                if(newAgainPwd.length() == 0){
                    toastShort("请再次输入新密码");
                    return;
                }
                if(type == 0){
                    if(oldPwd.length() < 6){
                        toastShort("原密码至少6位");
                        return;
                    }
                    if(newPwd.length() < 6){
                        toastShort("新密码至少6位");
                        return;
                    }
                }
                if(type == 1){
                    if(oldPwd.length() != 6){
                        toastShort("支付密码必须是6位数字");
                        return;
                    }
                    if(newPwd.length() != 6){
                        toastShort("支付密码必须是6位数字");
                        return;
                    }
                }
                if(!newPwd.equals(newAgainPwd)){
                    toastShort("两次输入的密码不一致");
                    return;
                }
                if(newPwd.equals(oldPwd)){
                    toastShort("新密码不能和原密码相同");
                    return;
                }
                button.setEnabled(false);
                if(type == 0)
                    SchoolTask.updatePwd(TextUtil.getMD5(oldPwd), TextUtil.getMD5(newPwd));
                else
                    SchoolTask.updatePayPwd(TextUtil.getMD5(oldPwd), TextUtil.getMD5(newPwd));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateLoginPwdEvent(UpdateLoginPwdEvent event){
        if(event.isOk()){
            toastShort("修改成功，请重新登录");
            EventBus.getDefault().post(new LogoutEvent());
            remainHome();
            openActivity(LoginActivity.class);
        }
        else{
            button.setEnabled(true);
            toastShort(event.getError());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdatePayPwdEvent(UpdatePaypwdEvent event){
        if(event.isOk()){
            toastShort("修改成功");
            finish();
        }
        else{
            button.setEnabled(true);
            toastShort(event.getError());
        }
    }

}
