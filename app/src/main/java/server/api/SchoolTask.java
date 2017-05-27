package server.api;

import com.edu.schooltask.beans.User;
import com.edu.schooltask.data.DataCache;
import com.edu.schooltask.utils.TextUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.math.BigDecimal;
import java.util.Map;

import server.api.detail.GetDetailEvent;
import server.api.money.GetMoneyEvent;
import server.api.base.BaseCallBack;
import server.api.login.LoginEvent;
import server.api.login.UnloginEvent;
import server.api.pwd.SetPaypwdEvent;
import server.api.register.CheckCodeEvent;
import server.api.register.GetCodeEvent;
import server.api.register.RegisterEvent;
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
import server.api.task.order.GetWaitAssessOrderEvent;
import server.api.task.release.ReleaseTaskEvent;
import server.api.token.BaseTokenCallBack;
import server.api.user.UpdateUserInfoEvent;
import server.api.user.UploadBGEvent;
import server.api.user.UploadHeadEvent;

/**
 * Created by 夜夜通宵 on 2017/5/17.
 */

public class SchoolTask {
    static DataCache mDataCache;
    final static String LOGIN_URL = "http://192.168.191.1:8080/SchoolTaskServer/LoginController/login.do";
    final static String GET_CODE_URL = "http://192.168.191.1:8080/SchoolTaskServer/RegisterController/getCode.do";
    final static String REGISTER_URL = "http://192.168.191.1:8080/SchoolTaskServer/RegisterController/register.do";
    final static String RELEASE_TASK_URL ="http://192.168.191.1:8080/SchoolTaskServer/TaskController/release.do";
    final static String RELEASE_TASK_WITH_IMAGE_URL ="http://192.168.191.1:8080/SchoolTaskServer/TaskController/releaseWithImage.do";
    final static String GET_MONEY_URL ="http://192.168.191.1:8080/SchoolTaskServer/MoneyController/getMoney.do";
    final static String GET_TASK_ORDER_URL ="http://192.168.191.1:8080/SchoolTaskServer/TaskController/getTaskOrder.do";
    final static String GET_SCHOOL_TASK_URL ="http://192.168.191.1:8080/SchoolTaskServer/TaskController/getSchoolTask.do";
    final static String ACCEPT_TASK_URL ="http://192.168.191.1:8080/SchoolTaskServer/TaskController/acceptTask.do";
    final static String SET_PAY_PWD_URL ="http://192.168.191.1:8080/SchoolTaskServer/UserController/setPayPwd.do";
    final static String GET_TASK_ORDER_INFO_URL ="http://192.168.191.1:8080/SchoolTaskServer/TaskController/getTaskOrderInfo.do";
    final static String CHANGE_TASK_ORDER_STATE_URL ="http://192.168.191.1:8080/SchoolTaskServer/TaskController/changeTaskOrderState.do";
    final static String GET_WAIT_ASSESS_ORDER_URL ="http://192.168.191.1:8080/SchoolTaskServer/TaskController/getWaitAssessOrder.do";
    final static String NEW_TASK_COMMENT_URL ="http://192.168.191.1:8080/SchoolTaskServer/TaskCommentController/newTaskComment.do";
    final static String GET_TASK_COMMENT_URL ="http://192.168.191.1:8080/SchoolTaskServer/TaskCommentController/getTaskComment.do";
    final static String GET_TASK_COUNT_URL ="http://192.168.191.1:8080/SchoolTaskServer/TaskController/getTaskCount.do";
    final static String GET_TASK_CHILD_COMMENT_URL ="http://192.168.191.1:8080/SchoolTaskServer/TaskCommentController/getTaskChildComment.do";
    final static String CHECK_CODE_URL ="http://192.168.191.1:8080/SchoolTaskServer/RegisterController/checkCode.do";
    final static String GET_TASK_LIST_URL ="http://192.168.191.1:8080/SchoolTaskServer/TaskController/getTaskList.do";
    final static String GET_DETAIL_URL ="http://192.168.191.1:8080/SchoolTaskServer/DetailController/getDetail.do";
    final static String UPLOAD_HEAD_URL ="http://192.168.191.1:8080/SchoolTaskServer/UserController/uploadHead.do";
    final static String UPLOAD_BG_URL ="http://192.168.191.1:8080/SchoolTaskServer/UserController/uploadBG.do";
    final static String UPDATE_USER_INFO_URL ="http://192.168.191.1:8080/SchoolTaskServer/UserController/updateUserInfo.do";
    public final static String TASK_IMAGE_URL ="http://192.168.191.1:8080/SchoolTaskServer/static/images/";
    public final static String HEAD_URL ="http://192.168.191.1:8080/SchoolTaskServer/static/head/";
    public final static String BG_URL ="http://192.168.191.1:8080/SchoolTaskServer/static/bg/";

    public SchoolTask(DataCache dataCache){
        SchoolTask.mDataCache = dataCache;
    }

    //--------------------------需要Token------------------------------------

    //查询余额
    public static void getMoney(){
        User user = getUser();
        if(user != null){
            OkHttpUtils.post()
                    .url(GET_MONEY_URL)
                    .addHeader("token",user.getToken())
                    .build().execute(new BaseTokenCallBack(new GetMoneyEvent()));
        }
    }

    //发布任务
    public static void releaseTask(String school, String description, String content, BigDecimal cost,
                                   int limitTime, String payPwd, Map<String,File> files){
        User user = getUser();
        if(user != null){
            if(files.size() != 0){
                OkHttpUtils.post()
                        .url(RELEASE_TASK_WITH_IMAGE_URL)
                        .addHeader("token",user.getToken())
                        .addParams("school",school)
                        .addParams("description",description)
                        .addParams("content",content)
                        .addParams("cost",cost+"")
                        .addParams("limit_time",limitTime+"")
                        .addParams("pay_pwd",payPwd)
                        .files("images",files)
                        .build().execute(new BaseTokenCallBack(new ReleaseTaskEvent()));
            }
            else{
                OkHttpUtils.post()
                        .url(RELEASE_TASK_URL)
                        .addHeader("token",user.getToken())
                        .addParams("school",school)
                        .addParams("description",description)
                        .addParams("content",content)
                        .addParams("cost",cost+"")
                        .addParams("limit_time",limitTime+"")
                        .addParams("pay_pwd",payPwd)
                        .build().execute(new BaseTokenCallBack(new ReleaseTaskEvent()));
            }

        }
    }

    //发表任务评论
    public static void newTaskComment(String orderId,  long parentId, String comment){
        User user = getUser();
        if(user != null){
            OkHttpUtils.post()
                    .url(NEW_TASK_COMMENT_URL)
                    .addHeader("token", user.getToken())
                    .addParams("user_id", user.getUserId())
                    .addParams("order_id", orderId)
                    .addParams("parent_id", parentId+"")
                    .addParams("comment", comment)
                    .build()
                    .execute(new BaseTokenCallBack(new NewTaskCommentEvent()));
        }
    }

    //获取用户订单
    public static void getTaskOrder(int page){
        User user = getUser();
        if(user != null){
            OkHttpUtils.post()
                    .url(GET_TASK_ORDER_URL)
                    .addHeader("token", user.getToken())
                    .addParams("user_id", user.getUserId())
                    .addParams("page", page+"")
                    .build()
                    .execute(new BaseTokenCallBack(new GetTaskOrderEvent()));
        }
    }

    //获取用户待评价订单
    public static void getWaitAssessOrder(int page){
        User user = getUser();
        if(user != null){
            OkHttpUtils.post()
                    .url(GET_WAIT_ASSESS_ORDER_URL)
                    .addHeader("token",user.getToken())
                    .addParams("user_id", user.getUserId())
                    .addParams("page", page+"")
                    .build()
                    .execute(new BaseTokenCallBack(new GetWaitAssessOrderEvent()));
        }
    }

    //获取订单信息
    public static void getTaskOrderInfo(String orderId){
        User user = getUser();
        if(user != null){
            OkHttpUtils.post()
                    .url(GET_TASK_ORDER_INFO_URL)
                    .addHeader("token",user.getToken())
                    .addParams("user_id", user.getUserId())
                    .addParams("order_id", orderId)
                    .build()
                    .execute(new BaseTokenCallBack(new GetTaskOrderInfoEvent()));
        }
    }

    //设置支付密码
    public static void setPayPwd(String payPwd){
        User user = getUser();
        if(user != null){
            OkHttpUtils.post()
                    .url(SET_PAY_PWD_URL)
                    .addHeader("token",user.getToken())
                    .addParams("pay_pwd", TextUtil.getMD5(payPwd))
                    .build()
                    .execute(new BaseTokenCallBack(new SetPaypwdEvent()));
        }
    }

    //接受任务
    public static void acceptTask(String orderId){
        User user = getUser();
        if(user != null){
            OkHttpUtils.post()
                    .url(ACCEPT_TASK_URL)
                    .addHeader("token",user.getToken())
                    .addParams("order_id", orderId)
                    .build()
                    .execute(new BaseTokenCallBack(new AcceptTaskEvent()));
        }
    }

    //改变任务状态
    public static void changeTaskOrderState(String orderId, int state){
        User user = getUser();
        if(user != null){
            OkHttpUtils.post()
                    .url(CHANGE_TASK_ORDER_STATE_URL)
                    .addHeader("token",user.getToken())
                    .addParams("order_id", orderId)
                    .addParams("state", state + "")
                    .build()
                    .execute(new BaseTokenCallBack(new ChangeTaskOrderStateEvent()));
        }
    }

    //获取明细
    public static void getDetail(int page){
        User user = getUser();
        if(user != null){
            OkHttpUtils.post()
                    .url(GET_DETAIL_URL)
                    .addHeader("token",user.getToken())
                    .addParams("user_id", user.getUserId())
                    .addParams("page", page + "")
                    .build()
                    .execute(new BaseTokenCallBack(new GetDetailEvent()));
        }
    }

    //上传头像
    public static void uploadHead(File head){
        User user = getUser();
        if(user != null){
            OkHttpUtils.post()
                    .url(UPLOAD_HEAD_URL)
                    .addHeader("token",user.getToken())
                    .addFile("head", "head.png",head)
                    .build()
                    .execute(new BaseTokenCallBack(new UploadHeadEvent()));
        }
    }

    //上传背景
    public static void uploadBG(File bg){
        User user = getUser();
        if(user != null){
            OkHttpUtils.post()
                    .url(UPLOAD_BG_URL)
                    .addHeader("token",user.getToken())
                    .addFile("bg", "bg.png",bg)
                    .build()
                    .execute(new BaseTokenCallBack(new UploadBGEvent()));
        }
    }

    //更新用户信息
    public static void updateUserInfo(String value, int type){
        User user = getUser();
        if(user != null){
            OkHttpUtils.post()
                    .url(UPDATE_USER_INFO_URL)
                    .addHeader("token",user.getToken())
                    .addParams("value", value)
                    .addParams("type", type + "")
                    .build()
                    .execute(new BaseTokenCallBack(new UpdateUserInfoEvent()));
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
                .addParams("user_id", userId)
                .addParams("pwd", pwd)
                .build().execute(new BaseCallBack(new LoginEvent()));
    }

    //注册时获取验证码
    public static void getCode(String userId){
        OkHttpUtils.post()
                .url(GET_CODE_URL)
                .addParams("user_id", userId)
                .build().execute(new BaseCallBack(new GetCodeEvent()));
    }

    //验证注册验证码
    public static void checkCode(String userId, String code){
        OkHttpUtils.post()
                .url(CHECK_CODE_URL)
                .addParams("user_id", userId)
                .addParams("code", code)
                .build().execute(new BaseCallBack(new CheckCodeEvent()));
    }

    //注册
    public static void register(String userId, String name, String school, String pwd){
        OkHttpUtils.post()
                .url(REGISTER_URL)
                .addParams("user_id", userId)
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
                .url(GET_TASK_COMMENT_URL)
                .addParams("order_id", orderId)
                .addParams("page", page+"")
                .build()
                .execute(new BaseCallBack(new GetTaskCommentEvent()));
    }

    //获取任务评论的回复
    public static void getTaskChildComment(String orderId, long parentId){
        OkHttpUtils.post()
                .url(GET_TASK_CHILD_COMMENT_URL)
                .addParams("order_id", orderId)
                .addParams("comment_id", parentId+"")
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
}
