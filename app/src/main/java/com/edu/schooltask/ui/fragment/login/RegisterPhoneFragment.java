package com.edu.schooltask.ui.fragment.login;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.event.RegisterNextEvent;
import com.edu.schooltask.filter.NumberFilter;
import com.edu.schooltask.filter.PhoneFilter;
import com.edu.schooltask.ui.base.BaseFragment;
import com.edu.schooltask.ui.view.Inputtextview.InputTextView;
import com.edu.schooltask.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import server.api.SchoolTask;
import server.api.event.user.register.CheckRegisterCodeEvent;
import server.api.event.user.register.GetRegisterCodeEvent;

/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class RegisterPhoneFragment extends BaseFragment{
    @BindView(R.id.rp_next) TextView nextBtn;
    @BindView(R.id.rp_id) InputTextView idText;
    @BindView(R.id.rp_code) InputTextView codeText;
    @BindView(R.id.rp_get_code) TextView getCodeBtn;

    @OnClick(R.id.rp_get_code)
    public void getCode(){
        String id = idText.getText();
        if(!StringUtil.equalLength(id, 11)){
            toastShort("请输入正确的手机号");
            return;
        }
        getCodeBtn.setEnabled(false);
        SchoolTask.getRegisterCode(id);
    }
    @OnClick(R.id.rp_next)
    public void next(){
        String id = idText.getText();
        String code = codeText.getText();
        int emptyIndex = StringUtil.isEmpty(id,code);
        if(emptyIndex != -1){
            String[] strings = {"手机号", "验证码"};
            toastShort("请输入" + strings[emptyIndex]);
            return;
        }
        if(!StringUtil.equalLength(code, 6)){
            toastShort("验证码为6位数字");
            return;
        }
        nextBtn.setEnabled(false);
        SchoolTask.checkRegisterCode(id, code); //判断验证码
    }

    private ViewPager viewPager;

    public RegisterPhoneFragment(){}

    @SuppressLint("ValidFragment")
    public RegisterPhoneFragment(ViewPager viewPager) {
        super(R.layout.fragment_register_phone);
        this.viewPager = viewPager;
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
        ButterKnife.bind(this, view);
        idText.setInputFilter(new PhoneFilter());    //设置过滤
        codeText.setInputFilter(new NumberFilter());
    }

    //获取验证码事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetRegisterCode(GetRegisterCodeEvent event){
        if(event.isOk()){
            toastShort(getString(R.string.code_success));
            getCodeBtn.setEnabled(false);
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
                    getCodeBtn.setEnabled(true);
                }
            }.start();
        }
        else{
            getCodeBtn.setEnabled(true);
            toastShort(event.getError());
        }
    }

    //判断验证码事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCheckRegisterCode(CheckRegisterCodeEvent event){
        if(event.isOk()){   //验证码正确跳转到下一步
            String id = (String)event.getData();
            EventBus.getDefault().post(new RegisterNextEvent(id));
            viewPager.setCurrentItem(2);
        }
        else{
            nextBtn.setEnabled(true);
            toastShort(event.getError());
        }
    }
}
