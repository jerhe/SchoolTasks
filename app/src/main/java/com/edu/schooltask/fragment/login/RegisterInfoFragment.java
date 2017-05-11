package com.edu.schooltask.fragment.login;

import android.view.View;
import android.widget.Button;

import com.edu.schooltask.activity.LoginActivity;
import com.edu.schooltask.R;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import com.edu.schooltask.base.BaseActivity;
import com.edu.schooltask.base.BaseFragment;
import com.edu.schooltask.beans.User;
import com.edu.schooltask.event.RegisterFinishEvent;
import com.edu.schooltask.http.HttpUtil;
import com.edu.schooltask.utils.KeyBoardUtil;
import com.edu.schooltask.utils.TextUtil;
import com.edu.schooltask.view.InputText;

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
    protected void init(){
        schoolText = (InputText) view.findViewById(R.id.ri_school);
        nameText = (InputText) view.findViewById(R.id.ri_name);
        pwdText = (InputText) view.findViewById(R.id.ri_pwd);
        finishBtn = (Button) view.findViewById(R.id.ri_finish);

        //设置过滤
        schoolText.setInputFilter(1);
        nameText.setInputFilter(2);
        pwdText.setInputFilter(3);

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerFinish();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRegisterFinish(RegisterFinishEvent event){
        if (event.isOk()){
            JSONObject userJSON = event.getData();
            User user = User.jsonObjectToUser(userJSON);
            toastShort("注册成功");
            mDataCache.saveUser(user);
            KeyBoardUtil.hideKeyBoard(getActivity());
            finish();
        }
        else{
            toastShort(event.getError());
        }
    }

    private void registerFinish(){
        if(!"完成".equals(finishBtn.getText()))return;
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
        finishBtn.setText("正在提交请求...");
        final String id = ((LoginActivity)getActivity()).registerId;
        HttpUtil.registerFinish(id, school, name, TextUtil.getMD5(pwd));
    }
}
