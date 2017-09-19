package com.edu.schooltask.fragment.login;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.edu.schooltask.R;
import com.edu.schooltask.event.RegisterNextEvent;
import com.edu.schooltask.filter.CodeFilter;
import com.edu.schooltask.filter.PhoneFilter;
import com.edu.schooltask.base.BaseFragment;
import com.edu.schooltask.view.InputTextView;
import com.edu.schooltask.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import server.api.client.UserClient;
import server.api.client.VerifyCodeClient;
import server.api.result.Result;

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
        setGetCodeEnable(false);
        VerifyCodeClient.getRegisterCode(getRegisterCodeResult, id);
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
        UserClient.checkRegisterCode(checkRegisterCodeResult, id, code); //判断验证码
    }

    private Result checkRegisterCodeResult = new Result() {
        @Override
        public void onResponse(int id) {

        }

        @Override
        public void onSuccess(int id, Object data) {
            String userId = (String)data;
            EventBus.getDefault().post(new RegisterNextEvent(userId));
            viewPager.setCurrentItem(2);
        }

        @Override
        public void onFailed(int id, int code, String error) {
            nextBtn.setEnabled(true);
            toastShort(error);
        }
    };
    private Result getRegisterCodeResult = new Result() {
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

    private ViewPager viewPager;

    public RegisterPhoneFragment(){}

    @SuppressLint("ValidFragment")
    public RegisterPhoneFragment(ViewPager viewPager) {
        super(R.layout.fragment_register_phone);
        this.viewPager = viewPager;
    }

    @Override
    protected void init(){
        idText.setInputFilter(new PhoneFilter());    //设置过滤
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
