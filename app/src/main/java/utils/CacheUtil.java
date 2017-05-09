package utils;

import android.content.Context;

import beans.User;
import http.HttpUtil;

/**
 * Created by 夜夜通宵 on 2017/5/9.
 */

public class CacheUtil {
    Context context;
    ACache cache;

    public CacheUtil(Context context) {
        this.context = context;
        cache = ACache.get(context);
    }




}
