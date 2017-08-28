package server.api;

import com.edu.schooltask.beans.TaskUploadKey;
import com.edu.schooltask.beans.UploadKey;
import com.edu.schooltask.beans.UserConfig;
import com.edu.schooltask.beans.UserInfoWithToken;
import com.edu.schooltask.utils.EncriptUtil;
import com.edu.schooltask.utils.UserUtil;
import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.zhy.http.okhttp.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import server.api.base.BaseTokenCallBack;
import server.api.friend.AgreeRequestEvent;
import server.api.friend.FriendRequestEvent;
import server.api.friend.GetFriendListEvent;
import server.api.friend.GetRequestListEvent;
import server.api.friend.RejectRequestEvent;
import server.api.qiniu.GetBGUploadKeyEvent;
import server.api.qiniu.GetHeadUploadKeyEvent;
import server.api.qiniu.GetTaskUploadKeyEvent;
import server.api.qiniu.UploadBGEvent;
import server.api.qiniu.UploadHeadEvent;
import server.api.qiniu.UploadTaskImageEvent;
import server.api.rong.GetTokenEvent;
import server.api.school.GetSchoolEvent;
import server.api.task.accept.AcceptTaskEvent;
import server.api.task.comment.GetTaskCommentListEvent;
import server.api.task.comment.GetTaskReplyListEvent;
import server.api.task.comment.NewTaskCommentEvent;
import server.api.task.get.GetSchoolTaskEvent;
import server.api.task.get.GetTaskInfoEvent;
import server.api.task.get.GetTaskListEvent;
import server.api.task.order.ChangeTaskOrderStateEvent;
import server.api.task.order.GetTaskOrderInfoEvent;
import server.api.task.order.GetTaskOrderListEvent;
import server.api.task.release.ReleaseTaskEvent;
import server.api.user.GetPersonalCenterInfoEvent;
import server.api.user.GetUserConfigEvent;
import server.api.user.GetUserHomePageInfoEvent;
import server.api.user.GetUserInfoEvent;
import server.api.user.UpdateLoginPwdEvent;
import server.api.user.UpdateUserInfoEvent;
import server.api.user.account.GetDetailEvent;
import server.api.user.account.GetMoneyEvent;
import server.api.user.account.RechargeEvent;
import server.api.user.account.SetPayPwdEvent;
import server.api.user.account.UpdatePaypwdEvent;
import server.api.user.login.LoginEvent;
import server.api.user.login.UnloginEvent;
import server.api.user.register.CheckCodeEvent;
import server.api.user.register.GetCodeEvent;
import server.api.user.register.RegisterEvent;
import server.api.voucher.GetAvailableVouchersEvent;

/**
 * Created by 夜夜通宵 on 2017/5/17.
 */

public class SchoolTask {
    private final static String SERVER = "http://192.168.191.1:8080/SchoolTask/";

    //VerifyCode
    private final static String GET_CODE = SERVER + "verifyCode/getCode";
    private final static String CHECK_CODE = SERVER + "verifyCode/checkCode";

    //User
    private final static String REGISTER = SERVER + "user/register";
    private final static String LOGIN = SERVER + "user/login";
    private final static String GET_USER_INFO = SERVER + "user/getUserInfo";
    private final static String UPDATE_USER_INFO = SERVER + "user/updateUserInfo";
    private final static String UPDATE_LOGIN_PWD = SERVER + "user/updateLoginPwd";
    private final static String GET_USER_HOME_PAGE_INFO = SERVER + "user/getUserHomePageInfo";
    private final static String GET_PERSONAL_CENTER_INFO = SERVER + "user/getPersonalCenterInfo";
    private final static String GET_USER_CONFIG = SERVER + "user/getUserConfig";
    private final static String UPDATE_USER_CONFIG = SERVER + "user/updateUserConfig";

    //Account
    private final static String GET_MONEY = SERVER + "account/getMoney";
    private final static String SET_PAY_PWD = SERVER + "account/setPayPwd";
    private final static String UPDATE_PAY_PWD = SERVER + "account/updatePayPwd";
    private final static String RECHARGE = SERVER + "account/recharge";
    private final static String GET_DETAIL = SERVER + "account/getDetailList";

    //Task
    private final static String RELEASE_TASK = SERVER + "task/release";
    private final static String GET_USER_ORDER_LIST = SERVER + "task/getUserOrderList";
    private final static String GET_TASK_LIST = SERVER + "task/getTaskList";
    private final static String ACCEPT_TASK = SERVER + "task/acceptTask";
    private final static String GET_TASK_INFO = SERVER + "task/getTaskInfo";
    private final static String GET_ORDER_INFO = SERVER + "task/getOrderInfo";
    private final static String CHANGE_ORDER_STATE = SERVER + "task/changeOrderState";
    private final static String COMMENT = SERVER + "task/comment";
    private final static String GET_COMMENT = SERVER + "task/getCommentList";
    private final static String GET_REPLY_LIST = SERVER + "task/getReplyList";
    private final static String SEARCH_TASK = SERVER + "task/searchTask";

    //School
    private final static String GET_SCHOOL = SERVER + "school/getSchoolList";

    //Qiniu
    private final static String GET_TASK_UPLOAD_KEY = SERVER + "qiniu/getTaskUploadKey";
    private final static String GET_HEAD_UPLOAD_KEY = SERVER + "qiniu/getHeadUploadKey";
    private final static String GET_BG_UPLOAD_KEY = SERVER + "qiniu/getBGUploadKey";
    private final static String QINIU_REFRESH = SERVER + "qiniu/refresh";

    //Voucher
    private final static String GET_AVAILABLE_VOUCHERS = SERVER + "voucher/getAvailableVoucherList";

    //Rong
    private final static String RONG_GET_TOKEN = SERVER + "rong/getToken";

    //Friend
    private final static String FRIEND_REQUEST = SERVER + "friend/request";
    private final static String GET_REQUEST_LIST = SERVER + "friend/getRequestList";
    private final static String GET_FRIEND_LIST = SERVER + "friend/getFriendList";
    private final static String AGREE_REQUEST = SERVER + "friend/agreeRequest";
    private final static String REJECT_REQUEST = SERVER + "friend/rejectRequest";

    public final static String TASK_IMAGE ="http://oqqzw04zt.bkt.clouddn.com/";
    public final static String HEAD ="http://oqqzw04zt.bkt.clouddn.com/head/";
    public final static String BG ="http://oqqzw04zt.bkt.clouddn.com/bg/";

    //------------------------------------------------------------------------------------

    static Configuration config;
    static UploadManager uploadManager;

    static TokenPost tokenPost;

    public static void init(){
        tokenPost = new TokenPost();
        config = new Configuration.Builder()
                .zone(Zone.zone2)
                .responseTimeout(20)
                .build();
        uploadManager = new UploadManager(config);
    }

    //--------------------------需要Token------------------------------------

    //查询余额
    public static void getMoney(){
        tokenPost.newPost()
                .url(GET_MONEY)
                .event(new GetMoneyEvent())
                .enqueue();
    }

    //获取七牛上传凭证
    public static void getTaskUploadKey(String payPwd){
        tokenPost.newPost()
                .url(GET_TASK_UPLOAD_KEY)
                .addParam("payPwd", payPwd)
                .event(new GetTaskUploadKeyEvent())
                .enqueue();
    }

    //上传任务图片
    public static void uploadTaskImage(TaskUploadKey taskUploadKey, List<File> images){
        int index = 0;
        for(File image : images){
            final int i = index;
            uploadManager.put(image, taskUploadKey.getOrderId() + "/" + index +".png", taskUploadKey.getKey(), new UpCompletionHandler() {
                @Override
                public void complete(String key, ResponseInfo info, JSONObject response) {
                    EventBus.getDefault().post(new UploadTaskImageEvent(i, info.isOK()));
                }
            }, null);
            index ++;
        }
    }


    //发布任务
    public static void releaseTask(String orderId, String school, String description, String content,
                                   BigDecimal cost, BigDecimal taskCost, int limitTime, String payPwd,
                                   long voucherId, int imageNum){
        tokenPost.newPost()
                .url(RELEASE_TASK)
                .addParam("orderId", orderId)
                .addParam("school", school)
                .addParam("description", description)
                .addParam("content",content)
                .addParam("cost", cost)
                .addParam("taskCost", taskCost)
                .addParam("limitTime", limitTime)
                .addParam("payPwd", payPwd)
                .addParam("voucherId", voucherId)
                .addParam("imageNum", imageNum)
                .event(new ReleaseTaskEvent())
                .enqueue();
    }

    //发表任务评论
    public static void comment(String orderId, long parentId, String toUserId, String comment){
        tokenPost.newPost()
                .url(COMMENT)
                .addParam("orderId", orderId)
                .addParam("parentId", parentId)
                .addParam("toUserId", toUserId)
                .addParam("comment", comment)
                .event(new NewTaskCommentEvent())
                .enqueue();
    }

    //获取用户订单
    public static void getUserOrderList(int type, int state, int sort, int page){
        tokenPost.newPost()
                .url(GET_USER_ORDER_LIST)
                .addParam("type", type)
                .addParam("state", state)
                .addParam("sort", sort)
                .addParam("page", page)
                .event(new GetTaskOrderListEvent())
                .enqueue();
    }


    //获取订单信息（订单页）
    public static void getOrderInfo(String orderId){
        tokenPost.newPost()
                .url(GET_ORDER_INFO)
                .addParam("orderId", orderId)
                .event(new GetTaskOrderInfoEvent())
                .enqueue();
    }

    //设置支付密码
    public static void setPayPwd(String payPwd){
        tokenPost.newPost()
                .url(SET_PAY_PWD)
                .addParam("payPwd", EncriptUtil.getMD5(payPwd))
                .event(new SetPayPwdEvent())
                .enqueue();
    }

    //接受任务
    public static void acceptTask(String orderId){
        tokenPost.newPost()
                .url(ACCEPT_TASK)
                .addParam("orderId", orderId)
                .event(new AcceptTaskEvent())
                .enqueue();
    }

    //改变任务状态
    public static void changeTaskOrderState(String orderId, int state){
        tokenPost.newPost()
                .url(CHANGE_ORDER_STATE)
                .addParam("orderId", orderId)
                .addParam("state", state)
                .event(new ChangeTaskOrderStateEvent())
                .enqueue();
    }

    //获取明细
    public static void getDetail(int page){
        tokenPost.newPost()
                .url(GET_DETAIL)
                .addParam("page", page)
                .event(new GetDetailEvent())
                .enqueue();
    }

    //获取七牛上传凭证
    public static void getHeadUploadKey(){
        tokenPost.newPost()
                .url(GET_HEAD_UPLOAD_KEY)
                .event(new GetHeadUploadKeyEvent())
                .enqueue();
    }

    //获取七牛上传凭证
    public static void getBGUploadKey(){
        tokenPost.newPost()
                .url(GET_BG_UPLOAD_KEY)
                .event(new GetBGUploadKeyEvent())
                .enqueue();
    }

    //更新用户信息
    public static void updateUserInfo(String value, int type){
        tokenPost.newPost()
                .url(UPDATE_USER_INFO)
                .addParam("value", value)
                .addParam("type", type)
                .event(new UpdateUserInfoEvent())
                .enqueue();
    }

    //修改密码
    public static void updateLoginPwd(String oldPwd, String newPwd){
        tokenPost.newPost()
                .url(UPDATE_LOGIN_PWD)
                .addParam("oldPwd", oldPwd)
                .addParam("newPwd", newPwd)
                .event(new UpdateLoginPwdEvent())
                .enqueue();
    }

    //获取用户个人中心信息
    public static void getPersonalCenterInfo(){
        tokenPost.newPost()
                .url(GET_PERSONAL_CENTER_INFO)
                .event(new GetPersonalCenterInfoEvent())
                .enqueue();
    }

    //修改支付密码
    public static void updatePayPwd(String oldPayPwd, String newPayPwd){
        tokenPost.newPost()
                .url(UPDATE_PAY_PWD)
                .addParam("oldPayPwd", oldPayPwd)
                .addParam("newPayPwd", newPayPwd)
                .event(new UpdatePaypwdEvent())
                .enqueue();
    }

    //获取用户信息
    public static void getUserInfo(){
        tokenPost.newPost()
                .url(GET_USER_INFO)
                .event(new GetUserInfoEvent())
                .enqueue();
    }


    //获取用户配置
    public static void getUserConfig(){
        tokenPost.newPost()
                .url(GET_USER_CONFIG)
                .event(new GetUserConfigEvent())
                .enqueue();
    }

    //充值
    public static void recharge(String orderId, BigDecimal money, String type){
        tokenPost.newPost()
                .url(RECHARGE)
                .addParam("orderId", orderId)
                .addParam("money", money)
                .addParam("type", type)
                .event(new RechargeEvent())
                .enqueue();
    }

    //获取用户可用代金券
    public static void getAvailableVouchers(){
        tokenPost.newPost()
                .url(GET_AVAILABLE_VOUCHERS)
                .event(new GetAvailableVouchersEvent())
                .enqueue();
    }

    //获取融云Token
    public static void rongGetToken(){
        tokenPost.newPost()
                .url(RONG_GET_TOKEN)
                .event(new GetTokenEvent())
                .enqueue();
    }

    //请求添加好友
    public static void friendRequest(String friendId){
        tokenPost.newPost()
                .url(FRIEND_REQUEST)
                .addParam("friendId", friendId)
                .event(new FriendRequestEvent())
                .enqueue();
    }

    //请求好友请求列表
    public static void getRequestList(){
        tokenPost.newPost()
                .url(GET_REQUEST_LIST)
                .event(new GetRequestListEvent())
                .enqueue();
    }

    //请求好友列表
    public static void getFriendList(){
        tokenPost.newPost()
                .url(GET_FRIEND_LIST)
                .event(new GetFriendListEvent())
                .enqueue();
    }

    //同意好友请求
    public static void agreeRequest(String friendId){
        tokenPost.newPost()
                .url(AGREE_REQUEST)
                .addParam("friendId", friendId)
                .event(new AgreeRequestEvent())
                .enqueue();
    }

    //同意好友请求
    public static void rejectRequest(String friendId){
        tokenPost.newPost()
                .url(REJECT_REQUEST)
                .addParam("friendId", friendId)
                .event(new RejectRequestEvent())
                .enqueue();
    }

    //用于判断用户已登录
    private static UserInfoWithToken getUser(){
        UserInfoWithToken user = UserUtil.getLoginUser();
        if(user != null) return user;
        else {
            EventBus.getDefault().post(new UnloginEvent());
            return null;
        }
    }

    /**
     * ---------------------------无需返回----------------------------------
     */

    //更新用户配置
    public static void updateUserConfig(UserConfig userConfig){
        UserInfoWithToken user = getUser();
        if(user != null){
            OkHttpUtils.post()
                    .url(UPDATE_USER_CONFIG)
                    .addHeader("token", user.getToken())
                    .addParams("message", userConfig.getMessage()+"")
                    .addParams("fans", userConfig.isFans()+"")
                    .addParams("comment", userConfig.isComment()+"")
                    .addParams("privateMessage", userConfig.isPrivateMessage()+"")
                    .build()
                    .execute(null);
        }
    }

    /**
     * ----------------------------QINIU----------------------------------
     */
    //上传头像
    public static void uploadHead(File head, UploadKey uploadKey){
        final UserInfoWithToken user = getUser();
        uploadManager.put(head, "head/" + user.getUserId() +".png", uploadKey.getKey(), new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                EventBus.getDefault().post(new UploadHeadEvent(info.isOK()));
                OkHttpUtils.post()
                        .url(QINIU_REFRESH)
                        .addParams("path","head/"+user.getUserId() +".png")
                        .build()
                        .execute(null);
            }
        }, null);
    }

    //上传背景
    public static void uploadBG(File bg, UploadKey uploadKey){
        final UserInfoWithToken user = getUser();
        uploadManager.put(bg, "bg/" + user.getUserId() +".png", uploadKey.getKey(), new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                EventBus.getDefault().post(new UploadBGEvent(info.isOK()));
                OkHttpUtils.post()
                        .url(QINIU_REFRESH)
                        .addParams("path","bg/"+user.getUserId() +".png")
                        .build()
                        .execute(null);
            }
        }, null);
    }

    /**
     * ----------------------------不需要Token----------------------------------
     */

    //登录
    public static void login(String userId, String pwd){
        Post.newPost()
                .url(LOGIN)
                .addParam("id", userId)
                .addParam("pwd", pwd)
                .event(new LoginEvent())
                .post();
    }

    //注册时获取验证码
    public static void getCode(String userId){
        Post.newPost()
                .url(GET_CODE)
                .addParam("id", userId)
                .event(new GetCodeEvent())
                .post();
    }

    //验证注册验证码
    public static void checkCode(String userId, String code){
        Post.newPost()
                .url(CHECK_CODE)
                .addParam("id", userId)
                .addParam("code", code)
                .addParam("type", 0 + "")
                .event(new CheckCodeEvent())
                .post();
    }

    //注册
    public static void register(String userId, String name, String school, String pwd){
        Post.newPost()
                .url(REGISTER)
                .addParam("id", userId)
                .addParam("name", name)
                .addParam("school", school)
                .addParam("pwd", EncriptUtil.getMD5(pwd))
                .event(new RegisterEvent())
                .post();
    }

    //根据学校获取任务列表
    public static void getSchoolTask(String school,int page){
        Post.newPost()
                .url(GET_TASK_LIST)
                .addParam("school",school)
                .addParam("page",page+"")
                .event(new GetSchoolTaskEvent())
                .post();
    }

    //获取任务评论
    public static void getTaskComment(String orderId, int page){
        Post.newPost()
                .url(GET_COMMENT)
                .addParam("orderId", orderId)
                .addParam("page", page+"")
                .event(new GetTaskCommentListEvent())
                .post();
    }

    //获取任务评论的回复
    public static void getTaskReplyList(String orderId, long parentId, int page){
        Post.newPost()
                .url(GET_REPLY_LIST)
                .addParam("orderId", orderId)
                .addParam("parentId", parentId+"")
                .addParam("page", page + "")
                .event(new GetTaskReplyListEvent())
                .post();
    }

    //获取任务列表
    public static void searchTask(String school, String description, String searchText,
                                   BigDecimal minCost, BigDecimal maxCost, int page){
        Post.newPost()
                .url(SEARCH_TASK)
                .addParam("school", school)
                .addParam("description", description)
                .addParam("searchText", searchText)
                .addParam("minCost", minCost + "")
                .addParam("maxCost", maxCost + "")
                .addParam("page", page+"")
                .event(new GetTaskListEvent())
                .post();
    }

    //获取订单信息（待接单页）
    public static void getTaskInfo(String orderId){
        Post.newPost()
                .url(GET_TASK_INFO)
                .addParam("orderId", orderId)
                .event(new GetTaskInfoEvent())
                .post();
    }

    //获取学校
    public static void getSchool(){
        Post.newPost()
                .url(GET_SCHOOL)
                .event(new GetSchoolEvent())
                .post();
    }

    //获取用户信息
    public static void getUserHomePageInfo(String name){
        Post.newPost()
                .url(GET_USER_HOME_PAGE_INFO)
                .addParam("name", name)
                .event(new GetUserHomePageInfoEvent())
                .post();
    }
}
