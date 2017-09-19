package com.edu.schooltask.activity;

import com.edu.schooltask.R;
import com.edu.schooltask.event.LogoutEvent;
import com.edu.schooltask.filter.PasswordFilter;
import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.view.InputTextView;
import com.edu.schooltask.utils.EncriptUtil;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import server.api.client.UserClient;
import server.api.result.Result;

public class UpdateLoginPwdActivity extends BaseActivity {
    @BindView(R.id.ulp_old) InputTextView oldText;
    @BindView(R.id.ulp_new) InputTextView newText;
    @BindView(R.id.ulp_new_again) InputTextView newAgainText;

    @OnClick(R.id.ulp_btn)
    public void update(){
        String oldPwd = oldText.getText();
        String newPwd = newText.getText();
        String newAgainPwd = newAgainText.getText();
        if(oldPwd.length() == 0){
            toastShort(s(R.string.input_tip, oldText.getName()));
            return;
        }
        if(newPwd.length() == 0){
            toastShort(s(R.string.input_tip, newText.getName()));
            return;
        }
        if(newAgainPwd.length() == 0){
            toastShort(s(R.string.input_again_tip, newText.getName()));
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
        UserClient.updateLoginPwd(updateLoginPwdResult, EncriptUtil.getMD5(oldPwd), EncriptUtil.getMD5(newPwd));
    }

    private Result updateLoginPwdResult = new Result(true) {
        @Override
        public void onResponse(int id) {

        }

        @Override
        public void onSuccess(int id, Object data) {
            toastShort("修改成功，请重新登录");
            EventBus.getDefault().post(new LogoutEvent());
            remainHome();
            openActivity(LoginActivity.class);
        }

        @Override
        public void onFailed(int id, int code, String error) {
            toastShort(error);
        }
    };

    @Override
    public int getLayout() {
        return R.layout.activity_update_login_pwd;
    }

    @Override
    public void init() {
        PasswordFilter filter = new PasswordFilter();
        oldText.setInputFilter(filter);
        newText.setInputFilter(filter);
        newAgainText.setInputFilter(filter);
    }

}
