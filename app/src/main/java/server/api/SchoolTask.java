package server.api;

import com.edu.schooltask.beans.TaskUploadKey;
import com.edu.schooltask.beans.UploadKey;
import com.edu.schooltask.beans.User;
import com.edu.schooltask.beans.UserConfig;
import com.edu.schooltask.data.DataCache;
import com.edu.schooltask.utils.TextUtil;
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

import server.api.account.GetDetailEvent;
import server.api.account.GetMoneyEvent;
import server.api.account.RechargeEvent;
import server.api.account.SetPaypwdEvent;
import server.api.account.UpdatePaypwdEvent;
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
import server.api.task.comment.GetTaskChildCommentEvent;
import server.api.task.comment.GetTaskCommentEvent;
import server.api.task.comment.NewTaskCommentEvent;
import server.api.task.count.GetTaskCountEvent;
import server.api.task.get.GetSchoolTaskEvent;
import server.api.task.get.GetTaskListEvent;
import server.api.task.order.ChangeTaskOrderStateEvent;
import server.api.task.order.GetTaskOrderEvent;
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

/**
 * Created by 夜夜通宵 on 2017/5/17.
 */

public class SchoolTask {

    //Register
    private final static String GET_CODE_URL =
            "http://192.168.191.1:8080/SchoolTask/register/getCode";
    private final static String CHECK_CODE_URL =
            "http://192.168.191.1:8080/SchoolTask/register/checkCode";
    private final static String REGISTER_URL =
            "http://192.168.191.1:8080/SchoolTask/register/register";
    //Login
    private final static String LOGIN_URL =
            "http://192.168.191.1:8080/SchoolTask/login/login";

    //Account
    private final static String GET_MONEY_URL =
            "http://192.168.191.1:8080/SchoolTask/account/getMoney";
    private final static String SET_PAY_PWD_URL =
            "http://192.168.191.1:8080/SchoolTask/account/setPayPwd";
    private final static String UPDATE_PAY_PWD_URL =
            "http://192.168.191.1:8080/SchoolTask/account/updatePayPwd";
    private final static String RECHARGE_URL =
            "http://192.168.191.1:8080/SchoolTask/account/recharge";
    private final static String GET_DETAIL_URL =
            "http://192.168.191.1:8080/SchoolTask/account/getDetail";

    //Task
    private final static String RELEASE_TASK_URL =
            "http://192.168.191.1:8080/SchoolTask/task/release";
    private final static String GET_USER_ORDER_URL =
            "http://192.168.191.1:8080/SchoolTask/task/getUserOrder";
    private final static String GET_SCHOOL_TASK_URL =
            "http://192.168.191.1:8080/SchoolTask/task/getSchoolTask";
    private final static String ACCEPT_TASK_URL =
            "http://192.168.191.1:8080/SchoolTask/task/acceptTask";
    private final static String GET_ORDER_INFO_URL =
            "http://192.168.191.1:8080/SchoolTask/task/getOrderInfo";
    private final static String CHANGE_ORDER_STATE_URL =
            "http://192.168.191.1:8080/SchoolTask/task/changeOrderState";

    private final static String COMMENT_URL =
            "http://192.168.191.1:8080/SchoolTask/task/comment";
    private final static String GET_COMMENT_URL =
            "http://192.168.191.1:8080/SchoolTask/task/getComment";
    private final static String GET_TASK_COUNT_URL =
            "http://192.168.191.1:8080/SchoolTask/task/getTaskCount";
    private final static String GET_CHILD_COMMENT_URL =
            "http://192.168.191.1:8080/SchoolTask/task/getChildComment";
    private final static String GET_TASK_LIST_URL =
            "http://192.168.191.1:8080/SchoolTask/task/getTaskList";

    //UserInfo
    private final static String UPDATE_USER_INFO_URL =
            "http://192.168.191.1:8080/SchoolTask/userInfo/updateUserInfo";
    private final static String UPDATE_PWD_URL =
            "http://192.168.191.1:8080/SchoolTask/userInfo/updatePwd";
    private final static String GET_USER_HOME_PAGE_INFO_URL =
            "http://192.168.191.1:8080/SchoolTask/userInfo/getUserHomePageInfo";
    private final static String GET_PERSONAL_CENTER_INFO_URL =
            "http://192.168.191.1:8080/SchoolTask/userInfo/getPersonalCenterInfo";

    //relation
    private final static String UPDATE_RELATION_URL =
            "http://192.168.191.1:8080/SchoolTask/relation/updateRelationType.do";

    private final static String GET_FOLLOWERS_URL =
            "http://192.168.191.1:8080/SchoolTask/relation/getFollowers";

    private final static String GET_FANS_URL =
            "http://192.168.191.1:8080/SchoolTask/relation/getFans";

    //Poll
    private final static String GET_POLL_URL =
            "http://192.168.191.1:8080/SchoolTask/poll/getPoll";
    private final static String SEND_PRIVATE_MESSAGE_URL =
            "http://192.168.191.1:8080/SchoolTask/poll/sendPrivateMessage";

    //School
    private final static String GET_SCHOOL_URL =
            "http://192.168.191.1:8080/SchoolTask/school/getSchool.do";

    //user config
    private final static String GET_USER_CONFIG_URL =
            "http://192.168.191.1:8080/SchoolTask/userConfig/getUserConfig";
    private final static String UPDATE_USER_CONFIG_URL =
            "http://192.168.191.1:8080/SchoolTask/userConfig/updateUserConfig";

    //Qiniu
    private final static String GET_TASK_UPLOAD_KEY_URL =
            "http://192.168.191.1:8080/SchoolTask/qiniu/getTaskUploadKey";
    private final static String GET_HEAD_UPLOAD_KEY_URL =
            "http://192.168.191.1:8080/SchoolTask/qiniu/getHeadUploadKey";
    private final static String GET_BG_UPLOAD_KEY_URL =
            "http://192.168.191.1:8080/SchoolTask/qiniu/getBGUploadKey";
    private final static String GET_MESSAGE_UPLOAD_KEY_URL =
            "http://192.168.191.1:8080/SchoolTask/qiniu/getMessageUploadKey";
    private final static String QINIU_REFRESH_URL =
            "http://192.168.191.1:8080/SchoolTask/qiniu/refresh";

    public final static String TASK_IMAGE_URL ="http://oqqzw04zt.bkt.clouddn.com/";
    public final static String HEAD_URL ="http://oqqzw04zt.bkt.clouddn.com/head/";
    public final static String BG_URL ="http://oqqzw04zt.bkt.clouddn.com//bg/";

    //------------------------------------------------------------------------------------

    static DataCache mDataCache;
    static Configuration config;
    static UploadManager uploadManager;

    static PostQueue postQueue;

    public SchoolTask(DataCache dataCache){
        SchoolTask.mDataCache = dataCache;
        postQueue = new PostQueue(dataCache);
        config = new Configuration.Builder()
                .zone(Zone.zone2)
                .build();
        uploadManager = new UploadManager(config);
    }

    //--------------------------需要Token------------------------------------

    //查询余额
    public static void getMoney(){
        User user = getUser();
        if(user != null){
            RequestBody requestBody = new RequestBody(GET_MONEY_URL, null,
                    new BaseTokenCallBack(new GetMoneyEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //获取七牛上传凭证
    public static void getTaskUploadKey(){
        User user = getUser();
        if(user != null){
            RequestBody requestBody = new RequestBody(GET_TASK_UPLOAD_KEY_URL, null,
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
    public static void releaseTask(String orderId, String school, String description, String content, BigDecimal cost,
                                   int limitTime, String payPwd, int imageNum){
        User user = getUser();
        if(user != null){
            Map<String, String> params = new HashMap<>();
            params.put("order_id", orderId);
            params.put("school", school);
            params.put("description", description);
            params.put("content",content);
            params.put("cost", cost+"");
            params.put("limit_time", limitTime+"");
            params.put("pay_pwd", payPwd);
            params.put("image_num", imageNum+"");
            RequestBody requestBody = new RequestBody(RELEASE_TASK_URL, params,
                    new BaseTokenCallBack(new ReleaseTaskEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //发表任务评论
    public static void comment(String orderId, long parentId, String comment){
        User user = getUser();
        if(user != null){
            Map<String, String> params = new HashMap<>();
            params.put("order_id", orderId);
            params.put("parent_id", parentId+"");
            params.put("comment", comment);
            RequestBody requestBody = new RequestBody(COMMENT_URL, params,
                    new BaseTokenCallBack(new NewTaskCommentEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //获取用户订单
    public static void getUserOrder(int page){
        User user = getUser();
        if(user != null){
            Map<String, String> params = new HashMap<>();
            params.put("page", page+"");
            RequestBody requestBody = new RequestBody(GET_USER_ORDER_URL, params,
                    new BaseTokenCallBack(new GetTaskOrderEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //获取订单信息
    public static void getOrderInfo(String orderId){
        User user = getUser();
        if(user != null){
            Map<String, String> params = new HashMap<>();
            params.put("order_id", orderId);
            RequestBody requestBody = new RequestBody(GET_ORDER_INFO_URL, params,
                    new BaseTokenCallBack(new GetTaskOrderInfoEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //设置支付密码
    public static void setPayPwd(String payPwd){
        User user = getUser();
        if(user != null){
            Map<String, String> params = new HashMap<>();
            params.put("pay_pwd", TextUtil.getMD5(payPwd));
            RequestBody requestBody = new RequestBody(SET_PAY_PWD_URL, params,
                    new BaseTokenCallBack(new SetPaypwdEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //接受任务
    public static void acceptTask(String orderId){
        User user = getUser();
        if(user != null){
            Map<String, String> params = new HashMap<>();
            params.put("order_id", orderId);
            RequestBody requestBody = new RequestBody(ACCEPT_TASK_URL, params,
                    new BaseTokenCallBack(new AcceptTaskEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //改变任务状态
    public static void changeTaskOrderState(String orderId, int state){
        User user = getUser();
        if(user != null){
            Map<String, String> params = new HashMap<>();
            params.put("order_id", orderId);
            params.put("state", state+"");
            RequestBody requestBody = new RequestBody(CHANGE_ORDER_STATE_URL, params,
                    new BaseTokenCallBack(new ChangeTaskOrderStateEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //获取明细
    public static void getDetail(int page){
        User user = getUser();
        if(user != null){
            Map<String, String> params = new HashMap<>();
            params.put("page", page+"");
            RequestBody requestBody = new RequestBody(GET_DETAIL_URL, params,
                    new BaseTokenCallBack(new GetDetailEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //获取七牛上传凭证
    public static void getHeadUploadKey(){
        User user = getUser();
        if(user != null){
            RequestBody requestBody = new RequestBody(GET_HEAD_UPLOAD_KEY_URL, null,
                    new BaseTokenCallBack(new GetHeadUploadKeyEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //上传头像
    public static void uploadHead(File head, UploadKey uploadKey){
        final User user = getUser();
        uploadManager.put(head, "head/" + user.getUserId() +".png", uploadKey.getKey(), new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                EventBus.getDefault().post(new UploadHeadEvent(info.isOK()));
                OkHttpUtils.post()
                        .url(QINIU_REFRESH_URL)
                        .addParams("path","head/"+user.getUserId() +".png")
                        .build()
                        .execute(null);
            }
        }, null);
    }

    //获取七牛上传凭证
    public static void getBGUploadKey(){
        User user = getUser();
        if(user != null){
            RequestBody requestBody = new RequestBody(GET_BG_UPLOAD_KEY_URL, null,
                    new BaseTokenCallBack(new GetBGUploadKeyEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //上传背景
    public static void uploadBG(File bg, UploadKey uploadKey){
        final User user = getUser();
        uploadManager.put(bg, "bg/" + user.getUserId() +".png", uploadKey.getKey(), new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                EventBus.getDefault().post(new UploadBGEvent(info.isOK()));
                OkHttpUtils.post()
                        .url(QINIU_REFRESH_URL)
                        .addParams("path","bg/"+user.getUserId() +".png")
                        .build()
                        .execute(null);
            }
        }, null);
    }

    //更新用户信息
    public static void updateUserInfo(String value, int type){
        User user = getUser();
        if(user != null){
            Map<String, String> params = new HashMap<>();
            params.put("value", value);
            params.put("type", type+"");
            RequestBody requestBody = new RequestBody(UPDATE_USER_INFO_URL, params,
                    new BaseTokenCallBack(new UpdateUserInfoEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //修改密码
    public static void updatePwd(String oldPwd, String newPwd){
        User user = mDataCache.getUser();
        if(user != null){
            Map<String, String> params = new HashMap<>();
            params.put("old_pwd", oldPwd);
            params.put("new_pwd", newPwd);
            RequestBody requestBody = new RequestBody(UPDATE_PWD_URL, params,
                    new BaseTokenCallBack(new UpdateLoginPwdEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //获取用户个人中心信息
    public static void getPersonalCenterInfo(){
        User user = mDataCache.getUser();
        if(user != null){
            RequestBody requestBody = new RequestBody(GET_PERSONAL_CENTER_INFO_URL, null,
                    new BaseTokenCallBack(new GetPersonalCenterInfoEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //修改支付密码
    public static void updatePayPwd(String oldPayPwd, String newPayPwd){
        User user = mDataCache.getUser();
        if(user != null){
            Map<String, String> params = new HashMap<>();
            params.put("old_pay_pwd", oldPayPwd);
            params.put("new_pay_pwd", newPayPwd);
            RequestBody requestBody = new RequestBody(UPDATE_PAY_PWD_URL, params,
                    new BaseTokenCallBack(new UpdatePaypwdEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //修改用户关系
    public static void updateRelation(String fromId, String toId, int type){
        User user = mDataCache.getUser();
        if(user != null){
            Map<String, String> params = new HashMap<>();
            params.put("from_id", fromId);
            params.put("to_id", toId);
            params.put("type", type+"");
            RequestBody requestBody = new RequestBody(UPDATE_RELATION_URL, params,
                    new BaseTokenCallBack(new UpdateRelationEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //获取用户关注
    public static void getFollowers(int page){
        User user = mDataCache.getUser();
        if(user != null){
            Map<String, String> params = new HashMap<>();
            params.put("page", page+"");
            RequestBody requestBody = new RequestBody(GET_FOLLOWERS_URL, params,
                    new BaseTokenCallBack(new GetFollowUserEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //获取用户粉丝
    public static void getFans(int page){
        User user = mDataCache.getUser();
        if(user != null){
            Map<String, String> params = new HashMap<>();
            params.put("page", page+"");
            RequestBody requestBody = new RequestBody(GET_FANS_URL, params,
                    new BaseTokenCallBack(new GetFansUserEvent()));
            postQueue.newPost(requestBody);
        }
    }


    //更新用户配置
    public static void updateUserConfig(UserConfig userConfig){
        User user = mDataCache.getUser();
        if(user != null){
            OkHttpUtils.post()
                    .url(UPDATE_USER_CONFIG_URL)
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
        User user = mDataCache.getUser();
        if(user != null){
            RequestBody requestBody = new RequestBody(GET_USER_CONFIG_URL, null,
                    new BaseTokenCallBack(new GetUserConfigEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //发送私信
    public static void sendPrivateMessage(String toId, String message, String image, int width, int height){
        User user = mDataCache.getUser();
        if(user != null){
            Map<String, String> params = new HashMap<>();
            params.put("to_id", toId);
            params.put("message", message);
            params.put("image", image);
            params.put("width", width+"");
            params.put("height", height+"");
            RequestBody requestBody = new RequestBody(SEND_PRIVATE_MESSAGE_URL, params,
                    new BaseTokenCallBack(new SendPrivateMessageEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //获取七牛上传凭证
    public static void getMessageImageUploadKey(){
        User user = getUser();
        if(user != null){
            RequestBody requestBody = new RequestBody(GET_MESSAGE_UPLOAD_KEY_URL, null,
                    new BaseTokenCallBack(new GetMessageUploadKeyEvent()));
            postQueue.newPost(requestBody);
        }
    }

    //上传图片
    public static void uploadMessageImage(File image, final int imageWidth, final int imageHeight, UploadKey uploadKey){
        final User user = getUser();
        String imageName = TextUtil.getRandStr(6);
        final String imagePath = "message/" + user.getUserId() + imageName +".jpg";
        uploadManager.put(image, imagePath, uploadKey.getKey(), new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                EventBus.getDefault().post(
                        new UploadMessageImageEvent(info.isOK(),
                                TASK_IMAGE_URL + imagePath, imageWidth, imageHeight));
            }
        }, null);
    }

    //充值
    public static void recharge(String orderId, BigDecimal money, String type){
        User user = getUser();
        if(user != null){
            Map<String, String> params = new HashMap<>();
            params.put("order_id", orderId);
            params.put("money", money.toString());
            params.put("type", type);
            RequestBody requestBody = new RequestBody(RECHARGE_URL, params,
                    new BaseTokenCallBack(new RechargeEvent()));
            postQueue.newPost(requestBody);
        }

    }

    //用于判断用户已登录
    private static User getUser(){
        User user = mDataCache.getUser();
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
                .url(LOGIN_URL)
                .addParams("id", userId)
                .addParams("pwd", pwd)
                .build().execute(new BaseCallBack(new LoginEvent()));
    }

    //注册时获取验证码
    public static void getCode(String userId){
        OkHttpUtils.post()
                .url(GET_CODE_URL)
                .addParams("id", userId)
                .build().execute(new BaseCallBack(new GetCodeEvent()));
    }

    //验证注册验证码
    public static void checkCode(String userId, String code){
        OkHttpUtils.post()
                .url(CHECK_CODE_URL)
                .addParams("id", userId)
                .addParams("code", code)
                .build().execute(new BaseCallBack(new CheckCodeEvent()));
    }

    //注册
    public static void register(String userId, String name, String school, String pwd){
        OkHttpUtils.post()
                .url(REGISTER_URL)
                .addParams("id", userId)
                .addParams("name", name)
                .addParams("school", school)
                .addParams("pwd", TextUtil.getMD5(pwd))
                .build().execute(new BaseCallBack(new RegisterEvent()));
    }

    //根据学校获取任务列表
    public static void getSchoolTask(String school,int page){
        OkHttpUtils.post()
                .url(GET_SCHOOL_TASK_URL)
                .addParams("school",school)
                .addParams("page",page+"")
                .build()
                .execute(new BaseCallBack(new GetSchoolTaskEvent()));
    }

    //获取任务的浏览数和评论数
    public static void getTaskCount(String orderId){
        OkHttpUtils.post()
                .url(GET_TASK_COUNT_URL)
                .addParams("order_id",orderId)
                .build()
                .execute(new BaseCallBack(new GetTaskCountEvent()));
    }

    //获取任务评论
    public static void getTaskComment(String orderId, int page){
        OkHttpUtils.post()
                .url(GET_COMMENT_URL)
                .addParams("order_id", orderId)
                .addParams("page", page+"")
                .build()
                .execute(new BaseCallBack(new GetTaskCommentEvent()));
    }

    //获取任务评论的回复
    public static void getTaskChildComment(String orderId, long parentId){
        OkHttpUtils.post()
                .url(GET_CHILD_COMMENT_URL)
                .addParams("order_id", orderId)
                .addParams("parent_id", parentId+"")
                .build()
                .execute(new BaseCallBack(new GetTaskChildCommentEvent()));
    }

    //获取任务列表
    public static void getTaskList(String school, String description, String searchText,
                                   BigDecimal minCost, BigDecimal maxCost, int page){
        OkHttpUtils.post()
                .url(GET_TASK_LIST_URL)
                .addParams("school", school)
                .addParams("description", description)
                .addParams("search_text", searchText)
                .addParams("min_cost", minCost + "")
                .addParams("max_cost", maxCost + "")
                .addParams("page", page+"")
                .build()
                .execute(new BaseCallBack(new GetTaskListEvent()));
    }

    //获取学校
    public static void getSchool(){
        OkHttpUtils.post()
                .url(GET_SCHOOL_URL)
                .build()
                .execute(new BaseCallBack(new GetSchoolEvent()));
    }

    //获取用户信息
    public static void getUserHomePageInfo(String fromId, String toId){
        OkHttpUtils.post()
                .url(GET_USER_HOME_PAGE_INFO_URL)
                .addParams("from_id", fromId)
                .addParams("to_id", toId)
                .build()
                .execute(new BaseCallBack(new GetUserHomePageInfoEvent()));
    }

    //获取消息
    public static void poll(){
        User user = mDataCache.getUser();
        if(user != null){
            OkHttpUtils.post()
                    .url(GET_POLL_URL)
                    .addHeader("token",user.getToken())
                    .build()
                    .execute(new BaseCallBack(new PollEvent()));
        }
    }
}
