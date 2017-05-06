package http;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by 夜夜通宵 on 2017/5/6.
 */

public class HttpCallBack implements okhttp3.Callback {
    public HttpResponse httpResponse;

    public HttpCallBack(){}
    public HttpCallBack(HttpResponse httpResponse){
        this.httpResponse = httpResponse;
    }

    @Override
    public void onFailure(Call call, IOException e) {
        httpResponse.code = -1;
        try {
            httpResponse.handler();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        try {
            String str = response.body().string();
            if(str.length() != 0){
                JSONObject jsonObject = new JSONObject(str);
                httpResponse.code = jsonObject.getInt("result");
                httpResponse.data = jsonObject;   //数据返回成功
                httpResponse.handler();
            }
        } catch (Exception e) {
            httpResponse.code = -2;
            e.printStackTrace();
        }
    }
}
