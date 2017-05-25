package server.api.token;

import android.util.Log;

import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.Callback;

import org.greenrobot.eventbus.EventBus;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by 夜夜通宵 on 2017/5/18.
 */

public class BaseTokenCallBack extends Callback<BaseTokenEvent> {
    BaseTokenEvent event;

    public BaseTokenCallBack(BaseTokenEvent event){
        this.event = event;
    }

    @Override
    public BaseTokenEvent parseNetworkResponse(Response response, int id) throws Exception {
        String result = response.body().string();
        event = new Gson().fromJson(result, event.getClass());
        if(event.isUpdate()){
            EventBus.getDefault().post(new RefreshTokenEvent(event.getToken()));    //刷新Token
            Log.d("Token","Refresh");
        }
        return event;
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        event.setCode(-1).setError("连接失败,请检查网络");
        e.printStackTrace();
        EventBus.getDefault().post(event);
    }

    @Override
    public void onResponse(BaseTokenEvent event, int id) {
        EventBus.getDefault().post(event);
        if(!event.isValidate()){   //Token错误
            EventBus.getDefault().post(new TokenErrorEvent());  //账号错误
        }
    }

}
