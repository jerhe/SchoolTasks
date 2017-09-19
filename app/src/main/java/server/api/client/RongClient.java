package server.api.client;

import server.api.Post;
import server.api.result.Result;

import static server.api.SchoolTask.SERVER;

/**
 * Created by 夜夜通宵 on 2017/9/12.
 */

public class RongClient {
    private final static String RONG_GET_TOKEN = SERVER + "rong/getToken";

    /**
     * 获取融云Token
     */
    public static void rongGetToken(Result result){
        Post.newPost()
                .url(RONG_GET_TOKEN)
                .result(result)
                .post();
    }
}
