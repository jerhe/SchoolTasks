package server.api.base;

import android.util.Log;

import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.Callback;

import org.greenrobot.eventbus.EventBus;

import okhttp3.Call;
import okhttp3.Response;
import server.api.token.RefreshTokenEvent;
import server.api.token.TokenErrorEvent;

/**
 * Created by 夜夜通宵 on 2017/5/18.
 */

public class BaseTokenCallBack extends Callback<BaseTokenEvent> {
    BaseTokenEvent event;
    ResponseListener responseListener;

    public static int tokenTag;

    public BaseTokenCallBack(BaseTokenEvent event){
        this.event = event;
    }

    @Override
    public BaseTokenEvent parseNetworkResponse(Response response, int id) throws Exception {
        String result = response.body().string();
        event = new Gson().fromJson(result, event.getClass());
        if(event.isUpdate()){   //token需要更新
            EventBus.getDefault().post(new RefreshTokenEvent(event.getToken()));    //刷新Token
        }
        return event;
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        event.setCode(-1).setError("连接失败,请检查网络");
        e.printStackTrace();
        EventBus.getDefault().post(event);
        if(responseListener != null){
            responseListener.onResponse();
        }
    }

    @Override
    public void onResponse(BaseTokenEvent event, int id) {
        EventBus.getDefault().post(event);
        if(!event.isValidate()){
            if(event.tokenTag == tokenTag){
                Log.e("token error", " @class:" + event.getClass().getSimpleName() + " @token:" + event.getToken());
                EventBus.getDefault().post(new TokenErrorEvent());
            }
            else{
                Log.e("token","tag different" + " @class:" + event.getClass().getSimpleName());
            }
        }
        if(responseListener != null){
            responseListener.onResponse();
        }
    }

    public String getEventClass(){
        return event.getClass().getCanonicalName();
    }

    public void setResponseListener(ResponseListener listener){
        responseListener = listener;
    }

    public interface ResponseListener{
        void onResponse();
    }

}
