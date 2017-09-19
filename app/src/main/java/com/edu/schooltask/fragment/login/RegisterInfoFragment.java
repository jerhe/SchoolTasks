package com.edu.schooltask.fragment.login;

import android.text.TextUtils;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.beans.UserInfoWithToken;
import com.edu.schooltask.event.LoginEvent;
import com.edu.schooltask.event.RegisterNextEvent;
import com.edu.schooltask.filter.NameFilter;
import com.edu.schooltask.filter.PasswordFilter;
import com.edu.schooltask.filter.SchoolFilter;
import com.edu.schooltask.other.SchoolAutoComplement;
import com.edu.schooltask.base.BaseFragment;
import com.edu.schooltask.view.InputTextView;
import com.edu.schooltask.utils.EncriptUtil;
import com.edu.schooltask.utils.GsonUtil;
import com.edu.schooltask.utils.KeyBoardUtil;
import com.edu.schooltask.utils.StringUtil;
import com.edu.schooltask.utils.UserUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import server.api.client.UserClient;
import server.api.result.Result;

/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class RegisterInfoFragment extends BaseFragment {
    @BindView(R.id.ri_school) InputTextView schoolText;
    @BindView(R.id.ri_name) InputTextView nameText;
    @BindView(R.id.ri_pwd) InputTextView pwdText;
    @BindView(R.id.ri_finish) TextView finishBtn;

    @OnClick(R.id.ri_finish)
    public void register(){
        String school = schoolText.getText();
        String name = nameText.getText();
        String pwd = pwdText.getText();

        int emptyIndex = StringUtil.isEmpty(school, name, pwd);
        if(emptyIndex != -1){
            String[] strings = {"学校", "姓名", "密码"};
            toastShort("请输入" + strings[emptyIndex]);
            return;
        }
        if(StringUtil.checkLength(pwd, 6)){
            toastShort("密码长度至少为6位");
            return;
        }

        KeyBoardUtil.hideKeyBoard(getActivity());
        if(TextUtils.isEmpty(id)){
            toastShort("发生错误");
            return;
        }
        UserClient.register(registerResult, id, name, school, EncriptUtil.getMD5(pwd));
    }

    private Result registerResult = new Result() {
        @Override
        public void onResponse(int id) {

        }

        @Override
        public void onSuccess(int id, Object data) {
            UserInfoWithToken registerUser = GsonUtil.toUserInfoWithToken(data);
            UserUtil.saveLoginUser(registerUser);
            EventBus.getDefault().post(new LoginEvent());
            toastShort("注册成功");
            //openActivity(SetPayPwdActivity.class);  //注册成功进入设置支付密码界面
            finish();
        }

        @Override
        public void onFailed(int id, int code, String error) {
            toastShort(error);
        }
    };

    private String id;

    public RegisterInfoFragment() {
        super(R.layout.fragment_register_info);
    }

    @Override
    protected void init(){
        //设置过滤
        schoolText.setInputFilter(new SchoolFilter());
        nameText.setInputFilter(new NameFilter());
        pwdText.setInputFilter(new PasswordFilter());
        schoolText.getInputText().addTextChangedListener(
                new SchoolAutoComplement(schoolText.getInputText(), mDataCache.getSchool()));

        //限制
        schoolText.setText("莆田学院");
        schoolText.setInputEnable(false);
    }

    //下一步事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void registerNextEvent(RegisterNextEvent event){
        this.id = event.getId();
    }
}
