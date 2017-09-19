package server.api.client;

import com.edu.schooltask.beans.UserInfoWithToken;
import com.edu.schooltask.beans.Comment;
import com.edu.schooltask.beans.dynamic.Dynamic;
import com.edu.schooltask.utils.UserUtil;

import server.api.Post;
import server.api.result.Result;

import static server.api.SchoolTask.SERVER;

/**
 * Created by 夜夜通宵 on 2017/9/12.
 */

public class DynamicClient {
    private final static String GET_DYNAMIC_LIST = SERVER + "dynamic/getDynamicList";
    private final static String RELEASE_DYNAMIC = SERVER + "dynamic/release";
    private final static String DYNAMIC_LIKE = SERVER + "dynamic/like";
    private final static String GET_COMMENT_LIST = SERVER + "dynamic/getCommentList";
    private final static String COMMENT = SERVER + "dynamic/comment";
    private final static String GET_DYNAMIC = SERVER + "dynamic/getDynamic";
    private final static String GET_REPLY_LIST = SERVER + "dynamic/getReplyList";
    private final static String DELETE = SERVER + "dynamic/delete";
    private final static String GET_LIKE_LIST = SERVER + "dynamic/getLikeList";


    public static void getDynamic(Result result, int dynamicId) {
        Post.newPost()
                .url(GET_DYNAMIC)
                .addParam("dynamicId", dynamicId)
                .result(result)
                .post();
    }

    /**
     * 获取动态列表
     * @param page
     */
    public static void getDynamicList(Result result, int type, int page) {
        UserInfoWithToken user = UserUtil.getLoginUser();
        String userId = null;
        if(user != null) userId = user.getUserId();
        Post.newPost()
                .url(GET_DYNAMIC_LIST)
                .addParam("userId", userId)
                .addParam("type", type)
                .addParam("page", page)
                .result(result)
                .post();
    }

    /**
     * 发布动态
     * @param dynamic   动态
     */
    public static void releaseDynamic(Result result, Dynamic dynamic){
        Post.newPost()
                .url(RELEASE_DYNAMIC)
                .addParam("school", dynamic.getSchool())
                .addParam("content", dynamic.getContent())
                .addParam("imageNum", dynamic.getImageNum())
                .addParam("imageUrl", dynamic.getImageUrl())
                .addParam("lookCount", dynamic.getLookCount())
                .addParam("likeCount", dynamic.getLikeCount())
                .addParam("commentCount", dynamic.getCommentCount())
                .addParam("location", dynamic.getLocation())
                .addParam("latitude", dynamic.getLatitude())
                .addParam("longitude", dynamic.getLongitude())
                .addParam("anonymous", dynamic.isAnonymous())
                .result(result)
                .post();
    }

    /**
     * 喜欢动态
     * @param dynamicId 动态id
     * @param isLike    喜欢
     */
    public static void dynamicLike(Result result, int dynamicId, boolean isLike){
        Post.newPost()
                .url(DYNAMIC_LIKE)
                .addParam("dynamicId", dynamicId)
                .addParam("isLike", isLike)
                .result(result)
                .post();
    }

    /**
     * 获取评论列表
     * @param dynamicId 动态编号
     * @param page      页数
     */
    public static void getCommentList(Result result, int dynamicId, String order, int page){
        Post.newPost()
                .url(GET_COMMENT_LIST)
                .addParam("dynamicId", dynamicId)
                .addParam("order", order)
                .addParam("page", page)
                .result(result)
                .post();
    }

    public static void getReplyList(Result result, int parentId, String order, int page){
        Post.newPost()
                .url(GET_REPLY_LIST)
                .addParam("parentId", parentId)
                .addParam("order", order)
                .addParam("page", page)
                .result(result)
                .post();
    }

    /**
     * 发布评论
     * @param result
     * @param dynamicId
     * @param comment
     */
    public static void comment(Result result, int dynamicId, Comment comment){
        Post.newPost()
                .url(COMMENT)
                .addParam("dynamicId", dynamicId)
                .addParam("parentId", comment.getParentId())
                .addParam("fromId", comment.getFromId())
                .addParam("toName", comment.getToName())
                .addParam("comment", comment.getComment())
                .addParam("image", comment.getImage())
                .result(result)
                .post();
    }

    /**
     * 删除动态
     * @param result
     * @param dynamicId
     */
    public static void delete(Result result, int dynamicId){
        Post.newPost()
                .url(DELETE)
                .addParam("dynamicId", dynamicId)
                .result(result)
                .post();
    }

    /**
     * 获取喜欢列表
     * @param result
     * @param dynamicId
     * @param page
     */
    public static void getLikeList(Result result, int dynamicId, int page){
        Post.newPost()
                .url(GET_LIKE_LIST)
                .addParam("dynamicId", dynamicId)
                .addParam("page", page)
                .result(result)
                .post();
    }

}
