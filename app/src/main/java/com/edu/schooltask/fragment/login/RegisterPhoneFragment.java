package com.edu.schooltask.fragment.login;

import android.annotation.SuppressLint;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.edu.schooltask.activity.LoginActivity;
import com.edu.schooltask.R;

import java.util.Timer;
import java.util.TimerTask;

import com.edu.schooltask.base.BaseFragment;
import com.edu.schooltask.event.GetCodeEvent;
import com.edu.schooltask.http.HttpUtil;
import com.edu.schooltask.view.InputText;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class RegisterPhoneFragment extends BaseFragment {
    private Button nextBtn;
    private InputText idText;
    private Button getCodeBtn;
    private ViewPager viewPager;

    public RegisterPhoneFragment(){
    }

    @SuppressLint("ValidFragment")
    public RegisterPhoneFragment(ViewPager viewPager) {
        super(R.layout.fragment_register_phone);
        this.viewPager = viewPager;
    }

    @Override
    protected void init(){
        getCodeBtn = (Button) view.findViewById(R.id.rp_get_code);
        nextBtn = (Button) view.findViewById(R.id.rp_next);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 判断验证码正确
                ((LoginActivity)getActivity()).registerId = idText.getText();
                viewPager.setCurrentItem(2);
            }
        });

        idText = (InputText) view.findViewById(R.id.rpf_id);
        idText.setInputFilter(0);    //设置过滤

        //发送验证码事件
        getCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCode();
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetCode(GetCodeEvent event){
        if(event.isOk()){
            startCoundDown();
            toastShort("验证码已经通过短信发送至您的手机");
        }
        else{
            toastShort(event.getError());
        }
    }

    /**
     * getCode 获取验证码
     */
    public void getCode(){
        String id = idText.getText();
        if(id.length() != 11){
            toastShort("请输入正确的手机号");
            return;
        }
        HttpUtil.getCode(id);
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

    public void startCoundDown(){
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
