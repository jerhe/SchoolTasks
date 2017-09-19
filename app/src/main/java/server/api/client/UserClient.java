package server.api.client;

import server.api.Post;
import server.api.result.Result;

import static server.api.SchoolTask.SERVER;

/**
 * Created by 夜夜通宵 on 2017/9/12.
 */

public class UserClient {
    private final static String CHECK_REGISTER_CODE = SERVER + "user/checkRegisterCode";
    private final static String CHECK_LOGIN_CODE = SERVER + "user/checkLoginCode";
    private final static String REGISTER = SERVER + "user/register";
    private final static String LOGIN = SERVER + "user/login";
    private final static String GET_SELF_USER_INFO = SERVER + "user/getSelfUserInfo";
    private final static String UPDATE_USER_INFO = SERVER + "user/updateUserInfo";
    private final static String UPDATE_LOGIN_PWD = SERVER + "user/updateLoginPwd";
    private final static String GET_USER_HOME_PAGE_INFO = SERVER + "user/getUserHomePageInfo";
    private final static String GET_PERSONAL_CENTER_INFO = SERVER + "user/getPersonalCenterInfo";
    private final static String REFRESH_HEAD = SERVER + "user/refreshHead";
    private final static String REFRESH_BG = SERVER + "user/refreshBg";

    public final static int UPDATE_NAME = 0;
    public final static int UPDATE_SIGN = 1;
    public final static int UPDATE_SCHOOL = 2;
    public final static int UPDATE_SEX = 3;

    /**
     * 验证注册验证码
     * @param userId    用户名
     * @param code      验证码
     */
    public static void checkRegisterCode(Result result, String userId, String code){
        Post.newPost()
                .url(CHECK_REGISTER_CODE)
                .addParam("id", userId)
                .addParam("code", code)
                .result(result)
                .post();
    }

    /**
     * 验证登录验证码
     * @param userId    用户名
     * @param code      验证码
     */
    public static void checkLoginCode(Result result, String userId, String code){
        Post.newPost()
                .url(CHECK_LOGIN_CODE)
                .addParam("id", userId)
                .addParam("code", code)
                .result(result)
                .post();
    }

    /**
     * 注册用户
     * @param userId    用户名
     * @param name      昵称
     * @param school    学校
     * @param pwd       登录密码
     */
    public static void register(Result result, String userId, String name, String school, String pwd){
        Post.newPost()
                .url(REGISTER)
                .addParam("id", userId)
                .addParam("name", name)
                .addParam("school", school)
                .addParam("pwd", pwd)
                .result(result)
                .post();
    }

    /**
     * 登录
     * @param userId    用户名
     * @param pwd       登录密码
     */
    public static void login(Result result, String userId, String pwd){
        Post.newPost()
                .url(LOGIN)
                .addParam("id", userId)
                .addParam("pwd", pwd)
                .result(result)
                .post();
    }

    /**
     *  获取用户自身信息
     */
    public static void getSelfUserInfo(Result result){
        Post.newPost()
                .url(GET_SELF_USER_INFO)
                .result(result)
                .post();
    }

    /**
     * 修改用户信息
     * @param value     值
     * @param type      修改类型
     */
    public static void updateUserInfo(Result result, String value, int type){
        Post.newPost()
                .url(UPDATE_USER_INFO)
                .addParam("value", value)
                .addParam("type", type)
                .result(result)
                .post();
    }

    /**
     * 修改登录密码
     * @param oldPwd    旧密码
     * @param newPwd    新密码
     */
    public static void updateLoginPwd(Result result, String oldPwd, String newPwd){
        Post.newPost()
                .url(UPDATE_LOGIN_PWD)
                .addParam("oldPwd", oldPwd)
                .addParam("newPwd", newPwd)
                .result(result)
                .post();
    }

    /**
     * 获取用户主页信息
     * @param name  用户昵称
     */
    public static void getUserHomePageInfo(Result result, String name){
        Post.newPost()
                .url(GET_USER_HOME_PAGE_INFO)
                .addParam("name", name)
                .result(result)
                .post();
    }

    /**
     * 获取用户个人中心信息
     */
    public static void getPersonalCenterInfo(Result result){
        Post.newPost()
                .url(GET_PERSONAL_CENTER_INFO)
                .result(result)
                .post();
    }

    /**
     * 修改用户头像
     * @param head  用户头像
     */
    public static void refreshHead(Result result, String head){
        Post.newPost()
                .url(REFRESH_HEAD)
                .addParam("head", head)
                .result(result)
                .post();
    }

    /**
     * 修改用户背景
     * @param bg    用户背景
     */
    public static void refreshBg(Result result, String bg){
        Post.newPost()
                .url(REFRESH_BG)
                .addParam("bg", bg)
                .result(result)
                .post();
    }
}
