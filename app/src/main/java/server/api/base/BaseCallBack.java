package server.api.base;

import android.util.Log;

import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.Callback;

import org.greenrobot.eventbus.EventBus;

import okhttp3.Call;
import okhttp3.Response;
import server.api.login.LoginEvent;

/**
 * Created by 夜夜通宵 on 2017/5/18.
 */

public class BaseCallBack extends Callback<BaseEvent> {
    BaseEvent event;

    public BaseCallBack(BaseEvent event){
        this.event = event;
    }

    @Override
    public BaseEvent parseNetworkResponse(Response response, int id) throws Exception {
        String result = response.body().string();
        event = new Gson().fromJson(result, event.getClass());
        return event;
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        event.setCode(-1).setError("连接失败,请检查网络");
        e.printStackTrace();
        EventBus.getDefault().post(event);
    }

    @Override
    public void onResponse(BaseEvent event, int id) {
        EventBus.getDefault().post(event);
    }

}
