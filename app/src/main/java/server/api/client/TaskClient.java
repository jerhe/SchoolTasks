package server.api.client;

import com.edu.schooltask.beans.Voucher;
import com.edu.schooltask.beans.Comment;
import com.edu.schooltask.beans.task.TaskPostBody;

import java.math.BigDecimal;

import server.api.Post;
import server.api.result.Result;

import static server.api.SchoolTask.SERVER;

/**
 * Created by 夜夜通宵 on 2017/9/12.
 */

public class TaskClient {
    private final static String RELEASE_TASK = SERVER + "task/release";
    private final static String GET_USER_ORDER_LIST = SERVER + "task/getUserOrderList";
    private final static String GET_TASK_LIST = SERVER + "task/getTaskList";
    private final static String ACCEPT_TASK = SERVER + "task/acceptTask";
    private final static String GET_TASK_INFO = SERVER + "task/getTaskInfo";
    private final static String GET_ORDER_INFO = SERVER + "task/getOrderInfo";
    private final static String CHANGE_ORDER_STATE = SERVER + "task/changeOrderState";
    private final static String TASK_COMMENT = SERVER + "task/comment";
    private final static String GET_TASK_COMMENT = SERVER + "task/getCommentList";
    private final static String GET_REPLY_LIST = SERVER + "task/getReplyList";
    private final static String SEARCH_TASK = SERVER + "task/searchTask";
    private final static String UPDATE_TASK_INFO = SERVER + "task/updateTaskInfo";


    /**
     * 发布任务
     * @param task  任务
     */
    public static void releaseTask(Result result, TaskPostBody task){
        Voucher voucher = task.getVoucher();
        Post.newPost()
                .url(RELEASE_TASK)
                .addParam("orderId", task.getOrderId())
                .addParam("school", task.getSchool())
                .addParam("description", task.getDescription())
                .addParam("content",task.getContent())
                .addParam("hideContent",task.getHideContent())
                .addParam("cost", task.getCost())
                .addParam("reward", task.getReward())
                .addParam("limitTime", task.getLimitTime())
                .addParam("payPassword", task.getPayPwd())
                .addParam("voucherId", voucher == null ? 0 : voucher.getId())
                .addParam("voucher", voucher == null ? new BigDecimal(0) : voucher.getMoney())
                .addParam("imageNum", task.getImageNum())
                .result(result)
                .post();
    }

    /**
     * 获取用户订单
     * @param type  类型
     * @param state 状态
     * @param sort  排序
     * @param page  页数
     */
    public static void getUserOrderList(Result result, int type, int state, int sort, int page){
        Post.newPost()
                .url(GET_USER_ORDER_LIST)
                .addParam("type", type)
                .addParam("state", state)
                .addParam("sort", sort)
                .addParam("page", page)
                .result(result)
                .post();
    }

    /**
     * 获取任务列表
     * @param school    学校
     * @param page      页数
     */
    public static void getTaskList(Result result, String school, int page){
        Post.newPost()
                .url(GET_TASK_LIST)
                .addParam("school",school)
                .addParam("page",page+"")
                .result(result)
                .post();
    }

    /**
     * 接受任务
     * @param orderId       订单号
     * @param payPassword   支付密码
     */
    public static void acceptTask(Result result, String orderId, String payPassword){
        Post.newPost()
                .url(ACCEPT_TASK)
                .addParam("orderId", orderId)
                .addParam("payPassword", payPassword)
                .result(result)
                .post();
    }

    /**
     * 获取任务信息
     * @param orderId   订单号
     */
    public static void getTaskInfo(Result result, String orderId){
        Post.newPost()
                .url(GET_TASK_INFO)
                .addParam("orderId", orderId)
                .result(result)
                .post();
    }

    /**
     * 获取订单信息
     * @param orderId   订单号
     */
    public static void getOrderInfo(Result result, String orderId){
        Post.newPost()
                .url(GET_ORDER_INFO)
                .addParam("orderId", orderId)
                .result(result)
                .post();
    }

    /**
     * 改变订单状态
     * @param orderId       订单号
     * @param state         状态
     * @param payPassword   支付密码
     */
    public static void changeTaskOrderState(Result result, String orderId, int state, String payPassword){
        Post.newPost()
                .url(CHANGE_ORDER_STATE)
                .addParam("orderId", orderId)
                .addParam("state", state)
                .addParam("payPassword", payPassword)
                .result(result)
                .post();
    }

    /**
     * 发送评论
     * @param result
     * @param orderId
     * @param comment
     */
    public static void comment(Result result, String orderId, Comment comment){
        Post.newPost()
                .url(TASK_COMMENT)
                .addParam("orderId", orderId)
                .addParam("parentId", comment.getParentId())
                .addParam("fromId", comment.getFromId())
                .addParam("toName", comment.getToName())
                .addParam("comment", comment.getComment())
                .addParam("image", comment.getImage())
                .result(result)
                .post();
    }

    /**
     * 获取任务评论
     * @param orderId   订单号
     * @param order     排序
     * @param page      页数
     */
    public static void getTaskComment(Result result, String orderId, String order, int page){
        Post.newPost()
                .url(GET_TASK_COMMENT)
                .addParam("orderId", orderId)
                .addParam("order", order)
                .addParam("page", page)
                .result(result)
                .post();
    }

    /**
     * 获取任务评论回复列表
     * @param parentId  评论号
     * @param page      页数
     */
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
     * 搜索任务
     * @param school        学校
     * @param description   类别
     * @param searchText    关键字
     * @param minCost       最低报酬
     * @param maxCost       最高报酬
     * @param page          页数
     */
    public static void searchTask(Result result, String school, String description, String searchText,
                                  BigDecimal minCost, BigDecimal maxCost, int page){
        Post.newPost()
                .url(SEARCH_TASK)
                .addParam("school", school)
                .addParam("description", description)
                .addParam("searchText", searchText)
                .addParam("minCost", minCost)
                .addParam("maxCost", maxCost)
                .addParam("page", page)
                .result(result)
                .post();
    }

    /**
     * 修改任务内容
     * @param orderId   订单号
     * @param content   内容
     */
    public static void updateTaskInfo(Result result, String orderId, String content){
        Post.newPost()
                .url(UPDATE_TASK_INFO)
                .addParam("orderId", orderId)
                .addParam("content", content)
                .result(result)
                .post();
    }
}
