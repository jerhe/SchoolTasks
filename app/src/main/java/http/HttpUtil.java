package http;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.edu.schooltask.LoginActivity;

import org.json.JSONException;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.List;

import base.BaseActivity;
import beans.User;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by 夜夜通宵 on 2017/5/6.
 */

public class HttpUtil {
    final static String LOGIN_URL = "http://192.168.191.1:8080/SchoolTaskServer/LoginController/login";
    final static String GET_CODE_URL = "http://192.168.191.1:8080/SchoolTaskServer/RegisterController/get_code";
    final static String REGISTER_FINISH_URL = "http://192.168.191.1:8080/SchoolTaskServer/RegisterController/finish";
    final static String RELEASE_ORDER_URL ="http://192.168.191.1:8080/SchoolTaskServer/OrderController/release";
    final static String CHECK_TOKEN_URL ="http://192.168.191.1:8080/SchoolTaskServer/TokenController/checkToken";
    final static String UPLOAD_ORDER_IMAGE_URL="http://192.168.191.1:8080/SchoolTaskServer/UploadImageController/orderImage";

    private static OkHttpClient client;
    private static OkHttpClient getInstance(){
        if(client == null){
            client = new OkHttpClient();
        }
        return client;
    }

    public static void login(String userId, String userPwd, HttpResponse httpResponse){
        RequestBody requestBody = new FormBody.Builder()
                .add("userid",userId)
                .add("userpwd",userPwd)
                .build();
        post(LOGIN_URL, requestBody, httpResponse);
    }

    public static void getCode(String userId, HttpResponse httpResponse){
        RequestBody requestBody = new FormBody.Builder()
                .add("userid",userId)
                .build();
        post(GET_CODE_URL, requestBody, httpResponse);
    }

    public static void registerFinish(String userId, String school, String name,
                                      String pwd, HttpResponse httpResponse){
        RequestBody requestBody = new FormBody.Builder()
                .add("id",userId)
                .add("school",school)
                .add("name",name)
                .add("pwd",pwd)
                .build();
        post(REGISTER_FINISH_URL, requestBody, httpResponse);
    }

    public static void release(String userId, String school, String title, String content, float cost,
                               int limitTime, List<String> paths, HttpResponse httpResponse){
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for(int i=0; i < paths.size(); i++) {
            File f=new File(paths.get(i));
            if(f!=null) {
                builder.addFormDataPart("image"+i, f.getName(), RequestBody.create(MediaType.parse("image/png"), f));
            }
        }
        builder.addFormDataPart("id",userId);
        builder.addFormDataPart("school", school);
        builder.addFormDataPart("title", title);
        builder.addFormDataPart("content", content);
        builder.addFormDataPart("cost", cost+"");
        builder.addFormDataPart("limittime", limitTime+"");
        MultipartBody requestBody = builder.build();
        post(RELEASE_ORDER_URL, requestBody, httpResponse);

    }



    public static void checkToken(String token, HttpResponse httpResponse){
        RequestBody requestBody = new FormBody.Builder()
                .add("token",token).build();
        post(CHECK_TOKEN_URL, requestBody, httpResponse);
    }


    /**
     * 所有需要身份验证的请求都需要先调用该接口来验证token
     * @param activity
     * @param user
     * @param httpCheckToken
     */
    public static void postWithToken(final BaseActivity activity, final User user, final HttpCheckToken httpCheckToken){
        checkToken(user.getToken(), new HttpResponse() {
            @Override
            public void handler() throws Exception {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (code) {
                            case 0:
                                httpCheckToken.isSuccess = true;
                                break;
                            case 1: //token过期
                                try {
                                    user.setToken(data.getString("token"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                user.updateAll("userid = ?", user.getUserId());
                                httpCheckToken.isSuccess = true;
                                break;
                            case 2: //令牌错误
                                DataSupport.deleteAll(User.class);
                                activity.toastShort("账号错误，请重新登录");
                                Intent intent = new Intent(activity, LoginActivity.class);
                                activity.startActivity(intent);
                                break;
                            case -1: //连接服务器失败
                                activity.toastShort("连接失败,请检查网络");
                                break;
                        }
                        httpCheckToken.handler();
                    }
                });
            }
        });
    }

    /**
     * post 发送POST请求
     * @param url   请求地址
     * @param requestBody   请求体
     * @param httpResponse 返回体
     */
    public static void post(String url, RequestBody requestBody, HttpResponse httpResponse){
        Request request = new Request.Builder().url(url).post(requestBody).build();
        getInstance().newCall(request).enqueue(new HttpCallBack(httpResponse));
    }

    /**
     * 判断网络连接
     * @param context
     * @return 有网络返回true,无返回false
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
}
