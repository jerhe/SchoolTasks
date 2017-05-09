package fragment.login;

import android.view.View;
import android.widget.Button;

import com.edu.schooltask.R;

import org.litepal.crud.DataSupport;

import base.BaseFragment;
import beans.User;
import http.HttpResponse;
import http.HttpUtil;
import utils.KeyBoardUtil;
import utils.TextUtil;
import view.InputText;

/**
 * Created by 夜夜通宵 on 2017/5/3.
 */

public class LoginFragment extends BaseFragment {
    private Button loginBtn;
    private InputText idText;
    private InputText pwdText;

    public LoginFragment() {
        super(R.layout.fragment_login);
    }

    @Override
    protected void init() {
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
            toastShort("请输入手机号");
            return;
        }
        if(pwd.length() == 0){
            toastShort("请输入密码");
            return;
        }
        if(pwd.length() < 6){
            toastShort("请输入正确的密码");
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
                                KeyBoardUtil.hideKeyBoard(getActivity());
                                getActivity().finish();
                                break;
                            case 1:
                                toastShort("手机号未注册");
                                break;
                            case 2:
                                toastShort("密码错误");
                                break;
                            case -1:
                                toastShort("连接失败，请检查网络");
                                break;
                            default:
                                toastShort("登录失败");
                        }
                    }
                });
            }
        });
    }
}
