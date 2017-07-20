package com.edu.schooltask.fragment.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;

import com.edu.schooltask.R;
import com.edu.schooltask.activity.LoginActivity;
import com.edu.schooltask.activity.SetPayPwdActivity;
import com.edu.schooltask.base.BaseFragment;
import com.edu.schooltask.beans.User;
import com.edu.schooltask.beans.UserBaseInfo;
import com.edu.schooltask.event.LoginSuccessEvent;
import com.edu.schooltask.utils.KeyBoardUtil;
import com.edu.schooltask.utils.TextUtil;
import com.edu.schooltask.view.InputText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import server.api.SchoolTask;
import server.api.register.RegisterEvent;

/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class RegisterInfoFragment extends BaseFragment {
    private InputText schoolText;
    private InputText nameText;
    private InputText pwdText;
    private Button finishBtn;

    public RegisterInfoFragment() {
        super(R.layout.fragment_register_info);
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
    protected void init(){
        schoolText = getView(R.id.ri_school);
        nameText = getView(R.id.ri_name);
        pwdText = getView(R.id.ri_pwd);
        finishBtn = getView(R.id.ri_finish);

        //设置过滤
        schoolText.setInputFilter(1);
        nameText.setInputFilter(2);
        pwdText.setInputFilter(3);

        TextUtil.setSchoolWatcher(getContext(), schoolText.getInputText(), mDataCache);

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRegister(RegisterEvent event){
        if (event.isOk()){
            User registerUser = new Gson().fromJson(new Gson().toJson(event.getData()), new TypeToken<User>(){}.getType());
            mDataCache.saveUser(registerUser);
            EventBus.getDefault().post(new LoginSuccessEvent());
            toastShort("注册成功");
            openActivity(SetPayPwdActivity.class);  //注册成功进入设置支付密码界面
            finish();
        }
        else{
            finishBtn.setEnabled(true);
            toastShort(event.getError());
        }
    }

    private void register(){
        final String school = schoolText.getText();
        final String name = nameText.getText();
        String pwd = pwdText.getText();
        if(school.length() == 0){
            toastShort("请输入学校");
            return;
        }
        if(name.length() == 0){
            toastShort("请输入昵称");
            return;
        }
        if(pwd.length() == 0){
            toastShort("请输入密码");
            return;
        }
        if(pwd.length() < 6){
            toastShort("密码长度至少为6位");
            return;
        }
        finishBtn.setEnabled(false);
        KeyBoardUtil.hideKeyBoard(getActivity());
        final String id = ((LoginActivity)getActivity()).registerId;
        if(TextUtils.isEmpty(id)){
            toastShort("发生错误");
            return;
        }
        SchoolTask.register(id, name, school, pwd);
    }
}
