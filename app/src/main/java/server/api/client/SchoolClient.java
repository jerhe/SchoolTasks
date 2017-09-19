package server.api.client;

import server.api.Post;
import server.api.result.Result;

import static server.api.SchoolTask.SERVER;

/**
 * Created by 夜夜通宵 on 2017/9/12.
 */

public class SchoolClient {
    private final static String GET_SCHOOL = SERVER + "school/getSchoolList";

    /**
     * 获取所有学校列表
     */
    public static void getSchool(Result result){
        Post.newPost()
                .url(GET_SCHOOL)
                .result(result)
                .post();
    }
}
