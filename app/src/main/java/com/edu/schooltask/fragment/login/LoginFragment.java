package com.edu.schooltask.fragment.login;

import android.widget.Button;

import com.edu.schooltask.R;
import com.edu.schooltask.base.BaseFragment;
import com.edu.schooltask.beans.UserInfo;
import com.edu.schooltask.event.LoginSuccessEvent;
import com.edu.schooltask.filter.PasswordFilter;
import com.edu.schooltask.filter.PhoneFilter;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.KeyBoardUtil;
import com.edu.schooltask.utils.StringUtil;
import com.edu.schooltask.utils.UserUtil;
import com.edu.schooltask.view.Inputtextview.InputTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import server.api.SchoolTask;
import server.api.login.LoginEvent;

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
        SchoolTask.login(id, StringUtil.getMD5(pwd));
    }

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
        ButterKnife.bind(this, view);
        idText.setInputFilter(new PhoneFilter());
        pwdText.setInputFilter(new PasswordFilter());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogin(LoginEvent event){
        if(event.isOk()){
            UserInfo user = GsonUtil.toUserInfo(event.getData());
            UserUtil.saveLoginUser(user);
            EventBus.getDefault().post(new LoginSuccessEvent());
            finish();
        }
        else{
            pwdText.clear();
            loginBtn.setText("登录");
            loginBtn.setEnabled(true);
            toastShort(event.getError());
        }
    }
}
