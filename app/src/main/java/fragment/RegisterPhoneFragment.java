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
import android.widget.TextView;

import com.edu.schooltask.R;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class RegisterPhoneFragment extends Fragment {
    private View view;
    private Button nextBtn;
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
        nextBtn = (Button) view.findViewById(R.id.rp_next);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(2);
            }
        });
        getCodeBtn = (Button) view.findViewById(R.id.rp_get_code);
        //发送验证码事件
        getCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 发送验证码

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
}
