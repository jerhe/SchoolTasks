package server.api.client;

import server.api.Post;
import server.api.result.Result;

import static server.api.SchoolTask.SERVER;

/**
 * Created by 夜夜通宵 on 2017/9/12.
 */

public class FriendClient {
    private final static String FRIEND_REQUEST = SERVER + "friend/request";
    private final static String GET_REQUEST_LIST = SERVER + "friend/getRequestList";
    private final static String GET_FRIEND_LIST = SERVER + "friend/getFriendList";
    private final static String AGREE_REQUEST = SERVER + "friend/agreeRequest";
    private final static String REJECT_REQUEST = SERVER + "friend/rejectRequest";
    private final static String DELETE = SERVER + "friend/delete";


    /**
     * 发送好友请求
     * @param friendId  好友
     */
    public static void friendRequest(Result result, String friendId){
        Post.newPost()
                .url(FRIEND_REQUEST)
                .addParam("friendId", friendId)
                .result(result)
                .post();
    }

    /**
     * 获取好友请求列表
     */
    public static void getRequestList(Result result){
        Post.newPost()
                .url(GET_REQUEST_LIST)
                .result(result)
                .post();
    }

    /**
     * 获取好友列表
     */
    public static void getFriendList(Result result){
        Post.newPost()
                .url(GET_FRIEND_LIST)
                .result(result)
                .post();
    }

    /**
     * 同意好友请求
     * @param friendId 好友
     */
    public static void agreeRequest(Result result, String friendId){
        Post.newPost()
                .url(AGREE_REQUEST)
                .addParam("friendId", friendId)
                .result(result)
                .post();
    }

    /**
     * 拒绝好友请求
     * @param friendId  好友
     */
    public static void rejectRequest(Result result, String friendId){
        Post.newPost()
                .url(REJECT_REQUEST)
                .addParam("friendId", friendId)
                .result(result)
                .post();
    }

    /**
     * 删除好友
     * @param result
     * @param friendId
     */
    public static void delete(Result result, String friendId){
        Post.newPost()
                .url(DELETE)
                .addParam("friendId", friendId)
                .result(result)
                .post();
    }
}
