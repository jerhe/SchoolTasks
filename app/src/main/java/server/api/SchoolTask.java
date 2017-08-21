package server.api;

import com.edu.schooltask.beans.TaskUploadKey;
import com.edu.schooltask.beans.UploadKey;
import com.edu.schooltask.beans.UserInfoWithToken;
import com.edu.schooltask.beans.UserConfig;
import com.edu.schooltask.utils.StringUtil;
import com.edu.schooltask.utils.UserUtil;
import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.zhy.http.okhttp.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import server.api.task.get.GetTaskInfoEvent;
import server.api.user.GetUserInfoEvent;
import server.api.user.account.GetDetailEvent;
import server.api.user.account.GetMoneyEvent;
import server.api.user.account.RechargeEvent;
import server.api.user.account.SetPayPwdEvent;
import server.api.user.account.UpdatePaypwdEvent;
import server.api.base.BaseCallBack;
import server.api.base.BaseTokenCallBack;
import server.api.login.LoginEvent;
import server.api.login.UnloginEvent;
import server.api.poll.PollEvent;
import server.api.poll.SendPrivateMessageEvent;
import server.api.qiniu.GetBGUploadKeyEvent;
import server.api.qiniu.GetHeadUploadKeyEvent;
import server.api.qiniu.GetMessageUploadKeyEvent;
import server.api.qiniu.GetTaskUploadKeyEvent;
import server.api.qiniu.UploadBGEvent;
import server.api.qiniu.UploadHeadEvent;
import server.api.qiniu.UploadMessageImageEvent;
import server.api.qiniu.UploadTaskImageEvent;
import server.api.register.CheckCodeEvent;
import server.api.register.GetCodeEvent;
import server.api.register.RegisterEvent;
import server.api.school.GetSchoolEvent;
import server.api.task.accept.AcceptTaskEvent;
import server.api.task.comment.GetTaskReplyListEvent;
import server.api.task.comment.GetTaskCommentListEvent;
import server.api.task.comment.NewTaskCommentEvent;
import server.api.task.get.GetSchoolTaskEvent;
import server.api.task.get.GetTaskListEvent;
import server.api.task.order.ChangeTaskOrderStateEvent;
import server.api.task.order.GetTaskOrderListEvent;
import server.api.task.order.GetTaskOrderInfoEvent;
import server.api.task.release.ReleaseTaskEvent;
import server.api.user.GetPersonalCenterInfoEvent;
import server.api.user.GetUserHomePageInfoEvent;
import server.api.user.UpdateLoginPwdEvent;
import server.api.user.UpdateUserInfoEvent;
import server.api.user.config.GetUserConfigEvent;
import server.api.user.relation.GetFansUserEvent;
import server.api.user.relation.GetFollowUserEvent;
import server.api.user.relation.UpdateRelationEvent;
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

    //relation
    private final static String UPDATE_RELATION = SERVER + "relation/updateRelation";
    private final static String GET_FOLLOWERS = SERVER + "relation/getFollowerList";
    private final static String GET_FANS = SERVER + "relation/getFansList";

    //Poll
    private final static String GET_POLL = SERVER + "poll/getPoll";
    private final static String SEND_PRIVATE_MESSAGE = SERVER + "poll/sendPrivateMessage";

    //School
    private final static String GET_SCHOOL = SERVER + "school/getSchoolList";

    //Qiniu
    private final static String GET_TASK_UPLOAD_KEY = SERVER + "qiniu/getTaskUploadKey";
    private final static String GET_HEAD_UPLOAD_KEY = SERVER + "qiniu/getHeadUploadKey";
    private final static String GET_BG_UPLOAD_KEY = SERVER + "qiniu/getBGUploadKey";
    private final static String GET_MESSAGE_UPLOAD_KEY = SERVER + "qiniu/getMessageUploadKey";
    private final static String QINIU_REFRESH = SERVER + "qiniu/refresh";

    //Voucher
    private final static String GET_AVAILABLE_VOUCHERS = SERVER + "voucher/getAvailableVoucherList";

    public final static String TASK_IMAGE ="http://oqqzw04zt.bkt.clouddn.com/";
    public final static String HEAD ="http://oqqzw04zt.bkt.clouddn.com/head/";
    public final static String BG ="http://oqqzw04zt.bkt.clouddn.com/bg/";

    //------------------------------------------------------------------------------------

    static Configuration config;
    static UploadManager uploadManager;

    static PostQueue postQueue;

    public static void init(){
        postQueue = new PostQueue();
        config = new Configuration.Builder()
                .zone(Zone.zone2)
                .responseTimeout(20)
                .build();
        uploadManager = new UploadManager(config);
    }

    //--------------------------需要Token------------------------------------

    //查询余额
    public static void getMoney(){
        UserInfoWithToken user = getUser();
        if(user != null){
            RequestBody requestBody = new RequestBody(GET_MONEY, null,
                    new BaseTokenCallBack(new GetMoneyEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //获取七牛上传凭证
    public static void getTaskUploadKey(String payPwd){
        UserInfoWithToken user = getUser();
        if(user != null){
            Map<String, String> params = new HashMap<>();
            params.put("pay_pwd", payPwd);
            RequestBody requestBody = new RequestBody(GET_TASK_UPLOAD_KEY, params,
                    new BaseTokenCallBack(new GetTaskUploadKeyEvent()));
            postQueue.newPost(requestBody);
        }
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
        UserInfoWithToken user = getUser();
        if(user != null){
            Map<String, String> params = new HashMap<>();
            params.put("order_id", orderId);
            params.put("school", school);
            params.put("description", description);
            params.put("content",content);
            params.put("cost", cost+"");
            params.put("task_cost", taskCost + "");
            params.put("limit_time", limitTime+"");
            params.put("pay_pwd", payPwd);
            params.put("voucher_id", voucherId + "");
            params.put("image_num", imageNum+"");
            RequestBody requestBody = new RequestBody(RELEASE_TASK, params,
                    new BaseTokenCallBack(new ReleaseTaskEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //发表任务评论
    public static void comment(String orderId, long parentId, String toUserId, String comment){
        UserInfoWithToken user = getUser();
        if(user != null){
            Map<String, String> params = new HashMap<>();
            params.put("order_id", orderId);
            params.put("parent_id", parentId+"");
            params.put("to_user_id", toUserId);
            params.put("comment", comment);
            RequestBody requestBody = new RequestBody(COMMENT, params,
                    new BaseTokenCallBack(new NewTaskCommentEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //获取用户订单
    public static void getUserOrderList(int type, int state, int sort, int page){
        UserInfoWithToken user = UserUtil.getLoginUser();
        if(user != null){
            Map<String, String> params = new HashMap<>();
            params.put("type", type+"");
            params.put("state", state+"");
            params.put("sort", sort+"");
            params.put("page", page+"");
            RequestBody requestBody = new RequestBody(GET_USER_ORDER_LIST, params,
                    new BaseTokenCallBack(new GetTaskOrderListEvent()));
            postQueue.newPost(requestBody);
        }
    }


    //获取订单信息（订单页）
    public static void getOrderInfo(String orderId){
        UserInfoWithToken user = getUser();
        if(user != null){
            Map<String, String> params = new HashMap<>();
            params.put("order_id", orderId);
            RequestBody requestBody = new RequestBody(GET_ORDER_INFO, params,
                    new BaseTokenCallBack(new GetTaskOrderInfoEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //设置支付密码
    public static void setPayPwd(String payPwd){
        UserInfoWithToken user = getUser();
        if(user != null){
            Map<String, String> params = new HashMap<>();
            params.put("pay_pwd", StringUtil.getMD5(payPwd));
            RequestBody requestBody = new RequestBody(SET_PAY_PWD, params,
                    new BaseTokenCallBack(new SetPayPwdEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //接受任务
    public static void acceptTask(String orderId){
        UserInfoWithToken user = getUser();
        if(user != null){
            Map<String, String> params = new HashMap<>();
            params.put("order_id", orderId);
            RequestBody requestBody = new RequestBody(ACCEPT_TASK, params,
                    new BaseTokenCallBack(new AcceptTaskEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //改变任务状态
    public static void changeTaskOrderState(String orderId, int state){
        UserInfoWithToken user = getUser();
        if(user != null){
            Map<String, String> params = new HashMap<>();
            params.put("order_id", orderId);
            params.put("state", state+"");
            RequestBody requestBody = new RequestBody(CHANGE_ORDER_STATE, params,
                    new BaseTokenCallBack(new ChangeTaskOrderStateEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //获取明细
    public static void getDetail(int page){
        UserInfoWithToken user = getUser();
        if(user != null){
            Map<String, String> params = new HashMap<>();
            params.put("page", page+"");
            RequestBody requestBody = new RequestBody(GET_DETAIL, params,
                    new BaseTokenCallBack(new GetDetailEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //获取七牛上传凭证
    public static void getHeadUploadKey(){
        UserInfoWithToken user = getUser();
        if(user != null){
            RequestBody requestBody = new RequestBody(GET_HEAD_UPLOAD_KEY, null,
                    new BaseTokenCallBack(new GetHeadUploadKeyEvent()));
            postQueue.newPost(requestBody);
        }
    }

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

    //获取七牛上传凭证
    public static void getBGUploadKey(){
        UserInfoWithToken user = getUser();
        if(user != null){
            RequestBody requestBody = new RequestBody(GET_BG_UPLOAD_KEY, null,
                    new BaseTokenCallBack(new GetBGUploadKeyEvent()));
            postQueue.newPost(requestBody);
        }
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

    //更新用户信息
    public static void updateUserInfo(String value, int type){
        UserInfoWithToken user = getUser();
        if(user != null){
            Map<String, String> params = new HashMap<>();
            params.put("value", value);
            params.put("type", type+"");
            RequestBody requestBody = new RequestBody(UPDATE_USER_INFO, params,
                    new BaseTokenCallBack(new UpdateUserInfoEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //修改密码
    public static void updateLoginPwd(String oldPwd, String newPwd){
        UserInfoWithToken user = getUser();
        if(user != null){
            Map<String, String> params = new HashMap<>();
            params.put("old_pwd", oldPwd);
            params.put("new_pwd", newPwd);
            RequestBody requestBody = new RequestBody(UPDATE_LOGIN_PWD, params,
                    new BaseTokenCallBack(new UpdateLoginPwdEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //获取用户个人中心信息
    public static void getPersonalCenterInfo(){
        UserInfoWithToken user = getUser();
        if(user != null){
            RequestBody requestBody = new RequestBody(GET_PERSONAL_CENTER_INFO, null,
                    new BaseTokenCallBack(new GetPersonalCenterInfoEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //修改支付密码
    public static void updatePayPwd(String oldPayPwd, String newPayPwd){
        UserInfoWithToken user = getUser();
        if(user != null){
            Map<String, String> params = new HashMap<>();
            params.put("old_pay_pwd", oldPayPwd);
            params.put("new_pay_pwd", newPayPwd);
            RequestBody requestBody = new RequestBody(UPDATE_PAY_PWD, params,
                    new BaseTokenCallBack(new UpdatePaypwdEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //修改用户关系
    public static void updateRelation(String fromId, String toId, int type){
        UserInfoWithToken user = getUser();
        if(user != null){
            Map<String, String> params = new HashMap<>();
            params.put("from_id", fromId);
            params.put("to_id", toId);
            params.put("type", type+"");
            RequestBody requestBody = new RequestBody(UPDATE_RELATION, params,
                    new BaseTokenCallBack(new UpdateRelationEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //获取用户信息
    public static void getUserInfo(){
        UserInfoWithToken user = getUser();
        if(user != null){
            RequestBody requestBody = new RequestBody(GET_USER_INFO, null,
                    new BaseTokenCallBack(new GetUserInfoEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //获取用户关注
    public static void getFollowers(int page){
        UserInfoWithToken user = UserUtil.getLoginUser();
        if(user != null){
            Map<String, String> params = new HashMap<>();
            params.put("page", page+"");
            RequestBody requestBody = new RequestBody(GET_FOLLOWERS, params,
                    new BaseTokenCallBack(new GetFollowUserEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //获取用户粉丝
    public static void getFans(int page){
        UserInfoWithToken user = UserUtil.getLoginUser();
        if(user != null){
            Map<String, String> params = new HashMap<>();
            params.put("page", page+"");
            RequestBody requestBody = new RequestBody(GET_FANS, params,
                    new BaseTokenCallBack(new GetFansUserEvent()));
            postQueue.newPost(requestBody);
        }
    }


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
                    .addParams("private_message", userConfig.isPrivateMessage()+"")
                    .build()
                    .execute(null);
        }
    }

    //获取用户配置
    public static void getUserConfig(){
        UserInfoWithToken user = getUser();
        if(user != null){
            RequestBody requestBody = new RequestBody(GET_USER_CONFIG, null,
                    new BaseTokenCallBack(new GetUserConfigEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //发送私信
    public static void sendPrivateMessage(String toId, String message, String image, int width, int height){
        UserInfoWithToken user = getUser();
        if(user != null){
            Map<String, String> params = new HashMap<>();
            params.put("to_id", toId);
            params.put("bg_private_message", message);
            params.put("image", image);
            params.put("width", width+"");
            params.put("height", height+"");
            RequestBody requestBody = new RequestBody(SEND_PRIVATE_MESSAGE, params,
                    new BaseTokenCallBack(new SendPrivateMessageEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //获取七牛上传凭证
    public static void getMessageImageUploadKey(){
        UserInfoWithToken user = getUser();
        if(user != null){
            RequestBody requestBody = new RequestBody(GET_MESSAGE_UPLOAD_KEY, null,
                    new BaseTokenCallBack(new GetMessageUploadKeyEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //上传图片
    public static void uploadMessageImage(File image, final int imageWidth, final int imageHeight, UploadKey uploadKey){
        final UserInfoWithToken user = getUser();
        String imageName = StringUtil.getRandStr(6);
        final String imagePath = "bg_private_message/" + user.getUserId() + imageName +".jpg";
        uploadManager.put(image, imagePath, uploadKey.getKey(), new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                EventBus.getDefault().post(
                        new UploadMessageImageEvent(info.isOK(),
                                TASK_IMAGE + imagePath, imageWidth, imageHeight));
            }
        }, null);
    }

    //充值
    public static void recharge(String orderId, BigDecimal money, String type){
        UserInfoWithToken user = getUser();
        if(user != null){
            Map<String, String> params = new HashMap<>();
            params.put("order_id", orderId);
            params.put("money", money.toString());
            params.put("type", type);
            RequestBody requestBody = new RequestBody(RECHARGE, params,
                    new BaseTokenCallBack(new RechargeEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //获取用户可用代金券
    public static void getAvailableVouchers(){
        UserInfoWithToken user = getUser();
        if(user != null){
            RequestBody requestBody = new RequestBody(GET_AVAILABLE_VOUCHERS, null,
                    new BaseTokenCallBack(new GetAvailableVouchersEvent()));
            postQueue.newPost(requestBody);
        }
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


    //---------------------------不需要Token------------------------------

    //登录
    public static void login(String userId, String pwd){
        OkHttpUtils.post()
                .url(LOGIN)
                .addParams("id", userId)
                .addParams("pwd", pwd)
                .build().execute(new BaseCallBack(new LoginEvent()));
    }

    //注册时获取验证码
    public static void getCode(String userId){
        OkHttpUtils.post()
                .url(GET_CODE)
                .addParams("id", userId)
                .build().execute(new BaseCallBack(new GetCodeEvent()));
    }

    //验证注册验证码
    public static void checkCode(String userId, String code){
        OkHttpUtils.post()
                .url(CHECK_CODE)
                .addParams("id", userId)
                .addParams("code", code)
                .addParams("type", 0 + "")
                .build().execute(new BaseCallBack(new CheckCodeEvent()));
    }

    //注册
    public static void register(String userId, String name, String school, String pwd){
        OkHttpUtils.post()
                .url(REGISTER)
                .addParams("id", userId)
                .addParams("name", name)
                .addParams("school", school)
                .addParams("pwd", StringUtil.getMD5(pwd))
                .build().execute(new BaseCallBack(new RegisterEvent()));
    }

    //根据学校获取任务列表
    public static void getSchoolTask(String school,int page){
        OkHttpUtils.post()
                .url(GET_TASK_LIST)
                .addParams("school",school)
                .addParams("page",page+"")
                .build()
                .execute(new BaseCallBack(new GetSchoolTaskEvent()));
    }

    //获取任务评论
    public static void getTaskComment(String orderId, int page){
        OkHttpUtils.post()
                .url(GET_COMMENT)
                .addParams("order_id", orderId)
                .addParams("page", page+"")
                .build()
                .execute(new BaseCallBack(new GetTaskCommentListEvent()));
    }

    //获取任务评论的回复
    public static void getTaskReplyList(String orderId, long parentId, int page){
        OkHttpUtils.post()
                .url(GET_REPLY_LIST)
                .addParams("order_id", orderId)
                .addParams("parent_id", parentId+"")
                .addParams("page", page + "")
                .build()
                .execute(new BaseCallBack(new GetTaskReplyListEvent()));
    }

    //获取任务列表
    public static void searchTask(String school, String description, String searchText,
                                   BigDecimal minCost, BigDecimal maxCost, int page){
        OkHttpUtils.post()
                .url(SEARCH_TASK)
                .addParams("school", school)
                .addParams("description", description)
                .addParams("search_text", searchText)
                .addParams("min_cost", minCost + "")
                .addParams("max_cost", maxCost + "")
                .addParams("page", page+"")
                .build()
                .execute(new BaseCallBack(new GetTaskListEvent()));
    }

    //获取订单信息（待接单页）
    public static void getTaskInfo(String orderId){
        OkHttpUtils.post()
                .url(GET_TASK_INFO)
                .addParams("order_id", orderId)
                .build()
                .execute(new BaseCallBack(new GetTaskInfoEvent()));
    }

    //获取学校
    public static void getSchool(){
        OkHttpUtils.post()
                .url(GET_SCHOOL)
                .build()
                .execute(new BaseCallBack(new GetSchoolEvent()));
    }

    //获取用户信息
    public static void getUserHomePageInfo(String fromId, String toId){
        OkHttpUtils.post()
                .url(GET_USER_HOME_PAGE_INFO)
                .addParams("from_id", fromId)
                .addParams("to_id", toId)
                .build()
                .execute(new BaseCallBack(new GetUserHomePageInfoEvent()));
    }

    //获取消息
    public static void poll(){
        UserInfoWithToken user = UserUtil.getLoginUser();
        if(user != null){
            OkHttpUtils.post()
                    .url(GET_POLL)
                    .addHeader("token",user.getToken())
                    .build()
                    .execute(new BaseCallBack(new PollEvent()));
        }
    }
}
