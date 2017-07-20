package com.edu.schooltask.fragment.login;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.edu.schooltask.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.edu.schooltask.base.BaseFragment;
import com.edu.schooltask.beans.Detail;
import com.edu.schooltask.beans.User;
import server.api.login.LoginEvent;

import com.edu.schooltask.beans.UserBaseInfo;
import com.edu.schooltask.data.DataCache;
import com.edu.schooltask.event.LoginSuccessEvent;
import com.edu.schooltask.utils.KeyBoardUtil;
import com.edu.schooltask.utils.TextUtil;
import com.edu.schooltask.view.InputText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import server.api.SchoolTask;

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
        idText = getView(R.id.login_id);
        pwdText = getView(R.id.login_pwd);
        loginBtn = getView(R.id.login_login_btn);
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
        if(event.isOk()){
            Log.e("2", event.getData().toString());
            User user =  new Gson().fromJson(new Gson().toJson(event.getData()), new TypeToken<User>() {}.getType());
            mDataCache.saveUser(user);
            toastShort("登录成功");
            EventBus.getDefault().post(new LoginSuccessEvent());
            finish();
        }
        else{
            pwdText.setText("");
            loginBtn.setText("登录");
            loginBtn.setEnabled(true);
            toastShort(event.getError());
        }
    }

    private void login(){
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
        loginBtn.setEnabled(false);
        KeyBoardUtil.hideKeyBoard(getActivity());
        loginBtn.setText("登录中...");
        SchoolTask.login(id, TextUtil.getMD5(pwd));
    }
}
