package com.edu.schooltask.fragment.login;

import android.support.annotation.MainThread;
import android.view.View;
import android.widget.Button;

import com.edu.schooltask.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import com.edu.schooltask.base.BaseFragment;
import com.edu.schooltask.beans.User;
import com.edu.schooltask.event.LoginEvent;
import com.edu.schooltask.event.LoginSuccessEvent;
import com.edu.schooltask.http.HttpUtil;
import com.edu.schooltask.utils.ACache;
import com.edu.schooltask.utils.KeyBoardUtil;
import com.edu.schooltask.utils.TextUtil;
import com.edu.schooltask.view.InputText;

/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class LoginFragment extends BaseFragment {
    private Button loginBtn;
    private InputText idText;
    private InputText pwdText;

    public LoginFragment() {
        super(R.layout.fragment_login);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void init() {
        idText = (InputText) view.findViewById(R.id.login_id);
        pwdText = (InputText) view.findViewById(R.id.login_pwd);
        loginBtn = (Button) view.findViewById(R.id.login_login_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        idText.setInputFilter(0);
        pwdText.setInputFilter(3);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogin(LoginEvent event){
        loginBtn.setText("登录");
        if(event.isOk()){
            User user = User.jsonObjectToUser(event.getData());
            mDataCache.saveUser(user);
            toastShort("登录成功");
            EventBus.getDefault().post(new LoginSuccessEvent());
            finish();
        }
        else{
            toastShort(event.getError());
        }
    }

    private void login(){
        if(!"登录".equals(loginBtn.getText()))return;
        final String id = idText.getText();
        String pwd = pwdText.getText();
        if(id.length() == 0){
            toastShort("请输入手机号");
            return;
        }
        if(pwd.length() == 0){
            toastShort("请输入密码");
            return;
        }
        if(pwd.length() < 6){
            toastShort("请输入正确的密码");
            return;
        }
        loginBtn.setText("登录中...");
        HttpUtil.login(id, TextUtil.getMD5(pwd));
    }
}
