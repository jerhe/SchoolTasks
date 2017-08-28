package com.edu.schooltask.ui.fragment.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;

import com.edu.schooltask.R;
import com.edu.schooltask.beans.UserInfoWithToken;
import com.edu.schooltask.event.LoginSuccessEvent;
import com.edu.schooltask.filter.PasswordFilter;
import com.edu.schooltask.filter.PhoneFilter;
import com.edu.schooltask.ui.base.BaseFragment;
import com.edu.schooltask.ui.view.Inputtextview.InputTextView;
import com.edu.schooltask.utils.EncriptUtil;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.KeyBoardUtil;
import com.edu.schooltask.utils.StringUtil;
import com.edu.schooltask.utils.UserUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import server.api.SchoolTask;
import server.api.user.login.LoginEvent;

/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class LoginFragment extends BaseFragment {

    @BindView(R.id.login_id)
    InputTextView idText;
    @BindView(R.id.login_pwd)
    InputTextView pwdText;
    @BindView(R.id.login_login_btn) Button loginBtn;

    @OnClick(R.id.login_login_btn)
    public void login(){
        String id = idText.getText();
        String pwd = pwdText.getText();

        int emptyIndex = StringUtil.isEmpty(id, pwd);
        if(emptyIndex != -1){
            String[] strings = {"手机号", "密码"};
            toastShort("请输入" + strings[emptyIndex]);
            return;
        }

        if(StringUtil.checkLength(pwd, 6)){
            toastShort("密码至少为6位");
            return;
        }
        KeyBoardUtil.hideKeyBoard(getActivity());   //隐藏输入法
        loginBtn.setText("登录中...");
        loginBtn.setEnabled(false);
        SchoolTask.login(id, EncriptUtil.getMD5(pwd));
    }

    public LoginFragment() {
        super(R.layout.fragment_login);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void init() {
        ButterKnife.bind(this, view);
        idText.setInputFilter(new PhoneFilter());
        pwdText.setInputFilter(new PasswordFilter());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogin(LoginEvent event){
        if(event.isOk()){
            UserInfoWithToken user = GsonUtil.toUserInfoWithToken(event.getData());
            UserUtil.saveLoginUser(user);
            EventBus.getDefault().post(new LoginSuccessEvent());
            finish();
        }
        else{
            pwdText.clear();
            loginBtn.setEnabled(true);
            loginBtn.setText("登录");
            toastShort(event.getError());
        }
    }
}