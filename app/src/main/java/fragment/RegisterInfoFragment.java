package fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.edu.schooltask.LoginActivity;
import com.edu.schooltask.R;

import org.json.JSONException;

import http.HttpResponse;
import http.HttpUtil;
import utils.TextUtil;
import view.InputText;

/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class RegisterInfoFragment extends Fragment {
    private View view;
    private InputText schoolText;
    private InputText nameText;
    private InputText pwdText;
    private Button finishBtn;

    public RegisterInfoFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null != view) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (null != parent) {
                parent.removeView(view);
            }
        } else {
            view = inflater.inflate(R.layout.fragment_register_info,container,false);
            init();
        }
        return view;
    }

    private void init(){
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
                finish();
            }
        });

    }

    private void finish(){
        final String school = schoolText.getText();
        String name = nameText.getText();
        String pwd = pwdText.getText();
        if(school.length() == 0){
            TextUtil.toast(getContext(), "请输入学校");
            return;
        }
        if(name.length() == 0){
            TextUtil.toast(getContext(), "请输入昵称");
            return;
        }
        if(pwd.length() == 0){
            TextUtil.toast(getContext(), "请输入密码");
            return;
        }
        if(pwd.length() < 6){
            TextUtil.toast(getContext(), "密码长度至少为6位");
            return;
        }

        String id = ((LoginActivity)getActivity()).registerId;
        HttpUtil.registerFinish(id, school, name, TextUtil.getMD5(pwd), new HttpResponse() {
            @Override
            public void handler() throws Exception {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (code){
                            case 0:
                                try {
                                    String token = data.getString("token");
                                    TextUtil.toast(getContext(), "注册成功" + token);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case 1:
                                TextUtil.toast(getContext(), "昵称已存在");
                                break;
                            case -1:
                                TextUtil.toast(getContext(), "发送失败,请检查网络");
                                break;
                            default:
                                TextUtil.toast(getContext(), "注册失败");
                                break;
                        }
                    }
                });
            }
        });
    }
}
