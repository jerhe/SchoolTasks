package server.api.client;

import server.api.Post;
import server.api.result.Result;

import static server.api.SchoolTask.SERVER;

/**
 * Created by 夜夜通宵 on 2017/9/12.
 */

public class AccountClient {
    private final static String GET_MONEY = SERVER + "account/getMoney";
    private final static String SET_PAY_PWD = SERVER + "account/setPayPwd";
    private final static String UPDATE_PAY_PWD = SERVER + "account/updatePayPwd";
    private final static String GET_DETAIL = SERVER + "account/getDetailList";
    private final static String CHECK_PAY_PASSWORD = SERVER + "account/checkPayPassword";
    private final static String GET_RECHARGE_CODE = SERVER + "account/getRechargeCode";
    private final static String GET_ALI_USER_LIST = SERVER + "account/getAliUserList";
    private final static String DELETE_ALI_USER = SERVER + "account/deleteAliUser";

    /**
     * 查询余额
     */
    public static void getMoney(Result result){
        Post.newPost()
                .url(GET_MONEY)
                .result(result)
                .post();
    }

    /**
     * 设置支付密码
     * @param payPwd    支付密码
     */
    public static void setPayPwd(Result result, String payPwd){
        Post.newPost()
                .url(SET_PAY_PWD)
                .addParam("payPwd", payPwd)
                .result(result)
                .post();
    }

    /**
     * 修改支付密码
     * @param oldPayPwd 旧密码
     * @param newPayPwd 新密码
     */
    public static void updatePayPwd(Result result, String oldPayPwd, String newPayPwd){
        Post.newPost()
                .url(UPDATE_PAY_PWD)
                .addParam("oldPayPwd", oldPayPwd)
                .addParam("newPayPwd", newPayPwd)
                .result(result)
                .post();
    }


    /**
     * 查询交易记录
     * @param page  页数
     */
    public static void getDetail(Result result, int page){
        Post.newPost()
                .url(GET_DETAIL)
                .addParam("page", page)
                .result(result)
                .post();
    }

    /**
     * 验证支付密码
     * @param payPassword
     */
    public static void checkPayPassword(Result result, String payPassword){
        Post.newPost()
                .url(CHECK_PAY_PASSWORD)
                .addParam("payPassword", payPassword)
                .result(result)
                .post();
    }

    /**
     * 获取充值代码
     * @param money
     */
    public static void getRechargeCode(Result result, int money){
        Post.newPost()
                .url(GET_RECHARGE_CODE)
                .addParam("money", money)
                .result(result)
                .post();
    }

    /**
     * 获取支付宝账号列表
     */
    public static void getAliUserList(Result result){
        Post.newPost()
                .url(GET_ALI_USER_LIST)
                .result(result)
                .post();
    }


    /**
     * 删除支付宝账号
     * @param id
     */
    public static void deleteAliUser(Result result, int id){
        Post.newPost()
                .url(DELETE_ALI_USER)
                .addParam("id", id)
                .result(result)
                .post();
    }
}
