package fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.edu.schooltask.LoginActivity;
import com.edu.schooltask.R;

import org.json.JSONException;

import java.util.Timer;
import java.util.TimerTask;

import beans.User;
import http.HttpResponse;
import http.HttpUtil;
import utils.TextUtil;
import view.InputText;

/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class RegisterPhoneFragment extends Fragment {
    private View view;
    private Button nextBtn;
    private InputText idText;
    private Button getCodeBtn;
    private ViewPager viewPager;

    public RegisterPhoneFragment(){}

    @SuppressLint("ValidFragment")
    public RegisterPhoneFragment(ViewPager viewPager) {
        this.viewPager = viewPager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null != view) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (null != parent) {
                parent.removeView(view);
            }
        } else {
            view = inflater.inflate(R.layout.fragment_register_phone,container,false);
            init();
        }
        return view;
    }

    private void init(){
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

    /**
     * getCode 获取验证码
     */
    public void getCode(){
        String id = idText.getText();
        if(id.length() != 11){
            TextUtil.toast(getContext(), "请输入正确的手机号");
            return;
        }
        //发送请求
        HttpUtil.getCode(id, new HttpResponse() {
            @Override
            public void handler() throws Exception {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (code){
                            case 0: //获取成功
                                startCoundDown();
                                TextUtil.toast(getContext(), "验证码已经通过短信发送至您的手机");
                                break;
                            case 1: //用户已存在
                                TextUtil.toast(getContext(), "用户已存在");
                                break;
                            default:    //获取失败
                                TextUtil.toast(getContext(), "获取验证码失败");
                        }
                    }
                });
            }
        });
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
