package http;

import okhttp3.FormBody;
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
}
