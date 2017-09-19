package server.api.client;

import android.util.Log;

import com.edu.schooltask.beans.TaskUploadKey;
import com.edu.schooltask.beans.UploadKey;
import com.edu.schooltask.utils.StringUtil;
import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import server.api.Post;
import server.api.event.UploadBGEvent;
import server.api.event.UploadHeadEvent;
import server.api.event.UploadImageEvent;
import server.api.result.Result;

import static server.api.SchoolTask.IMAGE;
import static server.api.SchoolTask.SERVER;

/**
 * Created by 夜夜通宵 on 2017/9/12.
 */

public class QiniuClient {
    private final static String GET_TASK_UPLOAD_KEY = SERVER + "qiniu/getTaskUploadKey";
    private final static String GET_HEAD_UPLOAD_KEY = SERVER + "qiniu/getHeadUploadKey";
    private final static String GET_BG_UPLOAD_KEY = SERVER + "qiniu/getBGUploadKey";
    private final static String GET_UPLOAD_KEY = SERVER + "qiniu/getUploadKey";

    private static Configuration config = new Configuration.Builder()
            .zone(Zone.zone2)
            .responseTimeout(20)
            .build();
    private static UploadManager uploadManager = new UploadManager(config);

    public static String getTaskImagePreUrl(String orderId){
        return "task/" + orderId + "/";
    }

    public static String getDynamicImagePreUrl(String imageUrl){
        return "dynamic/" + imageUrl + "/";
    }

    /**
     * 获取任务图片上传凭证
     * @param payPassword   支付密码
     */
    public static void getTaskUploadKey(Result result, String payPassword){
        Post.newPost()
                .url(GET_TASK_UPLOAD_KEY)
                .addParam("payPassword", payPassword)
                .result(result)
                .post();
    }

    /**
     * 获取头像上传凭证
     */
    public static void getHeadUploadKey(Result result){
        Post.newPost()
                .url(GET_HEAD_UPLOAD_KEY)
                .result(result)
                .post();
    }

    /**
     * 获取背景上传凭证
     */
    public static void getBGUploadKey(Result result){
        Post.newPost()
                .url(GET_BG_UPLOAD_KEY)
                .result(result)
                .post();
    }

    /**
     * 获取动态图片上传凭证
     */
    public static void getUploadKey(Result result){
        Post.newPost()
                .url(GET_UPLOAD_KEY)
                .result(result)
                .post();
    }

    /**
     * 按序上传多张图片
     * @param images    图片
     * @param preUrl    图片地址前缀
     * @param uploadKey       凭证
     * @param index     图片序号
     */
    private static void uploadImages(final List<File> images, final String preUrl, final String uploadKey, final int index){
        if(index == images.size() || images.size() == 0) return;
        File image = images.get(index);
        String url = preUrl + index + ".png";
        uploadManager.put(image, url, uploadKey, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                EventBus.getDefault().post(new UploadImageEvent(info.isOK(), index));
                if(info.isOK()){
                    uploadImages(images, preUrl, uploadKey, index + 1);
                }
            }
        }, null);
    }

    /**
     * 上传任务图片
     * @param taskUploadKey 凭证
     * @param images        图片
     */
    public static void uploadTaskImage(TaskUploadKey taskUploadKey, List<File> images){
        uploadImages(images, getTaskImagePreUrl(taskUploadKey.getOrderId()), taskUploadKey.getKey(), 0);
    }

    /**
     * 上传动态图片
     * @param uploadKey 凭证
     * @param imageUrl  图片地址
     * @param images    图片
     */
    public static void uploadDynamicImage(UploadKey uploadKey, String imageUrl, List<File> images){
        uploadImages(images, getDynamicImagePreUrl(imageUrl), uploadKey.getKey(), 0);
    }

    public static void uploadCommentImage(UploadKey uploadKey, String path, File image){
        uploadManager.put(image, path, uploadKey.getKey(), new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                EventBus.getDefault().post(new UploadImageEvent(info.isOK(), 0));
            }
        }, null);
    }

    /**
     * 上传头像
     * @param head      头像
     * @param uploadKey 凭证
     */
    public static void uploadHead(File head, UploadKey uploadKey){
        final String headPath = uploadKey.getPath();
        uploadManager.put(head, headPath, uploadKey.getKey(), new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                EventBus.getDefault().post(new UploadHeadEvent(info.isOK(), IMAGE + headPath));
            }
        }, null);
    }

    /**
     * 上传背景
     * @param bg        背景
     * @param uploadKey 凭证
     */
    public static void uploadBG(File bg, UploadKey uploadKey){
        final String bgPath = uploadKey.getPath();
        uploadManager.put(bg, bgPath, uploadKey.getKey(), new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {
                EventBus.getDefault().post(new UploadBGEvent(info.isOK(), IMAGE + bgPath));
            }
        }, null);
    }
}
