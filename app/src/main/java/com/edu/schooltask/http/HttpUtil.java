package com.edu.schooltask.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.edu.schooltask.beans.User;
import com.edu.schooltask.data.DataCache;
import com.edu.schooltask.event.AcceptOrderEvent;
import com.edu.schooltask.event.ChangeOrderStateEvent;
import com.edu.schooltask.event.CheckTokenEvent;
import com.edu.schooltask.event.GetAssessOrderEvent;
import com.edu.schooltask.event.GetCodeEvent;
import com.edu.schooltask.event.GetMoneyEvent;
import com.edu.schooltask.event.GetOrderChildCommentEvent;
import com.edu.schooltask.event.GetOrderCountEvent;
import com.edu.schooltask.event.GetOrderCommentEvent;
import com.edu.schooltask.event.GetOrderInfoEvent;
import com.edu.schooltask.event.GetSchoolOrderEvent;
import com.edu.schooltask.event.GetUserOrderEvent;
import com.edu.schooltask.event.LoginEvent;
import com.edu.schooltask.event.NewOrderCommentEvent;
import com.edu.schooltask.event.RegisterFinishEvent;
import com.edu.schooltask.event.ReleaseEvent;
import com.edu.schooltask.event.SetPaypwdEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by 夜夜通宵 on 2017/5/6.
 */

public class HttpUtil {
    private static DataCache mDataCache;
    final static String LOGIN_URL = "http://192.168.191.1:8080/SchoolTaskServer/LoginController/login.do";
    final static String GET_CODE_URL = "http://192.168.191.1:8080/SchoolTaskServer/RegisterController/getCode.do";
    final static String REGISTER_FINISH_URL = "http://192.168.191.1:8080/SchoolTaskServer/RegisterController/finish.do";
    final static String RELEASE_ORDER_URL ="http://192.168.191.1:8080/SchoolTaskServer/OrderController/release.do";
    final static String CHECK_TOKEN_URL ="http://192.168.191.1:8080/SchoolTaskServer/TokenController/checkToken.do";
    final static String GET_MONEY_URL ="http://192.168.191.1:8080/SchoolTaskServer/MoneyController/getMoney.do";
    final static String GET_USER_ORDER_URL ="http://192.168.191.1:8080/SchoolTaskServer/OrderController/getUserOrder.do";
    final static String GET_SCHOOL_ORDER_URL ="http://192.168.191.1:8080/SchoolTaskServer/OrderController/getSchoolOrder.do";
    final static String ACCEPT_ORDER_URL ="http://192.168.191.1:8080/SchoolTaskServer/OrderController/acceptOrder.do";
    final static String SET_PAYPWD_URL ="http://192.168.191.1:8080/SchoolTaskServer/UserController/setPaypwd.do";
    final static String GET_ORDER_INFO_URL ="http://192.168.191.1:8080/SchoolTaskServer/OrderController/getOrderInfo.do";
    final static String CHANGE_ORDER_STATE_URL ="http://192.168.191.1:8080/SchoolTaskServer/OrderController/changeOrderState.do";
    final static String GET_ASSESS_ORDER_URL ="http://192.168.191.1:8080/SchoolTaskServer/OrderController/getAssessOrder.do";
    final static String NEW_ORDER_COMMENT_URL ="http://192.168.191.1:8080/SchoolTaskServer/CommentController/newOrderComment.do";
    final static String GET_ORDER_COMMENT_URL ="http://192.168.191.1:8080/SchoolTaskServer/CommentController/getOrderComment.do";
    final static String GET_ORDER_COUNT_URL ="http://192.168.191.1:8080/SchoolTaskServer/OrderController/getOrderCount.do";
    final static String GET_ORDER_CHILD_COMMENT_URL ="http://192.168.191.1:8080/SchoolTaskServer/CommentController/getOrderChildComment.do";


    public final static String ORDER_IMAGE_URL ="http://192.168.191.1:8080/SchoolTaskServer/static/images/";

    public static void setDataCache(DataCache mDataCache){
        HttpUtil.mDataCache = mDataCache;
    }

    private static OkHttpClient client;
    private static OkHttpClient getInstance(){
        if(client == null){
            client = new OkHttpClient();
        }
        return client;
    }




    public static void login(String userId, String userPwd){
        RequestBody requestBody = new FormBody.Builder()
                .add("user_id",userId)
                .add("pwd",userPwd)
                .build();
        post(LOGIN_URL, requestBody, new BaseCallBack(new LoginEvent()));
    }

    public static void getCode(String userId){
        RequestBody requestBody = new FormBody.Builder()
                .add("user_id",userId)
                .build();
        post(GET_CODE_URL, requestBody, new BaseCallBack(new GetCodeEvent()));
    }

    public static void registerFinish(String userId, String school, String name, String pwd){
        RequestBody requestBody = new FormBody.Builder()
                .add("user_id",userId)
                .add("school",school)
                .add("name",name)
                .add("pwd",pwd)
                .build();
        post(REGISTER_FINISH_URL, requestBody, new BaseCallBack(new RegisterFinishEvent()));
    }

    public static void releaseOrder(String token, final String userId, final String school,
                                    final String content, final BigDecimal cost, final int limitTime,
                                    final List<String> paths, final String pwd){
        checkToken(token, new HttpCheckToken() {
            @Override
            public void onSuccess() {
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                for(int i=0; i < paths.size(); i++) {
                    File f=new File(paths.get(i));
                    if(f!=null) {
                        builder.addFormDataPart("image"+i, f.getName(), RequestBody.create(MediaType.parse("image/png"), f));
                    }
                }
                builder.addFormDataPart("user_id",userId);
                builder.addFormDataPart("school", school);
                builder.addFormDataPart("content", content);
                builder.addFormDataPart("cost", cost+"");
                builder.addFormDataPart("limit_time", limitTime+"");
                builder.addFormDataPart("pwd", pwd);
                MultipartBody requestBody = builder.build();
                post(RELEASE_ORDER_URL, requestBody, new BaseCallBack(new ReleaseEvent()));
            }

            @Override
            public void onFailure() {
                EventBus.getDefault().post(new ReleaseEvent(false));
            }
        });
    }

    public static void setPaypwd(String token, final String userId, final String pwd){
        checkToken(token, new HttpCheckToken() {
            @Override
            public void onSuccess() {
                RequestBody requestBody = new FormBody.Builder()
                        .add("user_id",userId).add("pwd",pwd).build();
                post(SET_PAYPWD_URL, requestBody, new BaseCallBack(new SetPaypwdEvent()));
            }

            @Override
            public void onFailure() {
                EventBus.getDefault().post(new SetPaypwdEvent(false));
            }
        });
    }

    public static void getOrderInfo(String token, final String userId, final String orderId){
        checkToken(token, new HttpCheckToken() {
            @Override
            public void onSuccess() {
                RequestBody requestBody = new FormBody.Builder()
                        .add("order_id",orderId).add("user_id",userId).build();
                post(GET_ORDER_INFO_URL, requestBody, new BaseCallBack(new GetOrderInfoEvent()));
            }

            @Override
            public void onFailure() {
                EventBus.getDefault().post(new GetOrderInfoEvent(false));
            }
        });
    }

    public static void acceptOrder(String token, final String orderId, final String userId){
        checkToken(token, new HttpCheckToken() {
            @Override
            public void onSuccess() {
                RequestBody requestBody = new FormBody.Builder()
                        .add("order_id",orderId).add("user_id",userId).build();
                post(ACCEPT_ORDER_URL, requestBody, new BaseCallBack(new AcceptOrderEvent()));
            }

            @Override
            public void onFailure() {
                EventBus.getDefault().post(new AcceptOrderEvent(false));
            }
        });
    }



    public static void getMoney(String token, final String id){
        checkToken(token, new HttpCheckToken() {
            @Override
            public void onSuccess() {
                RequestBody requestBody = new FormBody.Builder()
                        .add("user_id",id).build();
                post(GET_MONEY_URL, requestBody, new BaseCallBack(new GetMoneyEvent()));
            }

            @Override
            public void onFailure() {
                EventBus.getDefault().post(new GetMoneyEvent(false));
            }
        });

    }

    public static void getUserOrder(String token, final String id, final int pageIndex){
        checkToken(token, new HttpCheckToken() {
            @Override
            public void onSuccess() {
                RequestBody requestBody = new FormBody.Builder()
                        .add("user_id",id)
                        .add("page_index",pageIndex+"").build();
                post(GET_USER_ORDER_URL, requestBody, new BaseCallBack(new GetUserOrderEvent()));
            }

            @Override
            public void onFailure() {
                EventBus.getDefault().post(new GetUserOrderEvent(false));
            }
        });
    }

    public static void getSchoolOrder(String token, final String school, final int pageIndex){
        checkToken(token, new HttpCheckToken() {
            @Override
            public void onSuccess() {
                RequestBody requestBody = new FormBody.Builder()
                        .add("school",school).add("page_index",pageIndex+"").build();
                post(GET_SCHOOL_ORDER_URL, requestBody, new BaseCallBack(new GetSchoolOrderEvent()));
            }

            @Override
            public void onFailure() {
                EventBus.getDefault().post(new GetSchoolOrderEvent(false));
            }
        });
    }

    public static void getAssessOrder(String token, final String id, final int pageIndex){
        checkToken(token, new HttpCheckToken() {
            @Override
            public void onSuccess() {
                RequestBody requestBody = new FormBody.Builder()
                        .add("user_id",id)
                        .add("page_index",pageIndex+"").build();
                post(GET_ASSESS_ORDER_URL, requestBody, new BaseCallBack(new GetAssessOrderEvent()));
            }

            @Override
            public void onFailure() {
                EventBus.getDefault().post(new GetAssessOrderEvent(false));
            }
        });
    }


    public static void getAllSchoolOrder(int pageIndex){
        RequestBody requestBody = new FormBody.Builder()
                .add("school","*").add("page_index",pageIndex+"").build();
        post(GET_SCHOOL_ORDER_URL, requestBody, new BaseCallBack(new GetSchoolOrderEvent()));
    }

    public static void changeOrderState(final String token, final String orderId, final String userId,
                                        final int state){
        checkToken(token, new HttpCheckToken() {
            @Override
            public void onSuccess() {
                RequestBody requestBody = new FormBody.Builder()
                        .add("token",token).add("order_id",orderId).add("user_id",userId)
                        .add("state",state+"").build();
                post(CHANGE_ORDER_STATE_URL, requestBody, new BaseCallBack(new ChangeOrderStateEvent()));
            }

            @Override
            public void onFailure() {
                EventBus.getDefault().post(new ChangeOrderStateEvent(false));
            }
        });
    }

    public static void newOrderComment(final String token, final String orderId, final String userId,
                                        final long parent_id, final String comment){
        checkToken(token, new HttpCheckToken() {
            @Override
            public void onSuccess() {
                RequestBody requestBody = new FormBody.Builder()
                        .add("token",token).add("order_id",orderId).add("user_id",userId)
                        .add("parent_id",parent_id+"").add("comment",comment).build();
                post(NEW_ORDER_COMMENT_URL, requestBody, new BaseCallBack(new NewOrderCommentEvent()));
            }

            @Override
            public void onFailure() {
                EventBus.getDefault().post(new NewOrderCommentEvent(false));
            }
        });
    }

    public static void getOrderComment(String orderId, int pageIndex){
        RequestBody requestBody = new FormBody.Builder()
                .add("order_id",orderId).add("page_index",pageIndex+"").build();
        post(GET_ORDER_COMMENT_URL, requestBody, new BaseCallBack(new GetOrderCommentEvent()));

    }

    public static void getOrderChildComment(String orderId, long commentId){
        RequestBody requestBody = new FormBody.Builder()
                .add("order_id",orderId).add("comment_id",commentId + "").build();
        post(GET_ORDER_CHILD_COMMENT_URL, requestBody, new BaseCallBack(new GetOrderChildCommentEvent()));

    }

    public static void getOrderCount(String orderId){
        RequestBody requestBody = new FormBody.Builder()
                .add("order_id",orderId).build();
        post(GET_ORDER_COUNT_URL, requestBody, new BaseCallBack(new GetOrderCountEvent()));

    }

    //------------------------------------------------------------------------

    public static void checkToken(String token, final HttpCheckToken httpCheckToken){
        RequestBody requestBody = new FormBody.Builder()
                .add("token",token).build();
        post(CHECK_TOKEN_URL, requestBody, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                httpCheckToken.onFailure();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject data = new JSONObject(response.body().string());
                    int code = data.getInt("result");
                    switch (code){
                        case 1:
                            User user = mDataCache.getUser();
                            user.setToken(data.getString("token"));
                            mDataCache.saveUser(user);
                        case 0:
                            httpCheckToken.onSuccess();
                            break;
                        case -1:
                            break;
                        case 2:
                            EventBus.getDefault().post(new CheckTokenEvent());
                            break;
                        default:
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void post(String url, RequestBody requestBody, BaseCallBack baseCallBack){
        Request request = new Request.Builder().url(url).post(requestBody).build();
        getInstance().newCall(request).enqueue(baseCallBack);
    }

    public static void post(String url, RequestBody requestBody, okhttp3.Callback callback){
        Request request = new Request.Builder().url(url).post(requestBody).build();
        getInstance().newCall(request).enqueue(callback);
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
