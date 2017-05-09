package fragment.login;

import android.view.View;
import android.widget.Button;

import com.edu.schooltask.LoginActivity;
import com.edu.schooltask.R;

import org.json.JSONException;
import org.litepal.crud.DataSupport;

import base.BaseActivity;
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
                finish();
            }
        });
    }

    private void finish(){
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
        HttpUtil.registerFinish(id, school, name, TextUtil.getMD5(pwd), new HttpResponse() {
            @Override
            public void handler() throws Exception {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finishBtn.setText("完成");
                        switch (code){
                            case 0:
                                try {
                                    String token = data.getString("token");
                                    toastShort("注册成功");
                                    DataSupport.deleteAll(User.class);
                                    //以下是自动登录操作
                                    //存储用户到本地数据库
                                    User user = new User(id, token, name, school, "", -1, "", 1);
                                    user.save();
                                    //设置当前用户为注册用户
                                    ((BaseActivity)getActivity()).user = user;
                                    KeyBoardUtil.hideKeyBoard(getActivity());
                                    getActivity().finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                break;
                            case 1:
                                toastShort("昵称已存在");
                                break;
                            case -1:
                                toastShort("发送失败,请检查网络");
                                break;
                            default:
                                toastShort("注册失败");
                                break;
                        }
                    }
                });
            }
        });
    }
}
