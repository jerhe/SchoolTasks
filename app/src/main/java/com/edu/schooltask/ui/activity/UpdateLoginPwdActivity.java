package com.edu.schooltask.ui.activity;

import android.os.Bundle;

import com.edu.schooltask.R;
import com.edu.schooltask.ui.base.BaseActivity;
import com.edu.schooltask.event.LogoutEvent;
import com.edu.schooltask.filter.PasswordFilter;
import com.edu.schooltask.utils.EncriptUtil;
import com.edu.schooltask.ui.view.InputTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import server.api.SchoolTask;
import server.api.event.user.UpdateLoginPwdEvent;

public class UpdateLoginPwdActivity extends BaseActivity {
    @BindView(R.id.ulp_old)
    InputTextView oldText;
    @BindView(R.id.ulp_new)
    InputTextView newText;
    @BindView(R.id.ulp_new_again)
    InputTextView newAgainText;

    @OnClick(R.id.ulp_btn)
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
        if(oldPwd.length() < 6){
            toastShort("原密码至少6位");
            return;
        }
        if(newPwd.length() < 6){
            toastShort("新密码至少6位");
            return;
        }
        if(!newPwd.equals(newAgainPwd)){
            toastShort("两次输入的密码不一致");
            return;
        }
        if(newPwd.equals(oldPwd)){
            toastShort("新密码不能和原密码相同");
            return;
        }
        SchoolTask.updateLoginPwd(EncriptUtil.getMD5(oldPwd), EncriptUtil.getMD5(newPwd));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_login_pwd);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        PasswordFilter filter = new PasswordFilter();
        oldText.setInputFilter(filter);
        newText.setInputFilter(filter);
        newAgainText.setInputFilter(filter);

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

}
