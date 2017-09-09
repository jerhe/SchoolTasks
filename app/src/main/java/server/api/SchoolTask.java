package server.api;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.edu.schooltask.beans.TaskUploadKey;
import com.edu.schooltask.beans.UploadKey;
import com.edu.schooltask.beans.UserInfoWithToken;
import com.edu.schooltask.event.UnloginEvent;
import com.edu.schooltask.utils.EncriptUtil;
import com.edu.schooltask.utils.UserUtil;
import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;
import server.api.event.friend.AgreeRequestEvent;
import server.api.event.friend.FriendRequestEvent;
import server.api.event.friend.GetFriendListEvent;
import server.api.event.friend.GetRequestListEvent;
import server.api.event.friend.RejectRequestEvent;
import server.api.event.qiniu.GetBGUploadKeyEvent;
import server.api.event.qiniu.GetHeadUploadKeyEvent;
import server.api.event.qiniu.GetTaskUploadKeyEvent;
import server.api.event.qiniu.UploadBGEvent;
import server.api.event.qiniu.UploadHeadEvent;
import server.api.event.qiniu.UploadTaskImageEvent;
import server.api.event.rong.GetTokenEvent;
import server.api.event.school.GetSchoolEvent;
import server.api.event.task.AcceptTaskEvent;
import server.api.event.task.GetSchoolTaskEvent;
import server.api.event.task.GetTaskInfoEvent;
import server.api.event.task.GetTaskListEvent;
import server.api.event.task.ReleaseTaskEvent;
import server.api.event.task.UpdateTaskInfoEvent;
import server.api.event.task.comment.GetTaskCommentListEvent;
import server.api.event.task.comment.GetTaskReplyListEvent;
import server.api.event.task.comment.NewTaskCommentEvent;
import server.api.event.task.order.ChangeTaskOrderStateEvent;
import server.api.event.task.order.GetTaskOrderInfoEvent;
import server.api.event.task.order.GetTaskOrderListEvent;
import server.api.event.user.GetLoginCodeEvent;
import server.api.event.user.GetPersonalCenterInfoEvent;
import server.api.event.user.GetUserHomePageInfoEvent;
import server.api.event.user.GetUserInfoEvent;
import server.api.event.user.LoginEvent;
import server.api.event.user.RefreshHeadBGEvent;
import server.api.event.user.UpdateLoginPwdEvent;
import server.api.event.user.UpdateUserInfoEvent;
import server.api.event.user.account.CheckPayPasswordEvent;
import server.api.event.user.account.GetDetailEvent;
import server.api.event.user.account.GetMoneyEvent;
import server.api.event.user.account.RechargeEvent;
import server.api.event.user.account.SetPayPwdEvent;
import server.api.event.user.account.UpdatePayPasswordEvent;
import server.api.event.user.register.CheckRegisterCodeEvent;
import server.api.event.user.register.GetRegisterCodeEvent;
import server.api.event.user.register.RegisterEvent;
import server.api.event.voucher.GetAvailableVouchersEvent;

/**
 * Created by 夜夜通宵 on 2017/5/17.
 */

public class SchoolTask {
    private final static String SERVER = "https://192.168.191.1:8443/SchoolTask/";
    //private final static String SERVER = "https://119.29.91.152:8443/SchoolTask/";

    //VerifyCode
    private final static String GET_REGISTER_CODE = SERVER + "verifyCode/getRegisterCode";
    private final static String GET_LOGIN_CODE = SERVER + "verifyCode/getLoginCode";

    //User
    private final static String CHECK_REGISTER_CODE = SERVER + "user/checkRegisterCode";
    private final static String CHECK_LOGIN_CODE = SERVER + "user/checkLoginCode";
    private final static String REGISTER = SERVER + "user/register";
    private final static String LOGIN = SERVER + "user/login";
    private final static String GET_USER_INFO = SERVER + "user/getUserInfo";
    private final static String UPDATE_USER_INFO = SERVER + "user/updateUserInfo";
    private final static String UPDATE_LOGIN_PWD = SERVER + "user/updateLoginPwd";
    private final static String GET_USER_HOME_PAGE_INFO = SERVER + "user/getUserHomePageInfo";
    private final static String GET_PERSONAL_CENTER_INFO = SERVER + "user/getPersonalCenterInfo";
    private final static String REFRESH_HEAD = SERVER + "user/refreshHead";
    private final static String REFRESH_BG = SERVER + "user/refreshBg";

    //Account
    private final static String GET_MONEY = SERVER + "account/getMoney";
    private final static String SET_PAY_PWD = SERVER + "account/setPayPwd";
    private final static String UPDATE_PAY_PWD = SERVER + "account/updatePayPwd";
    private final static String RECHARGE = SERVER + "account/recharge";
    private final static String GET_DETAIL = SERVER + "account/getDetailList";
    private final static String CHECK_PAY_PASSWORD = SERVER + "account/checkPayPassword";


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
    private final static String UPDATE_TASK_INFO = SERVER + "task/updateTaskInfo";

    //School
    private final static String GET_SCHOOL = SERVER + "school/getSchoolList";

    //Qiniu
    private final static String GET_TASK_UPLOAD_KEY = SERVER + "qiniu/getTaskUploadKey";
    private final static String GET_HEAD_UPLOAD_KEY = SERVER + "qiniu/getHeadUploadKey";
    private final static String GET_BG_UPLOAD_KEY = SERVER + "qiniu/getBGUploadKey";

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

    public static void init(){
        config = new Configuration.Builder()
                .zone(Zone.zone2)
                .responseTimeout(20)
                .build();
        uploadManager = new UploadManager(config);
    }



    //--------------------------需要Token------------------------------------

    //查询余额
    public static void getMoney(){
        TokenPost.newPost()
                .url(GET_MONEY)
                .event(new GetMoneyEvent())
                .post();
    }

    //获取七牛上传凭证
    public static void getTaskUploadKey(String payPassword){
        TokenPost.newPost()
                .url(GET_TASK_UPLOAD_KEY)
                .addParam("payPassword", payPassword)
                .event(new GetTaskUploadKeyEvent())
                .post();
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


    /**
     * 发布订单
     * @param orderId       订单号
     * @param school        学校
     * @param description   类别
     * @param content       内容
     * @param cost          实际支付金额
     * @param reward        任务报酬
     * @param limitTime     时限
     * @param payPassword   支付密码
     * @param voucherId     代金券编号
     * @param voucher       代金券金额
     * @param imageNum      图片数
     */
    public static void releaseTask(String orderId, String school, String description, String content,
                                   BigDecimal cost, BigDecimal reward, int limitTime, String payPassword,
                                   long voucherId, BigDecimal voucher, int imageNum){
        TokenPost.newPost()
                .url(RELEASE_TASK)
                .addParam("orderId", orderId)
                .addParam("school", school)
                .addParam("description", description)
                .addParam("content",content)
                .addParam("cost", cost)
                .addParam("reward", reward)
                .addParam("limitTime", limitTime)
                .addParam("payPassword", payPassword)
                .addParam("voucherId", voucherId)
                .addParam("voucher", voucher)
                .addParam("imageNum", imageNum)
                .event(new ReleaseTaskEvent())
                .post();
    }

    //发表任务评论
    public static void comment(String orderId, long parentId, String toUserId, String comment){
        TokenPost.newPost()
                .url(COMMENT)
                .addParam("orderId", orderId)
                .addParam("parentId", parentId)
                .addParam("toUserId", toUserId)
                .addParam("comment", comment)
                .event(new NewTaskCommentEvent())
                .post();
    }

    //获取用户订单
    public static void getUserOrderList(int type, int state, int sort, int page){
        TokenPost.newPost()
                .url(GET_USER_ORDER_LIST)
                .addParam("type", type)
                .addParam("state", state)
                .addParam("sort", sort)
                .addParam("page", page)
                .event(new GetTaskOrderListEvent())
                .post();
    }


    //获取订单信息（订单页）
    public static void getOrderInfo(String orderId){
        TokenPost.newPost()
                .url(GET_ORDER_INFO)
                .addParam("orderId", orderId)
                .event(new GetTaskOrderInfoEvent())
                .post();
    }

    //设置支付密码
    public static void setPayPwd(String payPwd){
        TokenPost.newPost()
                .url(SET_PAY_PWD)
                .addParam("payPwd", EncriptUtil.getMD5(payPwd))
                .event(new SetPayPwdEvent())
                .post();
    }

    //检测支付密码
    public static void checkPayPassword(String payPassword){
        TokenPost.newPost()
                .url(CHECK_PAY_PASSWORD)
                .addParam("payPassword", EncriptUtil.getMD5(payPassword))
                .event(new CheckPayPasswordEvent())
                .post();
    }

    //接受任务
    public static void acceptTask(String orderId, String payPassword){
        TokenPost.newPost()
                .url(ACCEPT_TASK)
                .addParam("orderId", orderId)
                .addParam("payPassword", payPassword)
                .event(new AcceptTaskEvent())
                .post();
    }

    //改变任务状态
    public static void changeTaskOrderState(String orderId, int state, String payPassword){
        TokenPost.newPost()
                .url(CHANGE_ORDER_STATE)
                .addParam("orderId", orderId)
                .addParam("state", state)
                .addParam("payPassword", payPassword)
                .event(new ChangeTaskOrderStateEvent())
                .post();
    }

    //获取明细
    public static void getDetail(int page){
        TokenPost.newPost()
                .url(GET_DETAIL)
                .addParam("page", page)
                .event(new GetDetailEvent())
                .post();
    }

    //获取七牛上传凭证
    public static void getHeadUploadKey(){
        TokenPost.newPost()
                .url(GET_HEAD_UPLOAD_KEY)
                .event(new GetHeadUploadKeyEvent())
                .post();
    }

    //获取七牛上传凭证
    public static void getBGUploadKey(){
        TokenPost.newPost()
                .url(GET_BG_UPLOAD_KEY)
                .event(new GetBGUploadKeyEvent())
                .post();
    }

    //更新用户信息
    public static void updateUserInfo(String value, int type){
        TokenPost.newPost()
                .url(UPDATE_USER_INFO)
                .addParam("value", value)
                .addParam("type", type)
                .event(new UpdateUserInfoEvent())
                .post();
    }

    //修改登录密码
    public static void updateLoginPwd(String oldPwd, String newPwd){
        TokenPost.newPost()
                .url(UPDATE_LOGIN_PWD)
                .addParam("oldPwd", oldPwd)
                .addParam("newPwd", newPwd)
                .event(new UpdateLoginPwdEvent())
                .post();
    }

    //获取用户个人中心信息
    public static void getPersonalCenterInfo(){
        TokenPost.newPost()
                .url(GET_PERSONAL_CENTER_INFO)
                .event(new GetPersonalCenterInfoEvent())
                .post();
    }

    //修改支付密码
    public static void updatePayPwd(String oldPayPwd, String newPayPwd){
        TokenPost.newPost()
                .url(UPDATE_PAY_PWD)
                .addParam("oldPayPwd", oldPayPwd)
                .addParam("newPayPwd", newPayPwd)
                .event(new UpdatePayPasswordEvent())
                .post();
    }

    //获取用户信息
    public static void getUserInfo(){
        TokenPost.newPost()
                .url(GET_USER_INFO)
                .event(new GetUserInfoEvent())
                .post();
    }


    //充值
    public static void recharge(String orderId, BigDecimal money, String type){
        TokenPost.newPost()
                .url(RECHARGE)
                .addParam("orderId", orderId)
                .addParam("money", money)
                .addParam("type", type)
                .event(new RechargeEvent())
                .post();
    }

    //获取用户可用代金券
    public static void getAvailableVouchers(){
        TokenPost.newPost()
                .url(GET_AVAILABLE_VOUCHERS)
                .event(new GetAvailableVouchersEvent())
                .post();
    }

    //获取融云Token
    public static void rongGetToken(){
        TokenPost.newPost()
                .url(RONG_GET_TOKEN)
                .event(new GetTokenEvent())
                .post();
    }

    //请求添加好友
    public static void friendRequest(String friendId){
        TokenPost.newPost()
                .url(FRIEND_REQUEST)
                .addParam("friendId", friendId)
                .event(new FriendRequestEvent())
                .post();
    }

    //请求好友请求列表
    public static void getRequestList(){
        TokenPost.newPost()
                .url(GET_REQUEST_LIST)
                .event(new GetRequestListEvent())
                .post();
    }

    //请求好友列表
    public static void getFriendList(){
        TokenPost.newPost()
                .url(GET_FRIEND_LIST)
                .event(new GetFriendListEvent())
                .post();
    }

    //同意好友请求
    public static void agreeRequest(String friendId){
        TokenPost.newPost()
                .url(AGREE_REQUEST)
                .addParam("friendId", friendId)
                .event(new AgreeRequestEvent())
                .post();
    }

    //同意好友请求
    public static void rejectRequest(String friendId){
        TokenPost.newPost()
                .url(REJECT_REQUEST)
                .addParam("friendId", friendId)
                .event(new RejectRequestEvent())
                .post();
    }

    public static void refreshHead(String head){
        TokenPost.newPost()
                .url(REFRESH_HEAD)
                .addParam("head", head)
                .event(new RefreshHeadBGEvent())
                .post();
    }

    public static void refreshBg(String bg){
        TokenPost.newPost()
                .url(REFRESH_BG)
                .addParam("bg", bg)
                .event(new RefreshHeadBGEvent())
                .post();
    }

    //修改任务信息
    public static void updateTaskInfo(String orderId, String content){
        TokenPost.newPost()
                .url(UPDATE_TASK_INFO)
                .addParam("orderId", orderId)
                .addParam("content", content)
                .event(new UpdateTaskInfoEvent())
                .post();
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
     * ----------------------------QINIU----------------------------------
     */
    //上传头像
    public static void uploadHead(Context context, File head, UploadKey uploadKey){
        final UserInfoWithToken user = getUser();
        final String headPath = uploadKey.getPath();
        final ProgressDialog progressDialog = ProgressDialog.show(context, null, "上传中...", true, false);
        uploadManager.put(head, headPath, uploadKey.getKey(), new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if(info.isOK()){
                    user.setHead(TASK_IMAGE + headPath);
                    user.updateAll();
                    refreshHead(TASK_IMAGE + headPath); //刷新服务端头像
                    UserInfo userInfo = UserUtil.toRongUserInfo(user);  //刷新融云本地缓存
                    RongIM.getInstance().refreshUserInfoCache(userInfo);
                    RongIM.getInstance().setCurrentUserInfo(userInfo);  //修改当前用户头像
                }
                else{
                    Log.e("uploadHead", info.error);
                }
                progressDialog.dismiss();
                EventBus.getDefault().post(new UploadHeadEvent(info.isOK()));
            }
        }, null);
    }

    //上传背景
    public static void uploadBG(Context context, File bg, UploadKey uploadKey){
        final UserInfoWithToken user = getUser();
        final String bgPath = uploadKey.getPath();
        final ProgressDialog progressDialog = ProgressDialog.show(context, null, "上传中...", true, false);
        uploadManager.put(bg, bgPath, uploadKey.getKey(), new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                if(info.isOK()){
                    user.setBg(TASK_IMAGE + bgPath);
                    user.updateAll();
                    refreshBg(TASK_IMAGE + bgPath); //刷新服务端背景
                }
                progressDialog.dismiss();
                EventBus.getDefault().post(new UploadBGEvent(info.isOK()));
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
    public static void getRegisterCode(String userId){
        Post.newPost()
                .url(GET_REGISTER_CODE)
                .addParam("id", userId)
                .event(new GetRegisterCodeEvent())
                .post();
    }

    //登录时获取验证码
    public static void getLoginCode(String userId){
        Post.newPost()
                .url(GET_LOGIN_CODE)
                .addParam("id", userId)
                .event(new GetLoginCodeEvent())
                .post();
    }

    //验证注册验证码
    public static void checkRegisterCode(String userId, String code){
        Post.newPost()
                .url(CHECK_REGISTER_CODE)
                .addParam("id", userId)
                .addParam("code", code)
                .event(new CheckRegisterCodeEvent())
                .post();
    }

    //验证注册验证码
    public static void checkLoginCode(String userId, String code){
        Post.newPost()
                .url(CHECK_LOGIN_CODE)
                .addParam("id", userId)
                .addParam("code", code)
                .event(new LoginEvent())
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
    public static void getTaskComment(String orderId, String order, int page){
        Post.newPost()
                .url(GET_COMMENT)
                .addParam("orderId", orderId)
                .addParam("order", order)
                .addParam("page", page)
                .event(new GetTaskCommentListEvent())
                .post();
    }

    //获取任务评论的回复
    public static void getTaskReplyList(String orderId, long parentId, int page){
        Post.newPost()
                .url(GET_REPLY_LIST)
                .addParam("orderId", orderId)
                .addParam("parentId", parentId)
                .addParam("page", page)
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
                .addParam("minCost", minCost)
                .addParam("maxCost", maxCost)
                .addParam("page", page)
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
