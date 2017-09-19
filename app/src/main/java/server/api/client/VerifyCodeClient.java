package server.api.client;

import server.api.Post;
import server.api.result.Result;

import static server.api.SchoolTask.SERVER;

/**
 * 验证码请求类
 * Created by 夜夜通宵 on 2017/9/12.
 */

public class VerifyCodeClient {
    private final static String GET_REGISTER_CODE = SERVER + "verifyCode/getRegisterCode";
    private final static String GET_LOGIN_CODE = SERVER + "verifyCode/getLoginCode";

    /**
     * 注册时获取验证码
     * @param userId    用户名
     */
    public static void getRegisterCode(Result result, String userId){
        Post.newPost()
                .url(GET_REGISTER_CODE)
                .addParam("id", userId)
                .result(result)
                .post();
    }

    /**
     * 登陆时获取验证码
     * @param userId    用户名
     */
    public static void getLoginCode(Result result, String userId){
        Post.newPost()
                .url(GET_LOGIN_CODE)
                .addParam("id", userId)
                .result(result)
                .post();
    }
}
