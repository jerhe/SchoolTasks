package com.edu.schooltask.ui.fragment.login;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.widget.Button;

import com.edu.schooltask.R;

import java.util.Timer;
import java.util.TimerTask;

import com.edu.schooltask.ui.base.BaseFragment;
import com.edu.schooltask.event.RegisterNextEvent;
import com.edu.schooltask.filter.NumberFilter;
import com.edu.schooltask.filter.PhoneFilter;
import com.edu.schooltask.utils.StringUtil;
import com.edu.schooltask.ui.view.Inputtextview.InputTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import server.api.SchoolTask;
import server.api.register.CheckCodeEvent;
import server.api.register.GetCodeEvent;

/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class RegisterPhoneFragment extends BaseFragment{
    @BindView(R.id.rp_next) Button nextBtn;
    @BindView(R.id.rp_id)
    InputTextView idText;
    @BindView(R.id.rp_code)
    InputTextView codeText;
    @BindView(R.id.rp_get_code) Button getCodeBtn;

    @OnClick(R.id.rp_get_code)
    public void getCode(){
        String id = idText.getText();
        if(!StringUtil.equalLength(id, 11)){
            toastShort("请输入正确的手机号");
            return;
        }
        getCodeBtn.setEnabled(false);
        SchoolTask.getCode(id);
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
        SchoolTask.checkCode(id, code); //判断验证码
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
    public void onGetCode(GetCodeEvent event){
        if(event.isOk()){
            startCountDown();   //开始倒计时
            toastShort("验证码已经通过短信发送至您的手机");
        }
        else{
            getCodeBtn.setEnabled(true);
            toastShort(event.getError());
        }
    }

    //判断验证码事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCheckCode(CheckCodeEvent event){
        if(event.isOk()){   //验证码正确跳转到下一步
            EventBus.getDefault().post(new RegisterNextEvent((String)event.getData()));
            viewPager.setCurrentItem(2);
        }
        else{
            nextBtn.setEnabled(true);
            toastShort(event.getError());
        }
    }

    /**
     * countDown 倒计时，用于验证码等待时间倒计时
     * @param count 当前倒计时秒数
     */
    private void countDown(final int count){
        getCodeBtn.post(new Runnable() {
            @Override
            public void run() {
                getCodeBtn.setText(count+"秒后重试");
            }
        });
    }

    public void startCountDown(){
        //发送成功 按钮无效，进入倒计时
        getCodeBtn.setEnabled(false);
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            int second = 60;
            @Override
            public void run() {
                countDown(second--);
                if(second == 0) {   //倒计时结束
                    timer.cancel();
                    getCodeBtn.post(new Runnable() {
                        @Override
                        public void run() {
                            getCodeBtn.setEnabled(true);
                            getCodeBtn.setText("获取验证码");
                        }
                    });
                }
            }
        },0,1000);
    }
}
