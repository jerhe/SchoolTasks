package fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.edu.schooltask.R;

import org.json.JSONException;
import org.litepal.crud.DataSupport;

import beans.User;
import http.HttpResponse;
import http.HttpUtil;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import utils.TextUtil;
import view.InputText;

/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class LoginFragment extends Fragment {
    private View view;

    private Button loginBtn;
    private InputText idText;
    private InputText pwdText;


    public LoginFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null != view) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (null != parent) {
                parent.removeView(view);
            }
        } else {
            view = inflater.inflate(R.layout.fragment_login,container,false);
            init();
        }
        return view;
    }

    private void init(){
        idText = (InputText) view.findViewById(R.id.login_id);
        pwdText = (InputText) view.findViewById(R.id.login_pwd);
        loginBtn = (Button) view.findViewById(R.id.login_login_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               login();
            }
        });
        idText.setInputFilter(0);
        pwdText.setInputFilter(3);
    }

    private void login(){
        if(!"登录".equals(loginBtn.getText()))return;
        final String id = idText.getText();
        String pwd = pwdText.getText();
        if(id.length() == 0){
            TextUtil.toast(getContext(), "请输入手机号");
            return;
        }
        if(pwd.length() == 0){
            TextUtil.toast(getContext(), "请输入密码");
            return;
        }
        if(pwd.length() < 6){
            TextUtil.toast(getContext(), "请输入正确的密码");
            return;
        }
        loginBtn.setText("登录中...");
        HttpUtil.login(id, TextUtil.getMD5(pwd), new HttpResponse() {
            @Override
            public void handler() throws Exception {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loginBtn.setText("登录");
                        switch (code){
                            case 0:
                                DataSupport.deleteAll(User.class);  //删除所有本地用户
                                User user = User.jsonObjectToUser(data);
                                user.save();
                                getActivity().finish();
                                break;
                            case 1:
                                TextUtil.toast(getContext(), "手机号未注册");
                                break;
                            case 2:
                                TextUtil.toast(getContext(), "密码错误");
                                break;
                            case -1:
                                TextUtil.toast(getContext(), "连接失败，请检查网络");
                                break;
                            default:
                                TextUtil.toast(getContext(), "登录失败");
                                break;
                        }
                    }
                });
            }
        });
    }
}
