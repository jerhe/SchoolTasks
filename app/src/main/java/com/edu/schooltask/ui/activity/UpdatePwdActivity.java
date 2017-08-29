package com.edu.schooltask.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.edu.schooltask.R;
import com.edu.schooltask.ui.base.BaseActivity;
import com.edu.schooltask.event.LogoutEvent;
import com.edu.schooltask.filter.NumberFilter;
import com.edu.schooltask.filter.PasswordFilter;
import com.edu.schooltask.utils.EncriptUtil;
import com.edu.schooltask.ui.view.Inputtextview.InputTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import server.api.SchoolTask;
import server.api.event.user.account.UpdatePaypwdEvent;
import server.api.event.user.UpdateLoginPwdEvent;

public class UpdatePwdActivity extends BaseActivity {
    @BindView(R.id.up_old)
    InputTextView oldText;
    @BindView(R.id.up_new)
    InputTextView newText;
    @BindView(R.id.up_new_again)
    InputTextView newAgainText;
    //@BindView(R.id.up_btn) Button button;

    @OnClick(R.id.up_btn)
    public void update(){
        String oldPwd = oldText.getText();
        String newPwd = newText.getText();
        String newAgainPwd = newAgainText.getText();
        if(oldPwd.length() == 0){
            toastShort(getString(R.string.inputTip, oldText.getName()));
            return;
        }
        if(newPwd.length() == 0){
            toastShort(getString(R.string.inputTip, newText.getName()));
            return;
        }
        if(newAgainPwd.length() == 0){
            toastShort(getString(R.string.inputAgainTip, newText.getName()));
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
        if(type == 0)
            SchoolTask.updateLoginPwd(EncriptUtil.getMD5(oldPwd), EncriptUtil.getMD5(newPwd));
        else
            SchoolTask.updatePayPwd(EncriptUtil.getMD5(oldPwd), EncriptUtil.getMD5(newPwd));
    }

    int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pwd);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        type = intent.getIntExtra("relationType",0);
        setTitle(type == 0 ? "修改登录密码" : "修改支付密码");

        if(type == 0){
            PasswordFilter filter = new PasswordFilter();
            oldText.setInputFilter(filter);
            newText.setInputFilter(filter);
            newAgainText.setInputFilter(filter);
        }
        else{
            NumberFilter filter = new NumberFilter();
            oldText.setInputFilter(filter);
            newText.setInputFilter(filter);
            newAgainText.setInputFilter(filter);
        }
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
            toastShort(event.getError());
        }
    }

}
