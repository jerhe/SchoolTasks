package server.api.result;

import android.util.Log;

import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.Callback;

import org.greenrobot.eventbus.EventBus;

import okhttp3.Call;
import server.api.event.RefreshTokenEvent;
import server.api.event.TokenErrorEvent;

/**
 * Created by 夜夜通宵 on 2017/5/18.
 */

public class ResultCallBack extends Callback<Result> {
    private Result result;

    public ResultCallBack(Result result){
        this.result = result;
    }

    @Override
    public Result parseNetworkResponse(okhttp3.Response response, int id) throws Exception {
        if(result == null) return null;
        result.setId(id);
        String r = response.body().string();
        int httpCode = response.code();
        result.setHttpCode(httpCode);
        if(httpCode != 200) return result;  //服务端出现错误
        BaseResult baseResult = new Gson().fromJson(r, BaseResult.class);
        if(result.isWithToken() && baseResult.isUpdate()){
            EventBus.getDefault().post(new RefreshTokenEvent(baseResult.getToken()));
        }
        result.setResult(baseResult);
        return result;
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        if(result == null) return;
        result.setId(id);
        result.setResult(false, -1, "连接失败,请检查网络", null);
        e.printStackTrace();
        EventBus.getDefault().post(result);
    }

    @Override
    public void onResponse(Result result, int id) {
        if(result == null) return;
        if(result.getHttpCode() != 200) result.setError("连接出了点小问题");
        EventBus.getDefault().post(result);
        if(result.isWithToken() && !result.isValidate()){
            EventBus.getDefault().post(new TokenErrorEvent());
        }
    }

}
