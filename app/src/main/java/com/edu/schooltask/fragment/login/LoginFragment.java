package com.edu.schooltask.fragment.login;

import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.beans.UserInfoWithToken;
import com.edu.schooltask.event.LoginEvent;
import com.edu.schooltask.filter.CodeFilter;
import com.edu.schooltask.filter.PasswordFilter;
import com.edu.schooltask.filter.PhoneFilter;
import com.edu.schooltask.base.BaseFragment;
import com.edu.schooltask.view.InputTextView;
import com.edu.schooltask.utils.EncriptUtil;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.KeyBoardUtil;
import com.edu.schooltask.utils.StringUtil;
import com.edu.schooltask.utils.UserUtil;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import server.api.client.UserClient;
import server.api.client.VerifyCodeClient;
import server.api.result.Result;

/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class LoginFragment extends BaseFragment{

    @BindView(R.id.login_id) InputTextView idText;
    @BindView(R.id.login_pwd) InputTextView pwdText;
    @BindView(R.id.login_login_btn) TextView loginBtn;
    @BindView(R.id.login_switch_btn) TextView switchLoginBtn;
    @BindView(R.id.login_code) InputTextView codeText;
    @BindView(R.id.login_get_code) TextView getCodeBtn;
    @BindView(R.id.login_pwd_layout) RelativeLayout pwdLayout;
    @BindView(R.id.login_sms_layout) RelativeLayout smsLayout;

    @OnClick(R.id.login_login_btn)
    public void login(){
        String id = idText.getText();
        if(isPwdLogin){
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
            UserClient.login(loginResult, id, EncriptUtil.getMD5(pwd));
        }
        else{
            String code = codeText.getText();
            int emptyIndex = StringUtil.isEmpty(id, code);
            if(emptyIndex != -1){
                String[] strings = {"手机号", "验证码"};
                toastShort("请输入" + strings[emptyIndex]);
                return;
            }
            if(code.length() != 6){
                toastShort("验证码错误");
                return;
            }
            UserClient.checkLoginCode(loginResult, id, code);
        }
        KeyBoardUtil.hideKeyBoard(getActivity());   //隐藏输入法
        loginBtn.setEnabled(false);
    }

    @OnClick(R.id.login_switch_btn)
    public void switchLoginType(){
        if(pwdLayout.isShown()){
            switchLoginBtn.setText("密码登录");
            pwdLayout.setVisibility(View.GONE);
            pwdLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.left_out));
            smsLayout.setVisibility(View.VISIBLE);
            smsLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.right_in));
            isPwdLogin = false;
        }
        else{
            switchLoginBtn.setText("短信登录");
            smsLayout.setVisibility(View.GONE);
            smsLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.right_out));
            pwdLayout.setVisibility(View.VISIBLE);
            pwdLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.left_in));
            isPwdLogin = true;
        }
    }

    @OnClick(R.id.login_get_code)
    public void getLoginCode(){
        String id = idText.getText();
        if(id.length() == 0){
            toastShort("请输入手机号");
            return;
        }
        if(id.length() != 11){
            toastShort("请输入正确的手机号");
            return;
        }
        setGetCodeEnable(false);
        VerifyCodeClient.getLoginCode(getLoginCodeResult, id);
    }

    private Result loginResult = new Result() {
        @Override
        public void onResponse(int id) {

        }

        @Override
        public void onSuccess(int id, Object data) {
            UserInfoWithToken user = GsonUtil.toUserInfoWithToken(data);
            UserUtil.saveLoginUser(user);
            EventBus.getDefault().post(new LoginEvent());
            finish();
        }

        @Override
        public void onFailed(int id, int code, String error) {
            loginBtn.setEnabled(true);
            toastShort(error);
        }
    };
    private Result getLoginCodeResult = new Result() {
        @Override
        public void onResponse(int id) {

        }

        @Override
        public void onSuccess(int id, Object data) {
            toastShort(getString(R.string.code_success));
            setGetCodeEnable(false);
            new CountDownTimer(60000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if(getActivity() != null)
                        getCodeBtn.setText(millisUntilFinished / 1000 + getString(R.string.code_tip));
                    else
                        this.cancel();
                }

                @Override
                public void onFinish() {
                    getCodeBtn.setText("获取验证码");
                    setGetCodeEnable(true);
                }
            }.start();
        }

        @Override
        public void onFailed(int id, int code, String error) {
            setGetCodeEnable(true);
            toastShort(error);
        }
    };

    boolean isPwdLogin = true;

    public LoginFragment() {
        super(R.layout.fragment_login);
    }

    @Override
    protected void init() {
        idText.setInputFilter(new PhoneFilter());
        pwdText.setInputFilter(new PasswordFilter());
        codeText.setInputFilter(new CodeFilter());
    }

    private void setGetCodeEnable(boolean enable){
        getCodeBtn.setEnabled(enable);
        if(enable){
            getCodeBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        else{
            getCodeBtn.setTextColor(getResources().getColor(R.color.hintColor));
        }
    }
}
