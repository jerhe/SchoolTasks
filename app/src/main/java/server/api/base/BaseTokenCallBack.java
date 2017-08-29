package server.api.base;

import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.Callback;

import org.greenrobot.eventbus.EventBus;

import okhttp3.Call;
import okhttp3.Response;
import server.api.event.user.RefreshTokenEvent;
import com.edu.schooltask.event.TokenErrorEvent;

/**
 * Created by 夜夜通宵 on 2017/5/18.
 */

public class BaseTokenCallBack extends Callback<BaseTokenEvent> {
    BaseTokenEvent event;   //事件
    ResponseListener responseListener;  //用于Post队列回调

    //用于判断Token错误，当Token错误时，如果事件的Token和此处Token一致，表明TOken错误，否则表明Token刷新了
    //Token刷新时需要自增
    public static int tokenTag;

    public BaseTokenCallBack(){}

    public BaseTokenCallBack(BaseTokenEvent event){
        this.event = event;
    }

    //请求后台时解析返回的数据生成事件对象event，之后调用response
    @Override
    public BaseTokenEvent parseNetworkResponse(Response response, int id) throws Exception {
        String result = response.body().string();   //获取后台返回数据
        event = new Gson().fromJson(result, event.getClass());
        if(event.isUpdate()){   //Token需要更新，event对象包含刷新Token
            EventBus.getDefault().post(new RefreshTokenEvent(event.getToken()));    //Post刷新Token
        }
        return event;
    }

    //请求错误，未成功连接后台
    @Override
    public void onError(Call call, Exception e, int id) {
        event.setCode(-1).setError("连接失败,请检查网络");   //设置event错误码和错误信息
        e.printStackTrace();
        EventBus.getDefault().post(event);  //postEvent
        if(responseListener != null){
            responseListener.onResponse();
        }
    }

    @Override
    public void onResponse(BaseTokenEvent event, int id) {
        EventBus.getDefault().post(event);
        if(!event.isValidate()){
            if(event.tokenTag == tokenTag){ //Token错误
                EventBus.getDefault().post(new TokenErrorEvent());  //postEvent
            }
        }
        if(responseListener != null){
            responseListener.onResponse();
        }
    }

    public void setResponseListener(ResponseListener listener){
        responseListener = listener;
    }

    public interface ResponseListener{
        void onResponse();
    }

}
